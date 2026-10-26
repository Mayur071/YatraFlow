
import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import {
    UserRound,
    Mail,
    Phone,
    LockKeyhole,
    Eye,
    EyeOff,
    ArrowRight,
} from "lucide-react";

import { FcGoogle } from "react-icons/fc";
import { FaGithub } from "react-icons/fa6";

type FieldName =
    | "firstName"
    | "lastName"
    | "email"
    | "phoneNumber"
    | "password"
    | "confirmPassword";

type RegisterFormValues = Record<FieldName, string>;

type RegisterFormErrors = Partial<Record<FieldName, string>>;

const initialValues: RegisterFormValues = {
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    password: "",
    confirmPassword: "",
};

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const phonePattern = /^[6-9]\d{9}$/;

const passwordPattern =
    /^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@#$%^&+=!]).{8,20}$/;

const validateField = (
    field: FieldName,
    values: RegisterFormValues,
): string | undefined => {
    const value = values[field];
    const trimmedValue = value.trim();

    switch (field) {
        case "firstName":
            if (!trimmedValue) return "First name is required.";
            if (trimmedValue.length < 2 || trimmedValue.length > 50) {
                return "First name must be between 2 and 50 characters.";
            }
            return undefined;

        case "lastName":
            if (!trimmedValue) return "Last name is required.";
            if (trimmedValue.length < 2 || trimmedValue.length > 50) {
                return "Last name must be between 2 and 50 characters.";
            }
            return undefined;

        case "email":
            if (!trimmedValue) return "Email address is required.";
            if (value.length > 150) {
                return "Email cannot exceed 150 characters.";
            }
            if (!emailPattern.test(trimmedValue)) {
                return "Please enter a valid email address.";
            }
            return undefined;

        case "phoneNumber":
            if (!trimmedValue) return "Mobile number is required.";
            if (!phonePattern.test(trimmedValue)) {
                return "Enter a valid 10-digit mobile number starting with 6–9.";
            }
            return undefined;

        case "password":
            if (!value) return "Password is required.";
            if (value.length < 8 || value.length > 20) {
                return "Password must be between 8 and 20 characters.";
            }
            if (!passwordPattern.test(value)) {
                return "Include uppercase, lowercase, a number and an allowed special character.";
            }
            return undefined;

        case "confirmPassword":
            if (!value) return "Please confirm your password.";
            if (value !== values.password) {
                return "Passwords do not match.";
            }
            return undefined;
    }
};

const fieldClassName = (hasError: boolean) =>
    [
        "flex min-h-11 items-center gap-3 rounded-xl border bg-surface px-3.5",
        "transition-colors focus-within:ring-2 focus-within:ring-brand-primary/15",
        hasError
            ? "border-danger focus-within:border-danger"
            : "border-border focus-within:border-brand-primary",
    ].join(" ");

const inputClassName =
    "w-full min-w-0 bg-transparent py-2.5 text-sm text-text-primary outline-none placeholder:text-text-muted";

