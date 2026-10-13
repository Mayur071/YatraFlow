
import { Link } from "react-router-dom";

const footerLinks = [
    { label: "Help Center", to: "/help" },
    { label: "Contact Us", to: "/contact" },
    { label: "Cancellation & Refunds", to: "/cancellation-policy" },
    { label: "Privacy", to: "/privacy" },
    { label: "Terms", to: "/terms" },
];

const Footer = () => {
    const currentYear = new Date().getFullYear();

    return (
        <footer className="border-t border-border bg-surface-soft">
            <div className="mx-auto max-w-7xl px-5 py-5 sm:px-8 lg:px-10">
                <div className="flex flex-col items-center justify-between gap-4 sm:flex-row">
                    <p className="text-xs font-semibold text-text-primary sm:text-sm">
                        © {currentYear} YatraFlow. All rights reserved.
                    </p>

                    <nav
                        aria-label="Footer navigation"
                        className="flex flex-wrap items-center justify-center gap-x-5 gap-y-3"
                    >
                        {footerLinks.map((link) => (
                            <Link
                                key={link.to}
                                to={link.to}
                                className="rounded-sm text-sm font-medium text-text-primary transition-colors duration-200 hover:text-brand-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-primary"
                            >
                                {link.label}
                            </Link>
                        ))}
                    </nav>
                </div>
            </div>
        </footer>
    );
};

export default Footer;
