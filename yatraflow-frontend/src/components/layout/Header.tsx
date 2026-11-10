
import { useState } from "react";
import { Link, NavLink } from "react-router-dom";
import { useTheme } from "../../context/ThemeProvider";
import YatraFlowLogo from "./YatraFlowLogo";

import {
    Plane,
    Hotel,
    Train,
    Bus,
    Car,
    Compass,
    Heart,
    BriefcaseBusiness,
    Sun,
    Moon,
    Menu,
    X,
} from "lucide-react";

const navigationItems = [
    { label: "Flights", path: "/flights", icon: Plane },
    { label: "Hotels", path: "/hotels", icon: Hotel },
    { label: "Trains", path: "/trains", icon: Train },
    { label: "Buses", path: "/buses", icon: Bus },
    { label: "Cabs", path: "/cabs", icon: Car },
    { label: "Explore", path: "/explore", icon: Compass },
];

const Header = () => {
    const { theme, toggleTheme } = useTheme();

    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

    const navigationLinkClass = ({ isActive }: { isActive: boolean }) =>
        [
            "inline-flex items-center justify-center gap-1.5 whitespace-nowrap",
            "text-sm font-semibold transition-colors duration-200",
            isActive
                ? "text-brand-primary"
                : "text-text-primary hover:text-brand-primary",
        ].join(" ");

    const closeMobileMenu = () => {
        setMobileMenuOpen(false);
    };

    const utilityLinkClass =
        "inline-flex items-center gap-1.5 whitespace-nowrap rounded-lg px-2 py-2 text-sm font-semibold text-text-primary transition-colors hover:bg-surface-soft hover:text-brand-primary";

    return (
        <header className="sticky top-0 z-50 w-full border-b border-border bg-surface/95 shadow-sm backdrop-blur-xl">

            {/* Main Header */}
            <div className="mx-auto flex min-h-[76px] max-w-[1440px] items-center justify-between gap-4 px-4 sm:px-6 xl:px-8">

                {/* Brand */}
                <Link
                    to="/"
                    aria-label="YatraFlow home"
                    className="flex shrink-0 items-center"
                    onClick={closeMobileMenu}
                >
                    <YatraFlowLogo className="h-8 w-auto max-w-[160px] object-contain sm:h-9" />
                </Link>

                {/* Desktop Travel Navigation */}
                <nav
                    aria-label="Travel navigation"
                    className="hidden items-center gap-4 xl:flex 2xl:gap-5"
                >
                    {navigationItems.map((item) => (
                        <NavLink
                            key={item.path}
                            to={item.path}
                            className={navigationLinkClass}
                        >
                            <item.icon
                                size={17}
                                strokeWidth={1.8}
                                aria-hidden="true"
                                className="shrink-0"
                            />

                            <span>{item.label}</span>
                        </NavLink>
                    ))}
                </nav>

                {/* Header Actions */}
                <div className="flex shrink-0 items-center gap-1.5 sm:gap-2">

                    {/* Wishlist */}
                    <Link
                        to="/wishlist"
                        className={`${utilityLinkClass} hidden 2xl:inline-flex`}
                        aria-label="Wishlist"
                    >
                        <Heart size={18} strokeWidth={1.8} />
                        <span>Wishlist</span>
                    </Link>

                    {/* My Trips */}
                    <Link
                        to="/trips"
                        className={`${utilityLinkClass} hidden 2xl:inline-flex`}
                        aria-label="My Trips"
                    >
                        <BriefcaseBusiness size={18} strokeWidth={1.8} />
                        <span>My Trips</span>
                    </Link>

                    {/* Theme Toggle */}
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
                        className="inline-flex h-10 w-10 shrink-0 items-center justify-center rounded-full border border-border bg-surface text-text-primary transition-colors hover:bg-surface-soft focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-brand-primary"
                    >
                        {theme === "light" ? (
                            <Moon size={19} strokeWidth={1.8} />
                        ) : (
                            <Sun size={19} strokeWidth={1.8} />
                        )}
                    </button>

                    {/* Guest Authentication Actions */}
                    <div className="hidden items-center gap-2 xl:flex">

                        <Link
                            to="/sign-in"
                            className="whitespace-nowrap rounded-lg px-3 py-2.5 text-sm font-semibold text-text-primary transition-colors hover:bg-surface-soft"
                        >
                            Sign In
                        </Link>

                        <Link
                            to="/sign-up"
                            className={`whitespace-nowrap rounded-lg px-4 py-2.5 text-sm font-semibold shadow-sm transition-all hover:-translate-y-0.5 hover:shadow-md ${
                                theme === "dark"
                                    ? "bg-brand-primary text-slate-950 hover:bg-brand-hover"
                                    : "bg-brand-primary text-white hover:bg-brand-hover"
                            }`}
                        >
                            Sign Up
                        </Link>

                    </div>

                    {/* Mobile Menu Button */}
                    <button
                        type="button"
                        onClick={() =>
                            setMobileMenuOpen((open) => !open)
                        }
                        aria-label={
                            mobileMenuOpen
                                ? "Close navigation menu"
                                : "Open navigation menu"
                        }
                        aria-expanded={mobileMenuOpen}
                        className="inline-flex h-10 w-10 items-center justify-center rounded-lg border border-border text-text-primary transition-colors hover:bg-surface-soft xl:hidden"
                    >
                        {mobileMenuOpen ? (
                            <X size={21} />
                        ) : (
                            <Menu size={21} />
                        )}
                    </button>
                </div>
            </div>

            {/* Mobile Navigation */}
            {mobileMenuOpen && (
                <nav
                    aria-label="Mobile navigation"
                    className="border-t border-border bg-surface px-4 py-5 shadow-lg xl:hidden sm:px-6"
                >
                    <div className="mx-auto flex max-w-7xl flex-col gap-5">

                        {/* Travel Services */}
                        <div className="grid grid-cols-2 gap-4 sm:grid-cols-3">
                            {navigationItems.map((item) => (
                                <NavLink
                                    key={item.path}
                                    to={item.path}
                                    onClick={closeMobileMenu}
                                    className={navigationLinkClass}
                                >
                                    <item.icon
                                        size={18}
                                        strokeWidth={1.8}
                                        aria-hidden="true"
                                    />

                                    <span>{item.label}</span>
                                </NavLink>
                            ))}
                        </div>

                        <div className="border-t border-border pt-4">

                            {/* Utility Navigation */}
                            <div className="flex flex-col gap-2">

                                <Link
                                    to="/wishlist"
                                    onClick={closeMobileMenu}
                                    className={utilityLinkClass}
                                >
                                    <Heart size={18} />
                                    Wishlist
                                </Link>

                                <Link
                                    to="/trips"
                                    onClick={closeMobileMenu}
                                    className={utilityLinkClass}
                                >
                                    <BriefcaseBusiness size={18} />
                                    My Trips
                                </Link>

                            </div>
                        </div>

                        {/* Authentication */}
                        <div className="flex gap-3 border-t border-border pt-4">

                            <Link
                                to="/sign-in"
                                onClick={closeMobileMenu}
                                className="flex-1 rounded-lg border border-border px-4 py-2.5 text-center text-sm font-semibold text-text-primary transition-colors hover:bg-surface-soft"
                            >
                                Sign In
                            </Link>

                            <Link
                                to="/sign-up"
                                onClick={closeMobileMenu}
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
