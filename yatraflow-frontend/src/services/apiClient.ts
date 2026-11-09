
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
        const csrfToken =
            getCookie(CSRF_COOKIE_NAME) ?? csrfTokenFromResponse;

        if (csrfToken && !headers.has(CSRF_HEADER_NAME)) {
            headers.set(CSRF_HEADER_NAME, csrfToken);
        }
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
