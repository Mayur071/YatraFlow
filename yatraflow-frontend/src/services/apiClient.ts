
const CSRF_COOKIE_NAME = "XSRF-TOKEN";
const CSRF_HEADER_NAME = "X-XSRF-TOKEN";

let csrfTokenFromResponse: string | null = null;

export function setCsrfToken(token: string | null): void {
    csrfTokenFromResponse = token;
}

function getCookie(name: string): string | null {
    if (typeof document === "undefined") {
        return null;
    }

    const cookie = document.cookie
        .split("; ")
        .find((item) => item.startsWith(`${name}=`));

    if (!cookie) {
        return null;
    }

    const value = cookie.substring(name.length + 1);

    try {
        return decodeURIComponent(value);
    } catch {
        return value;
    }
}

export class ApiError extends Error {
    readonly status: number;
    readonly responseBody?: unknown;

    constructor(
        message: string,
        status: number,
        responseBody?: unknown
    ) {
        super(message);

        this.name = "ApiError";
        this.status = status;
        this.responseBody = responseBody;
    }
}

function extractErrorMessage(data: unknown, status: number): string {
    if (typeof data === "string" && data.trim()) {
        return data;
    }

    if (data && typeof data === "object") {
        const body = data as Record<string, unknown>;

        for (const key of ["message", "detail", "error"]) {
            if (typeof body[key] === "string") {
                return body[key];
            }
        }
    }

    return `Request failed with status ${status}`;
}


async function ensureCsrfToken(): Promise<string> {
    // First check whether the CSRF cookie already exists.
    const cookieToken = getCookie(CSRF_COOKIE_NAME);

    if (cookieToken) {
        csrfTokenFromResponse = cookieToken;
        return cookieToken;
    }

    // Reuse the token if it was already received.
    if (csrfTokenFromResponse) {
        return csrfTokenFromResponse;
    }

    // Request a fresh CSRF token from Spring Security.
    const response = await fetch("/api/v1/auth/csrf", {
        method: "GET",
        credentials: "include",
        headers: {
            Accept: "application/json",
        },
    });

    const data: unknown = await response.json().catch(() => null);

    if (!response.ok) {
        throw new ApiError(
            extractErrorMessage(data, response.status),
            response.status,
            data
        );
    }

    const body = data as Record<string, unknown> | null;

    const token =
        body && typeof body.token === "string"
            ? body.token
            : getCookie(CSRF_COOKIE_NAME);

    if (!token) {
        throw new ApiError(
            "Unable to initialize CSRF protection.",
            response.status,
            data
        );
    }

    csrfTokenFromResponse = token;

    return token;
}


export async function apiRequest<T>(
    endpoint: string,
    options: RequestInit = {}
): Promise<T> {
    const method = (options.method ?? "GET").toUpperCase();
    const headers = new Headers(options.headers);

    headers.set("Accept", "application/json");

    // Automatically attach CSRF token to state-changing requests.
    const safeMethods = ["GET", "HEAD", "OPTIONS", "TRACE"];

    if (!safeMethods.includes(method)) {
        const csrfToken = await ensureCsrfToken();

        headers.set(CSRF_HEADER_NAME, csrfToken);
    }

    const response = await fetch(endpoint, {
        ...options,
        method,
        headers,
        credentials: "include",
    });

    let responseData: unknown = null;

    if (response.status !== 204) {
        const contentType = response.headers.get("content-type") ?? "";

        if (contentType.includes("json")) {
            responseData = await response.json().catch(() => null);
        } else {
            responseData = await response.text().catch(() => "");
        }
    }

    if (!response.ok) {
        throw new ApiError(
            extractErrorMessage(responseData, response.status),
            response.status,
            responseData
        );
    }

    return responseData as T;
}
