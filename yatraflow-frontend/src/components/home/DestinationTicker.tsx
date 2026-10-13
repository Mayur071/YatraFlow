
import {
    Compass,
    Landmark,
    Leaf,
    MapPin,
    Mountain,
    Waves,
} from "lucide-react";

const destinations = [
    { name: "Goa", detail: "Coastal escapes", icon: Waves },
    { name: "Manali", detail: "Mountain retreats", icon: Mountain },
    { name: "Jaipur", detail: "Culture & heritage", icon: Landmark },
    { name: "Kerala", detail: "Nature getaways", icon: Leaf },
    { name: "Rishikesh", detail: "Adventure awaits", icon: Compass },
    { name: "Udaipur", detail: "Lakeside escapes", icon: MapPin },
];

const DestinationTicker = () => {
    const repeatedDestinations = [...destinations, ...destinations];

    return (
        <section
            aria-label="Popular travel destinations"
            className="border-b border-border bg-surface-soft"
        >
            <div className="mx-auto flex max-w-7xl items-center gap-5 px-4 py-3 sm:px-6 lg:px-8">
                <p className="hidden shrink-0 text-xs font-bold uppercase tracking-[0.15em] text-brand-teal lg:block">
                    Trending escapes
                </p>

                <div className="destination-marquee min-w-0 flex-1">
                    <div className="destination-marquee-track">
                        {repeatedDestinations.map((destination, index) => {
                            const Icon = destination.icon;
                            const isDuplicate = index >= destinations.length;

                            return (
                                <div
                                    key={`${destination.name}-${index}`}
                                    aria-hidden={isDuplicate}
                                    className="inline-flex shrink-0 items-center gap-2.5 rounded-full border border-border bg-surface px-4 py-2"
                                >
                                    <Icon
                                        size={17}
                                        strokeWidth={1.8}
                                        className="shrink-0 text-brand-teal"
                                        aria-hidden="true"
                                    />

                                    <span className="text-sm font-semibold text-text-primary">
                    {destination.name}
                  </span>

                                    <span className="text-xs text-text-secondary">
                    {destination.detail}
                  </span>
                                </div>
                            );
                        })}
                    </div>
                </div>
            </div>
        </section>
    );
};

export default DestinationTicker;
