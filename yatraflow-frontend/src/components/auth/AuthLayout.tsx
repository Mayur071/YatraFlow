
import { Link, Outlet } from "react-router-dom";
import {
    ArrowUpRight,
    Compass,
    MapPin,
    Mountain,
    ShieldCheck,
    Waves,
} from "lucide-react";
import YatraFlowLogo from "../layout/YatraFlowLogo";

const travelHighlights = [
    {
        icon: Compass,
        title: "Explore your way",
        description: "Find inspiration for your next journey.",
    },
    {
        icon: MapPin,
        title: "Discover more",
        description: "From hidden gems to iconic destinations.",
    },
    {
        icon: ShieldCheck,
        title: "Your trips, together",
        description: "Keep your travel plans organized in one place.",
    },
];

const AuthLayout = () => {
    return (
        <main className="min-h-screen bg-background text-text-primary">
            <div className="grid min-h-screen lg:grid-cols-2">
                {/* Left branding panel */}
                <section className="relative isolate hidden overflow-hidden border-r border-border bg-surface-soft lg:flex">
                    {/* Decorative theme-aware gradients */}
                    <div
                        aria-hidden="true"
                        className="pointer-events-none absolute inset-0 -z-10 overflow-hidden"
                    >
                        <div className="absolute -left-32 -top-24 h-96 w-96 rounded-full bg-brand-primary/15 blur-3xl" />
                        <div className="absolute -bottom-24 -right-20 h-96 w-96 rounded-full bg-brand-teal/15 blur-3xl" />
                        <div className="absolute right-1/4 top-1/3 h-64 w-64 rounded-full bg-brand-accent/10 blur-3xl" />
                    </div>

                    <div className="mx-auto flex min-h-screen w-full max-w-2xl flex-col px-8 py-10 xl:px-14 xl:py-12">
                        {/* Brand */}
                        <Link
                            to="/"
                            aria-label="YatraFlow home"
                            className="inline-flex w-fit rounded-sm focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-brand-primary"
                        >
                            <YatraFlowLogo className="h-9 w-auto max-w-[190px] object-contain" />
                        </Link>

                        {/* Main message */}
                        <div className="my-auto py-12">
                            <div className="inline-flex items-center gap-2 rounded-full border border-brand-teal/30 bg-brand-teal/10 px-3.5 py-2 text-xs font-semibold tracking-wide text-brand-teal">
                <span
                    aria-hidden="true"
                    className="h-2 w-2 rounded-full bg-brand-teal"
                />
                                YOUR NEXT JOURNEY STARTS HERE
                            </div>

                            <h1 className="mt-7 max-w-xl font-brand text-4xl font-extrabold leading-tight tracking-tight text-text-primary xl:text-5xl xl:leading-[1.15]">
                                The world is waiting.
                                <span className="mt-2 block text-brand-teal">
                  Find your somewhere.
                </span>
                            </h1>

                            <p className="mt-6 max-w-lg text-base leading-7 text-text-secondary xl:text-lg xl:leading-8">
                                Discover inspiring destinations, explore new possibilities
                                and make every journey a story worth remembering.
                            </p>

                            {/* Travel inspiration card */}
                            <div className="mt-10 rounded-2xl border border-border bg-surface/80 p-5 shadow-lg shadow-brand-primary/5 backdrop-blur-sm sm:p-6">
                                <div className="flex items-start justify-between gap-3">
                                    <div>
                                        <p className="text-xs font-semibold uppercase tracking-[0.16em] text-brand-primary">
                                            A little inspiration
                                        </p>

                                        <h2 className="mt-2 font-brand text-xl font-bold text-text-primary">
                                            Find your kind of somewhere.
                                        </h2>
                                    </div>

                                    <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl bg-brand-primary/10 text-brand-primary">
                                        <ArrowUpRight size={22} aria-hidden="true" />
                                    </div>
                                </div>

                                <div className="mt-5 grid grid-cols-3 gap-2.5">
                                    <div className="rounded-xl border border-border bg-surface-soft p-3">
                                        <Waves
                                            size={21}
                                            className="text-brand-teal"
                                            aria-hidden="true"
                                        />
                                        <p className="mt-3 text-sm font-semibold text-text-primary">
                                            Coastlines
                                        </p>
                                        <p className="mt-1 text-xs text-text-secondary">
                                            Slow escapes
                                        </p>
                                    </div>

                                    <div className="rounded-xl border border-border bg-surface-soft p-3">
                                        <Mountain
                                            size={21}
                                            className="text-brand-primary"
                                            aria-hidden="true"
                                        />
                                        <p className="mt-3 text-sm font-semibold text-text-primary">
                                            Mountains
                                        </p>
                                        <p className="mt-1 text-xs text-text-secondary">
                                            Fresh horizons
                                        </p>
                                    </div>

                                    <div className="rounded-xl border border-border bg-surface-soft p-3">
                                        <Compass
                                            size={21}
                                            className="text-brand-accent"
                                            aria-hidden="true"
                                        />
                                        <p className="mt-3 text-sm font-semibold text-text-primary">
                                            City breaks
                                        </p>
                                        <p className="mt-1 text-xs text-text-secondary">
                                            New discoveries
                                        </p>
                                    </div>
                                </div>
                            </div>

                            {/* Product highlights */}
                            <div className="mt-8 grid gap-4 sm:grid-cols-3">
                                {travelHighlights.map((item) => {
                                    const Icon = item.icon;

                                    return (
                                        <div key={item.title} className="flex items-start gap-2.5">
                                            <Icon
                                                size={18}
                                                className="mt-0.5 shrink-0 text-brand-teal"
                                                aria-hidden="true"
                                            />

                                            <div>
                                                <p className="text-sm font-semibold text-text-primary">
                                                    {item.title}
                                                </p>

                                                <p className="mt-1 text-xs leading-5 text-text-secondary">
                                                    {item.description}
                                                </p>
                                            </div>
                                        </div>
                                    );
                                })}
                            </div>
                        </div>

                        <p className="text-xs text-text-muted">
                            YatraFlow · Made for journeys worth remembering.
                        </p>
                    </div>
                </section>

                {/* Right authentication panel */}
                <section className="flex min-h-screen items-center justify-center bg-background px-5 py-8 sm:px-8 lg:px-10 xl:px-14">
                    <div className="w-full max-w-md">
                        {/* Brand appears on mobile, where the left panel is hidden */}
                        <div className="mb-10 lg:hidden">
                            <Link
                                to="/"
                                aria-label="YatraFlow home"
                                className="inline-flex rounded-sm focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-brand-primary"
                            >
                                <YatraFlowLogo className="h-9 w-auto max-w-[190px] object-contain" />
                            </Link>
                        </div>

                        {/* Login/Register page renders here */}
                        <Outlet />

                        <p className="mt-8 text-center text-xs leading-5 text-text-muted">
                            By continuing, you agree to YatraFlow&apos;s applicable terms
                            and privacy policy.
                        </p>
                    </div>
                </section>
            </div>
        </main>
    );
};

export default AuthLayout;