const RegisterPage = () => {
    const [values, setValues] =
        useState<RegisterFormValues>(initialValues);

    const [errors, setErrors] = useState<RegisterFormErrors>({});
    const [touched, setTouched] =
        useState<Partial<Record<FieldName, boolean>>>({});

    const [submitted, setSubmitted] = useState(false);
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [formStatus, setFormStatus] = useState("");

    const updateField = (field: FieldName, value: string) => {
        const nextValues = { ...values, [field]: value };

        setValues(nextValues);
        setFormStatus("");

        if (touched[field] || submitted) {
            setErrors((previous) => ({
                ...previous,
                [field]: validateField(field, nextValues),
            }));
        }

        // If password changes, revalidate its confirmation too.
        if (
            field === "password" &&
            (touched.confirmPassword || submitted)
        ) {
            setErrors((previous) => ({
                ...previous,
                confirmPassword: validateField("confirmPassword", nextValues),
            }));
        }
    };

    const markTouched = (field: FieldName) => {
        setTouched((previous) => ({ ...previous, [field]: true }));

        setErrors((previous) => ({
            ...previous,
            [field]: validateField(field, values),
        }));
    };

    const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        setSubmitted(true);
        setFormStatus("");

        const fields = Object.keys(initialValues) as FieldName[];

        const nextErrors: RegisterFormErrors = {};

        fields.forEach((field) => {
            nextErrors[field] = validateField(field, values);
        });

        setErrors(nextErrors);

        setTouched({
            firstName: true,
            lastName: true,
            email: true,
            phoneNumber: true,
            password: true,
            confirmPassword: true,
        });

        if (Object.values(nextErrors).some(Boolean)) {
            return;
        }

        // UI validation only. The backend API is not connected yet.
        setFormStatus(
            "All fields are valid. No account has been created yet because registration is not connected to the backend.",
        );
    };

    const renderError = (field: FieldName) => {
        const message = errors[field];

        if (!message) return null;

        return (
            <p
                id={`${field}-error`}
                role="alert"
                className="mt-1.5 text-xs leading-5 text-danger"
            >
                {message}
            </p>
        );
    };

    return (
        <div>
            {/* Heading */}
            <div className="mb-6">
                <p className="text-sm font-semibold tracking-wide text-brand-primary">
                    START YOUR JOURNEY
                </p>

                <h1 className="mt-2 font-brand text-3xl font-extrabold tracking-tight text-text-primary sm:text-4xl">
                    Create your account
                </h1>

                <p className="mt-3 text-sm leading-6 text-text-secondary">
                    Join YatraFlow and make your next trip memorable.
                </p>
            </div>

            {/* OAuth placeholders — not active yet */}
            {/* OAuth buttons — not active yet */}
            <div className="grid grid-cols-2 gap-3">
                <button
                    type="button"
                    disabled
                    title="Google sign-up is coming soon"
                    className="inline-flex min-h-11 cursor-not-allowed items-center justify-center gap-2 rounded-xl border border-border bg-surface px-3 text-sm font-semibold text-text-secondary opacity-70"
                >
                    <FcGoogle size={18} aria-hidden="true" />
                    <span>Google</span>
                </button>

                <button
                    type="button"
                    disabled
                    title="GitHub sign-up is coming soon"
                    className="inline-flex min-h-11 cursor-not-allowed items-center justify-center gap-2 rounded-xl border border-border bg-surface px-3 text-sm font-semibold text-text-secondary opacity-70"
                >
                    <FaGithub size={18} aria-hidden="true" />
                    <span>GitHub</span>
                </button>
            </div>

            <div className="my-5 flex items-center gap-4">
                <div className="h-px flex-1 bg-border" />
                <span className="text-xs font-medium uppercase tracking-wider text-text-muted">
          Or sign up with email
        </span>
                <div className="h-px flex-1 bg-border" />
            </div>

            <form noValidate onSubmit={handleSubmit} className="space-y-4">
                {/* First name and last name */}
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                    <div>
                        <label
                            htmlFor="firstName"
                            className="mb-2 block text-sm font-semibold text-text-primary"
                        >
                            First name
                        </label>

                        <div className={fieldClassName(Boolean(errors.firstName))}>
                            <UserRound
                                size={17}
                                className="shrink-0 text-text-muted"
                                aria-hidden="true"
                            />

                            <input
                                id="firstName"
                                name="firstName"
                                type="text"
                                autoComplete="given-name"
                                placeholder="First name"
                                value={values.firstName}
                                onChange={(event) =>
                                    updateField("firstName", event.currentTarget.value)
                                }
                                onBlur={() => markTouched("firstName")}
                                required
                                minLength={2}
                                maxLength={50}
                                aria-invalid={Boolean(errors.firstName)}
                                aria-describedby={
                                    errors.firstName ? "firstName-error" : undefined
                                }
                                className={inputClassName}
                            />
                        </div>

                        {renderError("firstName")}
                    </div>

                    <div>
                        <label
                            htmlFor="lastName"
                            className="mb-2 block text-sm font-semibold text-text-primary"
                        >
                            Last name
                        </label>

                        <div className={fieldClassName(Boolean(errors.lastName))}>
                            <UserRound
                                size={17}
                                className="shrink-0 text-text-muted"
                                aria-hidden="true"
                            />

                            <input
                                id="lastName"
                                name="lastName"
                                type="text"
                                autoComplete="family-name"
                                placeholder="Last name"
                                value={values.lastName}
                                onChange={(event) =>
                                    updateField("lastName", event.currentTarget.value)
                                }
                                onBlur={() => markTouched("lastName")}
                                required
                                minLength={2}
                                maxLength={50}
                                aria-invalid={Boolean(errors.lastName)}
                                aria-describedby={
                                    errors.lastName ? "lastName-error" : undefined
                                }
                                className={inputClassName}
                            />
                        </div>

                        {renderError("lastName")}
                    </div>
                </div>

                {/* Email */}
                <div>
                    <label
                        htmlFor="email"
                        className="mb-2 block text-sm font-semibold text-text-primary"
                    >
                        Email address
                    </label>

                    <div className={fieldClassName(Boolean(errors.email))}>
                        <Mail
                            size={18}
                            className="shrink-0 text-text-muted"
                            aria-hidden="true"
                        />

                        <input
                            id="email"
                            name="email"
                            type="email"
                            autoComplete="email"
                            placeholder="you@example.com"
                            value={values.email}
                            onChange={(event) =>
                                updateField("email", event.currentTarget.value)
                            }
                            onBlur={() => markTouched("email")}
                            required
                            maxLength={150}
                            aria-invalid={Boolean(errors.email)}
                            aria-describedby={errors.email ? "email-error" : undefined}
                            className={inputClassName}
                        />
                    </div>

                    {renderError("email")}
                </div>

                {/* Phone number */}
                <div>
                    <label
                        htmlFor="phoneNumber"
                        className="mb-2 block text-sm font-semibold text-text-primary"
                    >
                        Mobile number
                    </label>

                    <div className={fieldClassName(Boolean(errors.phoneNumber))}>
                        <Phone
                            size={18}
                            className="shrink-0 text-text-muted"
                            aria-hidden="true"
                        />

                        <span className="shrink-0 border-r border-border pr-3 text-sm text-text-secondary">
              +91
            </span>

                        <input
                            id="phoneNumber"
                            name="phoneNumber"
                            type="tel"
                            autoComplete="tel-national"
                            inputMode="numeric"
                            placeholder="10-digit mobile number"
                            value={values.phoneNumber}
                            onChange={(event) =>
                                updateField("phoneNumber", event.currentTarget.value)
                            }
                            onBlur={() => markTouched("phoneNumber")}
                            required
                            maxLength={10}
                            aria-invalid={Boolean(errors.phoneNumber)}
                            aria-describedby={
                                errors.phoneNumber ? "phoneNumber-error" : undefined
                            }
                            className={inputClassName}
                        />
                    </div>

                    {renderError("phoneNumber")}
                </div>

                {/* Password */}
                <div>
                    <label
                        htmlFor="password"
                        className="mb-2 block text-sm font-semibold text-text-primary"
                    >
                        Password
                    </label>

                    <div className={fieldClassName(Boolean(errors.password))}>
                        <LockKeyhole
                            size={18}
                            className="shrink-0 text-text-muted"
                            aria-hidden="true"
                        />

                        <input
                            id="password"
                            name="password"
                            type={showPassword ? "text" : "password"}
                            autoComplete="new-password"
                            placeholder="Create a strong password"
                            value={values.password}
                            onChange={(event) =>
                                updateField("password", event.currentTarget.value)
                            }
                            onBlur={() => markTouched("password")}
                            required
                            minLength={8}
                            maxLength={20}
                            aria-invalid={Boolean(errors.password)}
                            aria-describedby={
                                errors.password ? "password-error" : "password-hint"
                            }
                            className={inputClassName}
                        />

                        <button
                            type="button"
                            onClick={() => setShowPassword((visible) => !visible)}
                            aria-label={showPassword ? "Hide password" : "Show password"}
                            className="shrink-0 rounded-md p-1 text-text-secondary hover:text-text-primary focus-visible:outline-2 focus-visible:outline-brand-primary"
                        >
                            {showPassword ? (
                                <EyeOff size={18} aria-hidden="true" />
                            ) : (
                                <Eye size={18} aria-hidden="true" />
                            )}
                        </button>
                    </div>

                    <p
                        id="password-hint"
                        className="mt-1.5 text-xs leading-5 text-text-muted"
                    >
                        8–20 characters with uppercase, lowercase, a number and one of
                        @#$%^&amp;+=!
                    </p>

                    {renderError("password")}
                </div>

                {/* Confirm password */}
                <div>
                    <label
                        htmlFor="confirmPassword"
                        className="mb-2 block text-sm font-semibold text-text-primary"
                    >
                        Confirm password
                    </label>

                    <div className={fieldClassName(Boolean(errors.confirmPassword))}>
                        <LockKeyhole
                            size={18}
                            className="shrink-0 text-text-muted"
                            aria-hidden="true"
                        />

                        <input
                            id="confirmPassword"
                            name="confirmPassword"
                            type={showConfirmPassword ? "text" : "password"}
                            autoComplete="new-password"
                            placeholder="Re-enter your password"
                            value={values.confirmPassword}
                            onChange={(event) =>
                                updateField("confirmPassword", event.currentTarget.value)
                            }
                            onBlur={() => markTouched("confirmPassword")}
                            required
                            minLength={8}
                            maxLength={20}
                            aria-invalid={Boolean(errors.confirmPassword)}
                            aria-describedby={
                                errors.confirmPassword
                                    ? "confirmPassword-error"
                                    : undefined
                            }
                            className={inputClassName}
                        />

                        <button
                            type="button"
                            onClick={() =>
                                setShowConfirmPassword((visible) => !visible)
                            }
                            aria-label={
                                showConfirmPassword
                                    ? "Hide confirm password"
                                    : "Show confirm password"
                            }
                            className="shrink-0 rounded-md p-1 text-text-secondary hover:text-text-primary focus-visible:outline-2 focus-visible:outline-brand-primary"
                        >
                            {showConfirmPassword ? (
                                <EyeOff size={18} aria-hidden="true" />
                            ) : (
                                <Eye size={18} aria-hidden="true" />
                            )}
                        </button>
                    </div>

                    {renderError("confirmPassword")}
                </div>

                {/* Validation-only status */}
                {formStatus && (
                    <p
                        role="status"
                        className="rounded-lg border border-brand-primary/30 bg-brand-primary/10 px-3 py-2.5 text-sm leading-5 text-text-primary"
                    >
                        {formStatus}
                    </p>
                )}

                {/* Submit */}
                <button
                    type="submit"
                    className="group inline-flex min-h-12 w-full items-center justify-center gap-2 rounded-xl bg-brand-primary px-5 py-3 text-sm font-bold text-white shadow-lg shadow-brand-primary/20 transition-all hover:-translate-y-0.5 hover:bg-brand-hover hover:shadow-xl focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-primary"
                >
                    Create account
                    <ArrowRight
                        size={17}
                        className="transition-transform group-hover:translate-x-1"
                        aria-hidden="true"
                    />
                </button>
            </form>

            <p className="mt-6 text-center text-sm text-text-secondary">
                Already have an account?{" "}
                <Link
                    to="/login"
                    className="font-bold text-brand-primary transition-colors hover:text-brand-hover"
                >
                    Sign in
                </Link>
            </p>
        </div>
    );
};

export default RegisterPage;
