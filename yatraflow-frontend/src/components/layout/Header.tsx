
import { useState } from "react";
import { Link, NavLink } from "react-router-dom";
import { useTheme } from "../../context/ThemeProvider";
import YatraFlowLogo from "./YatraFlowLogo";

const navigationItems = [
    { label: "Flights", path: "/flights" },
    { label: "Hotels", path: "/hotels" },
    { label: "Trains", path: "/trains" },
    { label: "Buses", path: "/buses" },
    { label: "Cabs", path: "/cabs" },
    { label: "Explore", path: "/explore" },
];

const Header = () => {
    const { theme, toggleTheme } = useTheme();
    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

    const navigationLinkClass = ({ isActive }: { isActive: boolean }) =>
        [
            "whitespace-nowrap text-sm font-medium transition-colors duration-200",
            isActive
                ? "text-brand-primary"
                : "text-text-secondary hover:text-text-primary",
        ].join(" ");

    return (
        <header className="sticky top-0 z-50 w-full border-b border-border bg-surface/95 shadow-sm backdrop-blur-xl">
            <div className="mx-auto flex h-[72px] max-w-7xl items-center justify-between gap-4 px-4 sm:px-6 lg:px-8">

                {/* Brand */}
                <Link
                    to="/"
                    aria-label="YatraFlow home"
                    className="flex shrink-0 items-center"
                    onClick={() => setMobileMenuOpen(false)}
                >
                    <YatraFlowLogo className="h-8 w-auto max-w-[170px] object-contain sm:h-9" />
                </Link>

                {/* Desktop navigation */}
                <nav
                    aria-label="Main navigation"
                    className="hidden items-center gap-5 xl:flex"
                >
                    {navigationItems.map((item) => (
                        <NavLink
                            key={item.path}
                            to={item.path}
                            className={navigationLinkClass}
                        >
                            {item.label}
                        </NavLink>
                    ))}
                </nav>

                {/* Header actions */}
                <div className="flex shrink-0 items-center gap-2 sm:gap-3">

                    {/* Theme toggle */}
                    <button
                        type="button"
                        onClick={toggleTheme}
                        aria-label={
                            theme === "light"
                                ? "Switch to dark theme"
                                : "Switch to light theme"
                        }
                        title={
                            theme === "light"
                                ? "Switch to dark theme"
                                : "Switch to light theme"
                        }
                        className="inline-flex h-10 w-10 items-center justify-center rounded-full border border-border bg-surface text-text-primary transition-colors hover:bg-surface-soft focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-primary"
                    >
                        {theme === "light" ? (
                            <svg
                                aria-hidden="true"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="1.8"
                                className="h-5 w-5"
                            >
                                <path
                                    d="M20.9 13A8.5 8.5 0 0 1 11 3.1 8.5 8.5 0 1 0 20.9 13Z"
                                    strokeLinecap="round"
                                    strokeLinejoin="round"
                                />
                            </svg>
                        ) : (
                            <svg
                                aria-hidden="true"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="1.8"
                                className="h-5 w-5"
                            >
                                <circle cx="12" cy="12" r="4" />
                                <path
                                    d="M12 2v2m0 16v2M4.93 4.93l1.42 1.42m11.3 11.3 1.42 1.42M2 12h2m16 0h2M4.93 19.07l1.42-1.42m11.3-11.3 1.42-1.42"
                                    strokeLinecap="round"
                                />
                            </svg>
                        )}
                    </button>

                    {/* Desktop authentication actions */}
                    <div className="hidden items-center gap-3 xl:flex">
                        <Link
                            to="/login"
                            className="rounded-lg px-4 py-2.5 text-sm font-semibold text-text-primary transition-colors hover:bg-surface-soft"
                        >
                            Login
                        </Link>

                        <Link
                            to="/register"
                            className={`rounded-lg px-5 py-2.5 text-sm font-semibold shadow-sm transition-all hover:-translate-y-0.5 hover:shadow-md ${
                                theme === "dark"
                                    ? "bg-brand-primary text-slate-950 hover:bg-brand-hover"
                                    : "bg-brand-primary text-white hover:bg-brand-hover"
                            }`}
                        >
                            Sign Up
                        </Link>
                    </div>

                    {/* Mobile menu button */}
                    <button
                        type="button"
                        onClick={() => setMobileMenuOpen((open) => !open)}
                        aria-label={mobileMenuOpen ? "Close navigation menu" : "Open navigation menu"}
                        aria-expanded={mobileMenuOpen}
                        className="inline-flex h-10 w-10 items-center justify-center rounded-lg border border-border text-text-primary transition-colors hover:bg-surface-soft xl:hidden"
                    >
                        {mobileMenuOpen ? (
                            <svg
                                aria-hidden="true"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="1.8"
                                className="h-5 w-5"
                            >
                                <path d="m6 6 12 12M18 6 6 18" strokeLinecap="round" />
                            </svg>
                        ) : (
                            <svg
                                aria-hidden="true"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                strokeWidth="1.8"
                                className="h-5 w-5"
                            >
                                <path
                                    d="M4 6h16M4 12h16M4 18h16"
                                    strokeLinecap="round"
                                />
                            </svg>
                        )}
                    </button>
                </div>
            </div>

            {/* Responsive mobile navigation */}
            {mobileMenuOpen && (
                <nav
                    aria-label="Mobile navigation"
                    className="border-t border-border bg-surface px-4 py-5 shadow-lg xl:hidden sm:px-6"
                >
                    <div className="mx-auto flex max-w-7xl flex-col gap-4">
                        {navigationItems.map((item) => (
                            <NavLink
                                key={item.path}
                                to={item.path}
                                className={navigationLinkClass}
                                onClick={() => setMobileMenuOpen(false)}
                            >
                                {item.label}
                            </NavLink>
                        ))}

                        <div className="flex gap-3 border-t border-border pt-4">
                            <Link
                                to="/login"
                                onClick={() => setMobileMenuOpen(false)}
                                className="flex-1 rounded-lg border border-border px-4 py-2.5 text-center text-sm font-semibold text-text-primary transition-colors hover:bg-surface-soft"
                            >
                                Login
                            </Link>

                            <Link
                                to="/register"
                                onClick={() => setMobileMenuOpen(false)}
                                className={`flex-1 rounded-lg px-4 py-2.5 text-center text-sm font-semibold ${
                                    theme === "dark"
                                        ? "bg-brand-primary text-slate-950"
                                        : "bg-brand-primary text-white"
                                }`}
                            >
                                Sign Up
                            </Link>
                        </div>
                    </div>
                </nav>
            )}
        </header>
    );
};

export default Header;
