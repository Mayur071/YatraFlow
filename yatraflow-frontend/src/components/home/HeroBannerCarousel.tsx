
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
    ArrowRight,
    ChevronLeft,
    ChevronRight,
} from "lucide-react";

interface HeroBanner {
    id: number;
    location: string;
    region: string;
    eyebrow: string;
    title: string;
    description: string;
    image: string;
    imageAlt: string;
}

const heroBanners: HeroBanner[] = [
    {
        id: 1,
        location: "The Himalayas",
        region: "Manali, Himachal Pradesh",
        eyebrow: "MOUNTAIN ESCAPES",
        title: "Find Your Mountain Escape.",
        description:
            "Discover snow-covered peaks, beautiful valleys and unforgettable Himalayan journeys.",
        image: "/images/home/hero/himalayas.png",
        imageAlt: "Snow-covered Himalayan mountains and a scenic Manali valley",
    },
    {
        id: 2,
        location: "Goa",
        region: "Goa, India",
        eyebrow: "COASTAL GETAWAYS",
        title: "Let the Waves Take You Away.",
        description:
            "Experience beautiful beaches, golden sunsets and the magic of coastal India.",
        image: "/images/home/hero/goa.png",
        imageAlt: "Beautiful tropical beach and sunset in Goa",
    },
    {
        id: 3,
        location: "Rajasthan",
        region: "Jaisalmer, Rajasthan",
        eyebrow: "ROYAL HERITAGE",
        title: "Discover the Royal Side of India.",
        description:
            "Explore majestic forts, golden deserts and stories of India's royal heritage.",
        image: "/images/home/hero/rajasthan.png",
        imageAlt: "Golden sandstone fort and desert landscape in Rajasthan",
    },
    {
        id: 4,
        location: "Kerala",
        region: "Alleppey, Kerala",
        eyebrow: "NATURE GETAWAYS",
        title: "Slow Down. Breathe in Nature.",
        description:
            "Sail through peaceful backwaters surrounded by lush tropical greenery.",
        image: "/images/home/hero/kerala.png",
        imageAlt: "Traditional houseboat cruising through Kerala backwaters",
    },
    {
        id: 5,
        location: "Varanasi",
        region: "Uttar Pradesh, India",
        eyebrow: "CULTURE & SPIRITUALITY",
        title: "Experience the Soul of India.",
        description:
            "Witness timeless traditions, beautiful ghats and magical riverfront mornings.",
        image: "/images/home/hero/varanasi.png",
        imageAlt: "Sunrise over the historic ghats of Varanasi",
    },
];

const AUTO_SLIDE_INTERVAL = 5500;

const HeroBannerCarousel = () => {
    const [activeIndex, setActiveIndex] = useState(0);
    const [isPaused, setIsPaused] = useState(false);

    const activeBanner = heroBanners[activeIndex];

    const showNextBanner = () => {
        setActiveIndex((currentIndex) =>
            (currentIndex + 1) % heroBanners.length
        );
    };

    const showPreviousBanner = () => {
        setActiveIndex((currentIndex) =>
            (currentIndex - 1 + heroBanners.length) %
            heroBanners.length
        );
    };

    const goToBanner = (index: number) => {
        setActiveIndex(index);
    };

    useEffect(() => {
        if (isPaused) return;

        const intervalId = window.setInterval(() => {
            showNextBanner();
        }, AUTO_SLIDE_INTERVAL);

        return () => {
            window.clearInterval(intervalId);
        };
    }, [isPaused]);

    return (
        <section
            aria-label="Featured Indian travel destinations"
            className="relative isolate h-[560px] w-full overflow-hidden bg-slate-950 sm:h-[590px] lg:h-[640px]"
            onMouseEnter={() => setIsPaused(true)}
            onMouseLeave={() => setIsPaused(false)}
        >
            {/* Background Images */}
            {heroBanners.map((banner, index) => (
                <img
                    key={banner.id}
                    src={banner.image}
                    alt={banner.imageAlt}
                    loading={index === 0 ? "eager" : "lazy"}
                    fetchPriority={index === 0 ? "high" : "auto"}
                    draggable={false}
                    className={`absolute inset-0 h-full w-full object-cover object-center transition-opacity duration-1000 ease-in-out ${
                        activeIndex === index
                            ? "z-0 opacity-100"
                            : "z-[-1] opacity-0"
                    }`}
                />
            ))}

            {/* Image Overlays */}
            <div className="absolute inset-0 -z-0 bg-gradient-to-r from-black/75 via-black/40 to-black/5" />

            <div className="absolute inset-x-0 bottom-0 -z-0 h-2/3 bg-gradient-to-t from-black/55 via-transparent to-transparent" />

            {/* Hero Content */}
            <div className="relative z-10 mx-auto flex h-full max-w-[1440px] items-start px-5 pt-16 sm:px-8 sm:pt-20 lg:px-10 lg:pt-24">

                <div
                    className="max-w-2xl"
                    key={activeBanner.id}
                >
                    {/* Eyebrow */}
                    <div className="mb-5 inline-flex items-center gap-2 rounded-full border border-white/30 bg-black/20 px-4 py-2 backdrop-blur-md">
                        <span className="h-2 w-2 rounded-full bg-cyan-400" />

                        <span className="text-xs font-bold tracking-[0.16em] text-white sm:text-sm">
                            {activeBanner.eyebrow}
                        </span>
                    </div>

                    {/* Destination */}
                    <p className="mb-3 text-sm font-semibold tracking-wide text-white/85 sm:text-base">
                        {activeBanner.location}
                        <span className="mx-2 text-white/50">·</span>
                        {activeBanner.region}
                    </p>

                    {/* Heading */}
                    <h1 className="max-w-xl text-4xl font-extrabold leading-tight tracking-tight text-white sm:text-5xl lg:text-6xl">
                        {activeBanner.title}
                    </h1>

                    {/* Description */}
                    <p className="mt-5 max-w-lg text-sm leading-7 text-white/85 sm:text-base sm:leading-8">
                        {activeBanner.description}
                    </p>

                    {/* Explore CTA */}
                    <Link
                        to="/explore"
                        className="mt-7 inline-flex min-h-12 items-center gap-2 rounded-full bg-white px-6 py-3 text-sm font-bold text-slate-900 shadow-lg transition-all duration-200 hover:-translate-y-0.5 hover:bg-slate-100 hover:shadow-xl focus-visible:outline-2 focus-visible:outline-offset-4 focus-visible:outline-white"
                    >
                        Explore Destination

                        <ArrowRight
                            size={17}
                            aria-hidden="true"
                        />
                    </Link>
                </div>
            </div>

            {/* Carousel Controls */}
            <div className="absolute bottom-7 right-5 z-20 flex items-center gap-2 sm:right-8 lg:right-10">

                {/* Previous */}
                <button
                    type="button"
                    onClick={showPreviousBanner}
                    aria-label="Previous destination"
                    className="inline-flex h-10 w-10 items-center justify-center rounded-full border border-white/30 bg-black/25 text-white backdrop-blur-md transition-colors hover:bg-white/20 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-white"
                >
                    <ChevronLeft size={21} />
                </button>

                {/* Next */}
                <button
                    type="button"
                    onClick={showNextBanner}
                    aria-label="Next destination"
                    className="inline-flex h-10 w-10 items-center justify-center rounded-full border border-white/30 bg-black/25 text-white backdrop-blur-md transition-colors hover:bg-white/20 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-white"
                >
                    <ChevronRight size={21} />
                </button>

            </div>
        </section>
    );
};

export default HeroBannerCarousel;
