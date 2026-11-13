
import { apiRequest, setCsrfToken } from "./apiClient.ts";

// Base authentication API path
const AUTH_API = "/api/v1/auth";

// =========================================================
// REQUEST TYPES
// =========================================================

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

export interface EmailOtpVerificationRequest {
    email: string;
    otp: string;
}

export interface ResendVerificationOtpRequest {
    email: string;
}

// =========================================================
// RESPONSE TYPES
// =========================================================

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

// =========================================================
// 1. REGISTER
// =========================================================

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

// =========================================================
// 2. LOGIN
// =========================================================

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

// =========================================================
// 3. GET CSRF TOKEN
// =========================================================

export async function getCsrfToken(): Promise<CsrfTokenResponse> {

    const response = await apiRequest<CsrfTokenResponse>(
        `${AUTH_API}/csrf`
    );

    setCsrfToken(response.token);

    return response;
}

// =========================================================
// 4. REFRESH ACCESS TOKEN
// =========================================================

export function refreshAccessToken(): Promise<LoginResponse> {

    return apiRequest<LoginResponse>(`${AUTH_API}/refresh-token`, {
        method: "POST",
    });
}

// =========================================================
// 5. LOGOUT
// =========================================================

export async function logoutUser(): Promise<void> {

    await getCsrfToken();

    await apiRequest<void>(`${AUTH_API}/logout`, {
        method: "POST",
    });

    setCsrfToken(null);
}

// =========================================================
// 6. VERIFY EMAIL USING OTP
// =========================================================

export async function verifyEmailOtp(
    payload: EmailOtpVerificationRequest
): Promise<AuthMessageResponse> {

    await getCsrfToken();

    return apiRequest<AuthMessageResponse>(
        `${AUTH_API}/verify-email-otp`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
        }
    );
}

// =========================================================
// 7. RESEND EMAIL VERIFICATION OTP
// =========================================================

export async function resendVerificationOtp(
    payload: ResendVerificationOtpRequest
): Promise<AuthMessageResponse> {

    await getCsrfToken();

    return apiRequest<AuthMessageResponse>(
        `${AUTH_API}/resend-verification-otp`,
        {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(payload),
        }
    );
}
