
import { apiRequest, setCsrfToken } from "./apiClient.ts";

// Base authentication API path
const AUTH_API = "/api/v1/auth";

// Request types
export interface RegisterRequest {
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    password: string;
    confirmPassword: string;
}

export interface LoginRequest {
    email: string;
    password: string;
}

// Response types
export interface RegisterResponse {
    email: string;
    message?: string;
}

export interface LoginResponse {
    userId: number | string;
    firstName: string;
    lastName: string;
    email: string;
    roles: string[];
    tokenType: string;
    expiresIn: number;
    refreshExpiresIn: number;
}

export interface AuthMessageResponse {
    message?: string;
    email?: string;
}

export interface CsrfTokenResponse {
    token: string;
}

// 1. Register
export function registerUser(
    payload: RegisterRequest
): Promise<RegisterResponse> {
    return apiRequest<RegisterResponse>(`${AUTH_API}/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
    });
}

// 2. Login
export function loginUser(
    payload: LoginRequest
): Promise<LoginResponse> {
    return apiRequest<LoginResponse>(`${AUTH_API}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
    });
}

// 3. Get CSRF Token
export async function getCsrfToken(): Promise<CsrfTokenResponse> {
    const response = await apiRequest<CsrfTokenResponse>(
        `${AUTH_API}/csrf`
    );

    setCsrfToken(response.token);

    return response;
}

// 4. Refresh Access Token
export function refreshAccessToken(): Promise<LoginResponse> {
    return apiRequest<LoginResponse>(`${AUTH_API}/refresh-token`, {
        method: "POST",
    });
}

// 5. Logout
export async function logoutUser(): Promise<void> {
    await getCsrfToken();

    await apiRequest<void>(`${AUTH_API}/logout`, {
        method: "POST",
    });

    setCsrfToken(null);
}

// 6. Verify Email
export function verifyEmail(
    token: string
): Promise<AuthMessageResponse> {
    const params = new URLSearchParams({ token });

    return apiRequest<AuthMessageResponse>(
        `${AUTH_API}/verify-email?${params.toString()}`
    );
}

// 7. Resend Verification Email
export async function resendVerificationEmail(
    email: string
): Promise<AuthMessageResponse> {
    await getCsrfToken();

    return apiRequest<AuthMessageResponse>(
        `${AUTH_API}/resend-verification`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ email }),
        }
    );
}
