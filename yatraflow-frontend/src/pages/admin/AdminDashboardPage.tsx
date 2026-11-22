
import { Link } from "react-router-dom";

const modules = [
    {
        title: "Hotels",
        description: "Manage hotel listings and property details",
        path: "/admin/hotels",
        icon: "🏨",
    },
    {
        title: "Flights",
        description: "Manage flight information and routes",
        path: "/admin/flights",
        icon: "✈️",
    },
    {
        title: "Trains",
        description: "Manage trains, stations and routes",
        path: "/admin/trains",
        icon: "🚆",
    },
    {
        title: "Buses",
        description: "Manage buses, operators and routes",
        path: "/admin/buses",
        icon: "🚌",
    },
    {
        title: "Cabs",
        description: "Manage cab services and trip routes",
        path: "/admin/cabs",
        icon: "🚕",
    },
    {
        title: "Locations",
        description: "Manage cities and travel destinations",
        path: "/admin/locations",
        icon: "📍",
    },
];

const AdminDashboardPage = () => {
    return (
        <div className="space-y-8">

            {/* Page Heading */}
            <div>
                <h1 className="text-2xl font-bold text-gray-900">
                    Dashboard Overview
                </h1>

                <p className="mt-2 text-sm text-gray-500">
                    Manage and monitor the YatraFlow travel platform.
                </p>
            </div>

            {/* Overview Cards */}
            <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 xl:grid-cols-4">

                {[
                    { label: "Hotels", icon: "🏨" },
                    { label: "Flights", icon: "✈️" },
                    { label: "Train Routes", icon: "🚆" },
                    { label: "Bus Routes", icon: "🚌" },
                ].map((item) => (
                    <div
                        key={item.label}
                        className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm"
                    >
                        <div className="flex items-center justify-between">
                            <p className="text-sm font-medium text-gray-500">
                                {item.label}
                            </p>

                            <span className="text-2xl">{item.icon}</span>
                        </div>

                        <p className="mt-4 text-2xl font-semibold text-gray-900">
                            --
                        </p>

                        <p className="mt-2 text-xs text-gray-400">
                            Data will be available after integration
                        </p>
                    </div>
                ))}
            </div>

            {/* Management Modules */}
            <section>
                <div className="mb-5">
                    <h2 className="text-lg font-semibold text-gray-900">
                        Travel Management
                    </h2>

                    <p className="mt-1 text-sm text-gray-500">
                        Manage travel services and destinations across India.
                    </p>
                </div>

                <div className="grid grid-cols-1 gap-5 md:grid-cols-2 xl:grid-cols-3">

                    {modules.map((module) => (
                        <Link
                            key={module.title}
                            to={module.path}
                            className="group rounded-xl border border-gray-200 bg-white p-6 shadow-sm transition hover:border-blue-300 hover:shadow-md"
                        >
                            <div className="flex items-start justify-between">
                                <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gray-50 text-2xl">
                                    {module.icon}
                                </div>

                                <span className="text-gray-400 transition group-hover:translate-x-1 group-hover:text-blue-600">
                                    →
                                </span>
                            </div>

                            <h3 className="mt-5 font-semibold text-gray-900">
                                {module.title}
                            </h3>

                            <p className="mt-2 text-sm text-gray-500">
                                {module.description}
                            </p>
                        </Link>
                    ))}
                </div>
            </section>

            {/* Activity Placeholder */}
            <section className="rounded-xl border border-gray-200 bg-white p-6 shadow-sm">
                <h2 className="text-lg font-semibold text-gray-900">
                    Recent Activity
                </h2>

                <p className="mt-1 text-sm text-gray-500">
                    Administrative activities will appear here.
                </p>

                <div className="mt-6 flex min-h-32 items-center justify-center rounded-lg bg-gray-50">
                    <p className="text-sm text-gray-400">
                        No activity recorded yet.
                    </p>
                </div>
            </section>

        </div>
    );
};

export default AdminDashboardPage;
