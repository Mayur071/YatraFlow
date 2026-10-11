
import { Link } from "react-router-dom";
import YatraFlowLogo from "./YatraFlowLogo";

type FooterLink = {
    label: string;
    to: string;
};

const supportLinks: FooterLink[] = [
    { label: "Help Center", to: "/help" },
    { label: "Frequently Asked Questions", to: "/faq" },
    { label: "Contact Us", to: "/contact" },
    { label: "Cancellation & Refunds", to: "/cancellation-policy" },
];

const companyLinks: FooterLink[] = [
    { label: "About YatraFlow", to: "/about" },
    { label: "Our Story", to: "/about" },
    { label: "Careers", to: "/careers" },
];

const policyLinks: FooterLink[] = [
    { label: "Privacy Policy", to: "/privacy" },
    { label: "Terms of Service", to: "/terms" },
    { label: "Cookie Policy", to: "/cookies" },
    { label: "Accessibility", to: "/accessibility" },
];

const linkClassName =
    "inline-flex rounded-sm text-sm leading-6 text-text-secondary " +
    "transition-colors duration-200 hover:text-brand-primary " +
    "focus-visible:outline-none focus-visible:ring-2 " +
    "focus-visible:ring-brand-primary focus-visible:ring-offset-2 " +
    "focus-visible:ring-offset-surface-soft";

interface FooterLinkGroupProps {
    title: string;
    links: FooterLink[];
}

const FooterLinkGroup = ({ title, links }: FooterLinkGroupProps) => {
    const headingId = `footer-${title
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, "-")}`;

    return (
        <nav aria-labelledby={headingId}>
            <h2
                id={headingId}
                className="text-sm font-semibold text-text-primary"
            >
                {title}
            </h2>

            <ul className="mt-4 space-y-3">
                {links.map((link) => (
                    <li key={`${link.label}-${link.to}`}>
                        <Link to={link.to} className={linkClassName}>
                            {link.label}
                        </Link>
                    </li>
                ))}
            </ul>
        </nav>
    );
};

const Footer = () => {
    const currentYear = new Date().getFullYear();

    return (
        <footer className="border-t border-border bg-surface-soft">
            <div className="mx-auto max-w-7xl px-5 py-12 sm:px-8 sm:py-14 lg:px-10">
                {/* Main footer content */}
                <div className="grid grid-cols-1 gap-x-8 gap-y-10 sm:grid-cols-2 lg:grid-cols-12 lg:gap-y-0">
                    {/* Brand */}
                    <div className="sm:col-span-2 lg:col-span-3">
                        <Link
                            to="/"
                            aria-label="YatraFlow home"
                            className="inline-flex rounded-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-primary focus-visible:ring-offset-2 focus-visible:ring-offset-surface-soft"
                        >
                            <YatraFlowLogo className="h-9 w-auto max-w-[190px] object-contain" />
                        </Link>

                        <p className="mt-5 max-w-sm text-sm leading-7 text-text-secondary">
                            Discover new destinations, plan your journeys, and make every
                            trip more memorable with YatraFlow.
                        </p>

                        <p className="mt-4 text-xs font-semibold tracking-[0.16em] text-text-muted">
                            PLAN. EXPLORE. TRAVEL.
                        </p>
                    </div>

                    {/* Help & Support */}
                    <div className="lg:col-span-2">
                        <FooterLinkGroup title="Help & Support" links={supportLinks} />
                    </div>

                    {/* Company */}
                    <div className="lg:col-span-2">
                        <FooterLinkGroup title="Company" links={companyLinks} />
                    </div>

                    {/* Policies */}
                    <div className="lg:col-span-2">
                        <FooterLinkGroup title="Policies" links={policyLinks} />
                    </div>

                    {/* Contact */}
                    <div className="lg:col-span-3">
                        <h2 className="text-sm font-semibold text-text-primary">
                            Connect With Us
                        </h2>

                        <p className="mt-4 text-sm leading-7 text-text-secondary">
                            Have a question, need assistance, or want to share feedback?
                            We would love to hear from you.
                        </p>

                        <Link
                            to="/contact"
                            className="mt-4 inline-flex items-center gap-2 rounded-sm text-sm font-semibold text-brand-primary transition-colors duration-200 hover:text-brand-hover focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-primary focus-visible:ring-offset-2 focus-visible:ring-offset-surface-soft"
                        >
                            Get in touch
                            <span aria-hidden="true">→</span>
                        </Link>
                    </div>
                </div>

                {/* Copyright */}
                <div className="mt-12 flex flex-col gap-3 border-t border-border pt-6 sm:flex-row sm:items-center sm:justify-between">
                    <p className="text-xs leading-5 text-text-secondary">
                        © {currentYear} YatraFlow. All rights reserved.
                    </p>

                    <p className="text-xs leading-5 text-text-muted">
                        Made for journeys worth remembering.
                    </p>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
