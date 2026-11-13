
import {
    useEffect,
    useRef,
    useState,
    type FormEvent,
    type KeyboardEvent,
    type ClipboardEvent,
} from "react";

import { Link, useLocation } from "react-router-dom";

import {
    ArrowLeft,
    ArrowRight,
    CheckCircle2,
    Clock3,
    Mail,
    RefreshCw,
    ShieldCheck,
} from "lucide-react";

import {
    verifyEmailOtp,
    resendVerificationOtp,
} from "../../services/authServices.ts";

// =========================================================
// TYPES & CONSTANTS
// =========================================================

interface VerificationRouteState {
    email?: string;
}

const OTP_LENGTH = 6;
const OTP_EXPIRY_SECONDS = 300;
const RESEND_COOLDOWN_SECONDS = 60;

type Feedback = {
    type: "error" | "info";
    message: string;
};

// =========================================================
// UTILITY
// =========================================================

const formatTime = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;

    return `${String(minutes).padStart(2, "0")}:${String(
        remainingSeconds
    ).padStart(2, "0")}`;
};

// =========================================================
// COMPONENT
// =========================================================

const EmailOtpVerificationPage = () => {

    const location = useLocation();

    const routeState =
        location.state as VerificationRouteState | null;

    const email = routeState?.email?.trim().toLowerCase() ?? "";

    // -----------------------------------------------------
    // STATE
    // -----------------------------------------------------

    const [otp, setOtp] = useState("");

    const [remainingSeconds, setRemainingSeconds] = useState(
        OTP_EXPIRY_SECONDS
    );

    const [resendRemainingSeconds, setResendRemainingSeconds] =
        useState(RESEND_COOLDOWN_SECONDS);

    const [feedback, setFeedback] = useState<Feedback | null>(null);

    const [isVerifying, setIsVerifying] = useState(false);

    const [isResending, setIsResending] = useState(false);

    const [isVerified, setIsVerified] = useState(false);

    const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

    // -----------------------------------------------------
    // DERIVED VALUES
    // -----------------------------------------------------

    const isExpired = remainingSeconds <= 0;

    const canResend = resendRemainingSeconds <= 0;

    const hasEmail = Boolean(email);

    const formattedEmail = email || "Email address unavailable";

    // -----------------------------------------------------
    // COUNTDOWN TIMER
    // -----------------------------------------------------

    useEffect(() => {

        if (isVerified) {
            return;
        }

        const intervalId = window.setInterval(() => {

            setRemainingSeconds((current) =>
                Math.max(0, current - 1)
            );

            setResendRemainingSeconds((current) =>
                Math.max(0, current - 1)
            );

        }, 1000);

        return () => window.clearInterval(intervalId);

    }, [isVerified]);

    // -----------------------------------------------------
    // HANDLE OTP INPUT
    // -----------------------------------------------------

    const handleOtpChange = (index: number, value: string) => {

        setFeedback(null);

        const digitsOnly = value.replace(/\D/g, "");

        const currentDigits = Array.from(
            { length: OTP_LENGTH },
            (_, position) => otp[position] ?? ""
        );

        if (!digitsOnly) {

            currentDigits[index] = "";

            setOtp(currentDigits.join(""));

            return;
        }

        const insertedDigits = digitsOnly.slice(
            0,
            OTP_LENGTH - index
        );

        insertedDigits.split("").forEach((digit, offset) => {
            currentDigits[index + offset] = digit;
        });

        setOtp(currentDigits.join(""));

        const nextIndex = Math.min(
            index + insertedDigits.length,
            OTP_LENGTH - 1
        );

        inputRefs.current[nextIndex]?.focus();
    };

    // -----------------------------------------------------
    // KEYBOARD NAVIGATION
    // -----------------------------------------------------

    const handleKeyDown = (
        index: number,
        event: KeyboardEvent<HTMLInputElement>
    ) => {

        if (
            event.key === "Backspace" &&
            !otp[index] &&
            index > 0
        ) {
            inputRefs.current[index - 1]?.focus();
        }

        if (event.key === "ArrowLeft" && index > 0) {

            event.preventDefault();

            inputRefs.current[index - 1]?.focus();
        }

        if (
            event.key === "ArrowRight" &&
            index < OTP_LENGTH - 1
        ) {

            event.preventDefault();

            inputRefs.current[index + 1]?.focus();
        }
    };

    // -----------------------------------------------------
    // PASTE COMPLETE OTP
    // -----------------------------------------------------

    const handleOtpPaste = (
        event: ClipboardEvent<HTMLInputElement>
    ) => {

        event.preventDefault();

        const pastedDigits = event.clipboardData
            .getData("text")
            .replace(/\D/g, "")
            .slice(0, OTP_LENGTH);

        if (!pastedDigits) return;

        setOtp(pastedDigits);

        setFeedback(null);

        const nextIndex = Math.min(
            pastedDigits.length,
            OTP_LENGTH - 1
        );

        inputRefs.current[nextIndex]?.focus();
    };

    // =====================================================
    // VERIFY EMAIL OTP API
    // =====================================================

    const handleSubmit = async (
        event: FormEvent<HTMLFormElement>
    ) => {

        event.preventDefault();

        if (isVerifying || isVerified) {
            return;
        }

        setFeedback(null);

        if (!hasEmail) {

            setFeedback({
                type: "error",
                message:
                    "Registration email is missing. Please sign up again.",
            });

            return;
        }

        if (isExpired) {

            setFeedback({
                type: "error",
                message:
                    "Your verification code has expired. Please request a new OTP.",
            });

            return;
        }

        if (otp.length !== OTP_LENGTH) {

            setFeedback({
                type: "error",
                message:
                    "Please enter all 6 digits of your verification code.",
            });

            return;
        }

        setIsVerifying(true);

        try {

            const response = await verifyEmailOtp({
                email,
                otp,
            });

            setIsVerified(true);

            setFeedback({
                type: "info",
                message:
                    response.message ??
                    "Your email has been verified successfully.",
            });

        } catch (error) {

            setFeedback({
                type: "error",
                message:
                    error instanceof Error
                        ? error.message
                        : "OTP verification failed. Please try again.",
            });

            setOtp("");

            inputRefs.current[0]?.focus();

        } finally {

            setIsVerifying(false);
        }
    };

    // =====================================================
    // RESEND OTP API
    // =====================================================

    const handleResend = async () => {

        if (
            !canResend ||
            isResending ||
            isVerifying ||
            isVerified ||
            !hasEmail
        ) {
            return;
        }

        setFeedback(null);

        setIsResending(true);

        try {

            const response = await resendVerificationOtp({
                email,
            });

            // Clear previous OTP.
            setOtp("");

            // Reset countdown after successful resend.
            setRemainingSeconds(OTP_EXPIRY_SECONDS);

            setResendRemainingSeconds(
                RESEND_COOLDOWN_SECONDS
            );

            inputRefs.current[0]?.focus();

            setFeedback({
                type: "info",
                message:
                    response.message ??
                    "A new verification code has been sent to your email.",
            });

        } catch (error) {

            setFeedback({
                type: "error",
                message:
                    error instanceof Error
                        ? error.message
                        : "Unable to resend OTP. Please try again.",
            });

        } finally {

            setIsResending(false);
        }
    };

    // =====================================================
    // SUCCESS SCREEN
    // =====================================================

    if (isVerified) {

        return (
            <div className="py-10 text-center">

                <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-brand-teal/10 text-brand-teal">
                    <CheckCircle2 size={36} />
                </div>

                <p className="mt-6 text-sm font-semibold tracking-wide text-brand-teal">
                    VERIFICATION COMPLETE
                </p>

                <h1 className="mt-2 font-brand text-3xl font-extrabold tracking-tight text-text-primary">
                    Email verified!
                </h1>

                <p className="mt-3 text-sm leading-6 text-text-secondary">
                    {feedback?.message ??
                        "Your YatraFlow account has been successfully created."}
                </p>

                <div className="mt-6 rounded-xl border border-border bg-surface-soft p-4">
                    <div className="flex items-center justify-center gap-2 text-brand-teal">
                        <ShieldCheck size={19} />

                        <span className="text-sm font-semibold">
                            Your email is now verified
                        </span>
                    </div>

                    <p className="mt-2 break-all text-sm text-text-secondary">
                        {email}
                    </p>
                </div>

                <Link
                    to="/sign-in"
                    className="mt-8 inline-flex min-h-12 w-full items-center justify-center gap-2 rounded-xl bg-brand-primary px-5 py-3 text-sm font-bold text-white shadow-lg shadow-brand-primary/20 transition-all hover:-translate-y-0.5 hover:bg-brand-hover hover:shadow-xl focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-primary"
                >
                    Continue to Sign In

                    <ArrowRight size={17} />
                </Link>

            </div>
        );
    }

    // =====================================================
    // PROGRESS & FEEDBACK STYLING
    // =====================================================

    const progressPercentage =
        (remainingSeconds / OTP_EXPIRY_SECONDS) * 100;

    const feedbackClassName =
        feedback?.type === "error"
            ? "border-danger/30 bg-danger/10 text-danger"
            : "border-brand-primary/30 bg-brand-primary/10 text-text-primary";

    // =====================================================
    // MAIN OTP SCREEN
    // =====================================================

    return (
        <div>

            {/* Back Navigation */}
            <Link
                to="/sign-up"
                className="mb-7 inline-flex items-center gap-2 text-sm font-semibold text-text-secondary transition-colors hover:text-brand-primary"
            >
                <ArrowLeft size={16} />

                Back to Sign Up
            </Link>

            {/* Heading */}
            <div className="mb-8">

                <div className="mb-5 flex h-14 w-14 items-center justify-center rounded-2xl border border-brand-primary/15 bg-brand-primary/10 text-brand-primary">
                    <Mail size={26} strokeWidth={1.8} />
                </div>

                <p className="text-sm font-semibold tracking-wide text-brand-primary">
                    ONE LAST STEP
                </p>

                <h1 className="mt-2 font-brand text-3xl font-extrabold tracking-tight text-text-primary sm:text-4xl">
                    Verify your email
                </h1>

                <p className="mt-3 text-sm leading-6 text-text-secondary">
                    We've sent a 6-digit verification code to your email
                    address. Enter the code below to activate your YatraFlow
                    account.
                </p>

                {/* Registered Email */}
                <div className="mt-5 flex items-center gap-3 rounded-xl border border-border bg-surface-soft p-3.5">

                    <Mail
                        size={19}
                        className="shrink-0 text-brand-primary"
                    />

                    <div className="min-w-0 flex-1">

                        <p className="text-xs text-text-muted">
                            Verification email sent to
                        </p>

                        <p className="mt-0.5 truncate text-sm font-semibold text-text-primary">
                            {formattedEmail}
                        </p>

                    </div>

                </div>

            </div>

            {/* Missing Email Warning */}
            {!hasEmail && (
                <div
                    role="alert"
                    className="mb-5 rounded-xl border border-danger/30 bg-danger/10 px-4 py-3 text-sm text-danger"
                >
                    Registration email is missing. Please return to Sign Up
                    and complete registration again.
                </div>
            )}

            {/* OTP Form */}
            <form
                onSubmit={handleSubmit}
                className="space-y-6"
            >

                <div>

                    <label className="mb-3 block text-sm font-semibold text-text-primary">
                        Enter verification code
                    </label>

                    <div className="grid grid-cols-6 gap-2.5 sm:gap-3">

                        {Array.from({ length: OTP_LENGTH }).map(
                            (_, index) => (

                                <input
                                    key={index}
                                    ref={(element) => {
                                        inputRefs.current[index] = element;
                                    }}
                                    type="text"
                                    inputMode="numeric"
                                    autoComplete={
                                        index === 0
                                            ? "one-time-code"
                                            : "off"
                                    }
                                    aria-label={`OTP digit ${index + 1}`}
                                    maxLength={OTP_LENGTH}
                                    value={otp[index] ?? ""}
                                    disabled={
                                        isExpired ||
                                        isVerifying ||
                                        isVerified ||
                                        !hasEmail
                                    }
                                    required
                                    onChange={(event) =>
                                        handleOtpChange(
                                            index,
                                            event.currentTarget.value
                                        )
                                    }
                                    onKeyDown={(event) =>
                                        handleKeyDown(index, event)
                                    }
                                    onPaste={handleOtpPaste}
                                    onFocus={(event) =>
                                        event.currentTarget.select()
                                    }
                                    className="h-12 min-w-0 rounded-xl border border-border bg-surface px-1 text-center text-xl font-bold tabular-nums text-text-primary outline-none transition-all placeholder:text-text-muted focus:border-brand-primary focus:ring-2 focus:ring-brand-primary/15 disabled:cursor-not-allowed disabled:opacity-50 sm:h-14 sm:text-2xl"
                                />

                            )
                        )}

                    </div>

                </div>

                {/* Expiry Countdown */}
                <div className="rounded-xl border border-border bg-surface-soft p-4">

                    <div className="flex items-center justify-between gap-3">

                        <div className="flex items-center gap-2">

                            <Clock3
                                size={18}
                                className="text-text-secondary"
                            />

                            <span className="text-sm font-semibold text-text-primary">
                                {isExpired
                                    ? "Verification code expired"
                                    : "Code expires in"}
                            </span>

                        </div>

                        <span
                            className={`text-lg font-bold tabular-nums ${
                                isExpired
                                    ? "text-danger"
                                    : "text-brand-primary"
                            }`}
                        >
                            {formatTime(remainingSeconds)}
                        </span>

                    </div>

                    <div className="mt-3 h-1.5 overflow-hidden rounded-full bg-border">

                        <div
                            className={`h-full rounded-full transition-[width] duration-1000 ${
                                isExpired
                                    ? "bg-danger"
                                    : "bg-brand-primary"
                            }`}
                            style={{
                                width: `${progressPercentage}%`,
                            }}
                        />

                    </div>

                    <p className="mt-2 text-xs leading-5 text-text-muted">
                        {isExpired
                            ? "Your code is no longer valid. Request a new verification code."
                            : "For security, this verification code is valid for 5 minutes only."}
                    </p>

                </div>

                {/* API Feedback */}
                {feedback && (
                    <div
                        role="alert"
                        aria-live="polite"
                        className={`rounded-xl border px-4 py-3 text-sm leading-5 ${feedbackClassName}`}
                    >
                        {feedback.message}
                    </div>
                )}

                {/* Verify Button */}
                <button
                    type="submit"
                    disabled={
                        otp.length !== OTP_LENGTH ||
                        isExpired ||
                        isVerifying ||
                        isResending ||
                        !hasEmail
                    }
                    className="group inline-flex min-h-12 w-full items-center justify-center gap-2 rounded-xl bg-brand-primary px-5 py-3 text-sm font-bold text-white shadow-lg shadow-brand-primary/20 transition-all hover:-translate-y-0.5 hover:bg-brand-hover hover:shadow-xl focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-primary disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:translate-y-0"
                >

                    {isVerifying
                        ? "Verifying..."
                        : "Verify Email"}

                    {!isVerifying && (
                        <ArrowRight
                            size={17}
                            className="transition-transform group-hover:translate-x-1"
                        />
                    )}

                </button>

            </form>

            {/* Resend OTP */}
            <div className="mt-7 border-t border-border pt-6">

                <div className="text-center">

                    <p className="text-sm text-text-secondary">
                        Didn't receive the code?
                    </p>

                    <button
                        type="button"
                        onClick={handleResend}
                        disabled={
                            !canResend ||
                            isResending ||
                            isVerifying ||
                            isVerified ||
                            !hasEmail
                        }
                        className="mt-2 inline-flex items-center gap-2 text-sm font-bold text-brand-primary transition-colors hover:text-brand-hover disabled:cursor-not-allowed disabled:text-text-muted"
                    >

                        <RefreshCw
                            size={15}
                            className={
                                isResending
                                    ? "animate-spin"
                                    : ""
                            }
                        />

                        {isResending
                            ? "Sending OTP..."
                            : canResend
                                ? "Resend verification code"
                                : `Resend OTP in ${formatTime(
                                    resendRemainingSeconds
                                )}`}

                    </button>

                </div>

                {/* Security Notice */}
                <div className="mt-6 flex items-start gap-2 rounded-lg bg-brand-primary/5 p-3">

                    <ShieldCheck
                        size={17}
                        className="mt-0.5 shrink-0 text-brand-teal"
                    />

                    <p className="text-xs leading-5 text-text-secondary">
                        Never share your verification code with anyone.
                        YatraFlow will never ask you to disclose your OTP.
                    </p>

                </div>

            </div>

        </div>
    );
};

export default EmailOtpVerificationPage;
