
import { useState } from "react";
import { createPortal } from "react-dom";
import { Link, NavLink, useNavigate } from "react-router-dom";
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
    UserRound,
    Users,
    CreditCard,
    Settings,
    LogOut,
    ChevronDown,
} from "lucide-react";

import { useTheme } from "../../context/ThemeProvider";
import { useAuth } from "../../hooks/useAuth";
import YatraFlowLogo from "./YatraFlowLogo";

const navigationItems = [
    { label: "Flights", path: "/flights", icon: Plane },
    { label: "Hotels", path: "/hotels", icon: Hotel },
    { label: "Trains", path: "/trains", icon: Train },
    { label: "Buses", path: "/buses", icon: Bus },
    { label: "Cabs", path: "/cabs", icon: Car },
    { label: "Explore", path: "/explore", icon: Compass },
];

const profileItems = [
    { label: "My Profile", path: "/profile", icon: UserRound },
    { label: "Traveller Details", path: "/travellers", icon: Users },
    { label: "Wishlist", path: "/wishlist", icon: Heart },
    { label: "My Trips", path: "/trips", icon: BriefcaseBusiness },
    { label: "Payments", path: "/payments", icon: CreditCard },
    { label: "Settings", path: "/settings", icon: Settings },
];

const Header = () => {
    const { theme, toggleTheme } = useTheme();
    const { user, isAuthenticated, logout } = useAuth();

    const navigate = useNavigate();

    const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
    const [profileMenuOpen, setProfileMenuOpen] = useState(false);
    const [logoutConfirmOpen, setLogoutConfirmOpen] = useState(false);
    const [isLoggingOut, setIsLoggingOut] = useState(false);

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

    const closeProfileMenu = () => {
        setProfileMenuOpen(false);
    };

    const openLogoutConfirm = () => {
        closeProfileMenu();
        closeMobileMenu();
        setLogoutConfirmOpen(true);
    };

    const handleLogout = async () => {
        if (isLoggingOut) return;

        setIsLoggingOut(true);

        try {
            await logout();

            setLogoutConfirmOpen(false);
            closeProfileMenu();
            closeMobileMenu();

            navigate("/", { replace: true });
        } catch (error) {
            console.error("Logout failed:", error);
        } finally {
            setIsLoggingOut(false);
        }
    };

    const utilityLinkClass =
        "inline-flex items-center gap-1.5 whitespace-nowrap rounded-lg px-2 py-2 text-sm font-semibold text-text-primary transition-colors hover:bg-surface-soft hover:text-brand-primary";

    const userName = user
        ? `${user.firstName} ${user.lastName}`.trim()
        : "";

    const userInitial = user?.firstName?.charAt(0).toUpperCase() || "U";

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

                    {isAuthenticated ? (
                        <>
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

                            {/* Profile Dropdown */}
                            <div className="relative hidden xl:block">
                                <button
                                    type="button"
                                    onClick={() =>
                                        setProfileMenuOpen((open) => !open)
                                    }
                                    aria-expanded={profileMenuOpen}
                                    aria-label="Open profile menu"
                                    className="flex items-center gap-2 rounded-full border border-border bg-surface px-2 py-1.5 transition-colors hover:bg-surface-soft"
                                >
                                    <span className="flex h-8 w-8 items-center justify-center rounded-full bg-brand-primary text-sm font-bold text-white">
                                        {userInitial}
                                    </span>

                                    <span className="max-w-[110px] truncate text-sm font-semibold text-text-primary">
                                        {user?.firstName}
                                    </span>

                                    <ChevronDown
                                        size={16}
                                        className={`text-text-secondary transition-transform ${
                                            profileMenuOpen ? "rotate-180" : ""
                                        }`}
                                    />
                                </button>

                                {profileMenuOpen && (
                                    <div className="absolute right-0 top-full mt-3 w-64 overflow-hidden rounded-xl border border-border bg-surface shadow-xl">

                                        {/* User Information */}
                                        <div className="border-b border-border px-4 py-4">
                                            <p className="truncate text-sm font-semibold text-text-primary">
                                                {userName}
                                            </p>
                                            <p className="mt-1 truncate text-xs text-text-secondary">
                                                {user?.email}
                                            </p>
                                        </div>

                                        {/* Profile Navigation */}
                                        <div className="p-2">
                                            {profileItems.map((item) => (
                                                <Link
                                                    key={item.path}
                                                    to={item.path}
                                                    onClick={closeProfileMenu}
                                                    className="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium text-text-primary transition-colors hover:bg-surface-soft hover:text-brand-primary"
                                                >
                                                    <item.icon
                                                        size={17}
                                                        strokeWidth={1.8}
                                                    />
                                                    {item.label}
                                                </Link>
                                            ))}
                                        </div>

                                        {/* Logout */}
                                        <div className="border-t border-border p-2">
                                            <button
                                                type="button"
                                                onClick={openLogoutConfirm}
                                                className="flex w-full items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-semibold text-red-500 transition-colors hover:bg-red-500/10"
                                            >
                                                <LogOut size={17} />
                                                Sign Out
                                            </button>
                                        </div>
                                    </div>
                                )}
                            </div>
                        </>
                    ) : (
                        /* Guest Authentication Actions */
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
                    )}

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

                            {isAuthenticated ? (
                                <>
                                    {/* Mobile User Information */}
                                    <div className="mb-4 flex items-center gap-3">
                                        <span className="flex h-11 w-11 items-center justify-center rounded-full bg-brand-primary text-base font-bold text-white">
                                            {userInitial}
                                        </span>

                                        <div className="min-w-0">
                                            <p className="truncate text-sm font-semibold text-text-primary">
                                                {userName}
                                            </p>
                                            <p className="truncate text-xs text-text-secondary">
                                                {user?.email}
                                            </p>
                                        </div>
                                    </div>

                                    {/* Mobile Profile Navigation */}
                                    <div className="flex flex-col gap-2">
                                        {profileItems.map((item) => (
                                            <Link
                                                key={item.path}
                                                to={item.path}
                                                onClick={closeMobileMenu}
                                                className={utilityLinkClass}
                                            >
                                                <item.icon size={18} />
                                                {item.label}
                                            </Link>
                                        ))}

                                        <button
                                            type="button"
                                            onClick={openLogoutConfirm}
                                            className="flex items-center gap-2 rounded-lg px-2 py-2 text-sm font-semibold text-red-500 transition-colors hover:bg-red-500/10"
                                        >
                                            <LogOut size={18} />
                                            Sign Out
                                        </button>
                                    </div>
                                </>
                            ) : (
                                /* Guest Authentication */
                                <div className="flex gap-3">
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
                            )}
                        </div>
                    </div>
                </nav>
            )}

            {/* Logout Confirmation Modal - React Portal */}
            {logoutConfirmOpen &&
                createPortal(
                    <div
                        className="fixed inset-0 z-[9999] flex items-center justify-center bg-black/60 px-4 backdrop-blur-sm"
                        onClick={() => {
                            if (!isLoggingOut) {
                                setLogoutConfirmOpen(false);
                            }
                        }}
                    >
                        <div
                            role="dialog"
                            aria-modal="true"
                            aria-labelledby="logout-title"
                            aria-describedby="logout-description"
                            className="w-full max-w-[380px] rounded-xl border border-border bg-surface p-5 shadow-2xl"
                            onClick={(event) => event.stopPropagation()}
                        >
                            {/* Modal Icon */}
                            <div className="flex flex-col items-center text-center">
                                <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-red-500/10">
                                    <LogOut
                                        size={21}
                                        className="text-red-500"
                                    />
                                </div>

                                {/* Modal Heading */}
                                <h2
                                    id="logout-title"
                                    className="text-lg font-semibold text-text-primary"
                                >
                                    Sign out of your account?
                                </h2>

                                {/* Modal Description */}
                                <p
                                    id="logout-description"
                                    className="mt-2 text-sm leading-5 text-text-secondary"
                                >
                                    Are you sure you want to sign out? You can log back in anytime.
                                </p>
                            </div>

                            {/* Modal Actions */}
                            <div className="mt-6 grid grid-cols-2 gap-3">
                                <button
                                    type="button"
                                    disabled={isLoggingOut}
                                    onClick={() =>
                                        setLogoutConfirmOpen(false)
                                    }
                                    className="rounded-lg border border-border px-4 py-2.5 text-sm font-medium text-text-primary transition-colors hover:bg-surface-soft disabled:cursor-not-allowed disabled:opacity-50"
                                >
                                    Cancel
                                </button>

                                <button
                                    type="button"
                                    disabled={isLoggingOut}
                                    onClick={handleLogout}
                                    className="rounded-lg bg-red-600 px-4 py-2.5 text-sm font-semibold text-white transition-colors hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-60"
                                >
                                    {isLoggingOut
                                        ? "Signing out..."
                                        : "Sign Out"}
                                </button>
                            </div>
                        </div>
                    </div>,
                    document.body
                )}
        </header>
    );
};

export default Header;
