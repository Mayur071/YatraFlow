
import { useState } from "react";
import { Link } from "react-router-dom";
import {
    Eye,
    EyeOff,
    Mail,
    LockKeyhole,
    ArrowRight,
} from "lucide-react";

import { FcGoogle } from "react-icons/fc";
import { FaGithub } from "react-icons/fa6";

const LoginPage = () => {
    const [showPassword, setShowPassword] = useState(false);

    const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();
        // Backend API integration will be added in a later step.
    };

    return (
        <div>
            <div className="mb-8">
                <p className="text-sm font-semibold tracking-wide text-brand-primary">
                    WELCOME BACK
                </p>

                <h1 className="mt-2 font-brand text-3xl font-extrabold tracking-tight text-text-primary sm:text-4xl">
                    Sign in to YatraFlow
                </h1>

                <p className="mt-3 text-sm leading-6 text-text-secondary">
                    Your next journey is just a sign-in away.
                </p>
            </div>

            {/* Social sign-in options — OAuth is not integrated yet */}
            {/* OAuth buttons — not active yet */}
            <div className="grid grid-cols-2 gap-3">
                <button
                    type="button"
                    disabled
                    title="Google sign-in is coming soon"
                    className="inline-flex min-h-11 cursor-not-allowed items-center justify-center gap-2 rounded-xl border border-border bg-surface px-3 text-sm font-semibold text-text-secondary opacity-70"
                >
                    <FcGoogle size={18} aria-hidden="true" />
                    <span>Continue with Google</span>
                </button>

                <button
                    type="button"
                    disabled
                    title="GitHub sign-in is coming soon"
                    className="inline-flex min-h-11 cursor-not-allowed items-center justify-center gap-2 rounded-xl border border-border bg-surface px-3 text-sm font-semibold text-text-secondary opacity-70"
                >
                    <FaGithub size={18} aria-hidden="true" />
                    <span>Continue with GitHub</span>
                </button>
            </div>

            <div className="my-6 flex items-center gap-4">
                <div className="h-px flex-1 bg-border" />
                <span className="text-xs font-medium uppercase tracking-wider text-text-muted">
          Or continue with email
        </span>
                <div className="h-px flex-1 bg-border" />
            </div>

            <form onSubmit={handleSubmit} className="space-y-5">
                {/* Email */}
                <div>
                    <label
                        htmlFor="email"
                        className="mb-2 block text-sm font-semibold text-text-primary"
                    >
                        Email address
                    </label>

                    <div className="flex min-h-12 items-center gap-3 rounded-xl border border-border bg-surface px-3.5 transition-colors focus-within:border-brand-primary focus-within:ring-2 focus-within:ring-brand-primary/15">
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
                            required
                            maxLength={150}
                            className="w-full min-w-0 bg-transparent py-3 text-sm text-text-primary outline-none placeholder:text-text-muted"
                        />
                    </div>
                </div>

                {/* Password */}
                <div>
                    <div className="mb-2 flex items-center justify-between gap-3">
                        <label
                            htmlFor="password"
                            className="text-sm font-semibold text-text-primary"
                        >
                            Password
                        </label>

                        <Link
                            to="/forgot-password"
                            className="text-xs font-semibold text-brand-primary transition-colors hover:text-brand-hover"
                        >
                            Forgot password?
                        </Link>
                    </div>

                    <div className="flex min-h-12 items-center gap-3 rounded-xl border border-border bg-surface px-3.5 transition-colors focus-within:border-brand-primary focus-within:ring-2 focus-within:ring-brand-primary/15">
                        <LockKeyhole
                            size={18}
                            className="shrink-0 text-text-muted"
                            aria-hidden="true"
                        />

                        <input
                            id="password"
                            name="password"
                            type={showPassword ? "text" : "password"}
                            autoComplete="current-password"
                            placeholder="Enter your password"
                            required
                            minLength={8}
                            maxLength={20}
                            className="w-full min-w-0 bg-transparent py-3 text-sm text-text-primary outline-none placeholder:text-text-muted"
                        />

                        <button
                            type="button"
                            onClick={() => setShowPassword((visible) => !visible)}
                            aria-label={showPassword ? "Hide password" : "Show password"}
                            className="shrink-0 rounded-md p-1 text-text-secondary transition-colors hover:text-text-primary focus-visible:outline-2 focus-visible:outline-brand-primary"
                        >
                            {showPassword ? (
                                <EyeOff size={18} aria-hidden="true" />
                            ) : (
                                <Eye size={18} aria-hidden="true" />
                            )}
                        </button>
                    </div>
                </div>

                {/* Submit */}
                <button
                    type="submit"
                    className="group inline-flex min-h-12 w-full items-center justify-center gap-2 rounded-xl bg-brand-primary px-5 py-3 text-sm font-bold text-white shadow-lg shadow-brand-primary/20 transition-all hover:-translate-y-0.5 hover:bg-brand-hover hover:shadow-xl focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-primary"
                >
                    Sign in
                    <ArrowRight
                        size={17}
                        className="transition-transform group-hover:translate-x-1"
                        aria-hidden="true"
                    />
                </button>
            </form>

            <p className="mt-7 text-center text-sm text-text-secondary">
                New to YatraFlow?{" "}
                <Link
                    to="/register"
                    className="font-bold text-brand-primary transition-colors hover:text-brand-hover"
                >
                    Create an account
                </Link>
            </p>
        </div>
    );
};

export default LoginPage;
