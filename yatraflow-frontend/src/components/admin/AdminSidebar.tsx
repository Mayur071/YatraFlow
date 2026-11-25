
import { NavLink } from "react-router-dom";

const navigationItems = [
    { label: "Dashboard", path: "/admin", icon: "▦" },
    { label: "Locations", path: "/admin/locations", icon: "⌖" },
    { label: "Hotels", path: "/admin/hotels", icon: "⌂" },
    { label: "Flights", path: "/admin/flights", icon: "✈" },
    { label: "Trains", path: "/admin/trains", icon: "▤" },
    { label: "Buses", path: "/admin/buses", icon: "▣" },
    { label: "Cabs", path: "/admin/cabs", icon: "▰" },
    { label: "Users", path: "/admin/users", icon: "♙" },
];

const AdminSidebar = () => {
    return (
        <aside className="flex h-screen w-64 flex-col border-r border-gray-200 bg-white">
            <div className="flex h-20 items-center gap-3 border-b border-gray-200 px-6">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-blue-600 font-bold text-white">
                    YF
                </div>

                <div>
                    <h1 className="font-semibold text-gray-900">YatraFlow</h1>
                    <p className="text-xs text-gray-500">Admin Panel</p>
                </div>
            </div>

            <nav className="flex-1 space-y-1 overflow-y-auto px-4 py-6">
                <p className="mb-3 px-3 text-xs font-semibold uppercase tracking-wider text-gray-400">
                    Management
                </p>

                {navigationItems.map((item) => (
                    <NavLink
                        key={item.path}
                        to={item.path}
                        end={item.path === "/admin"}
                        className={({ isActive }) =>
                            `flex items-center gap-3 rounded-lg px-3 py-3 text-sm font-medium transition-colors ${
                                isActive
                                    ? "bg-blue-50 text-blue-700"
                                    : "text-gray-600 hover:bg-gray-100 hover:text-gray-900"
                            }`
                        }
                    >
                        <span className="flex w-5 items-center justify-center text-lg">
                            {item.icon}
                        </span>

                        {item.label}
                    </NavLink>
                ))}
            </nav>

            <div className="border-t border-gray-200 p-4">
                <div className="rounded-lg bg-gray-50 p-3">
                    <p className="text-sm font-medium text-gray-800">
                        Administration
                    </p>
                    <p className="mt-1 text-xs text-gray-500">
                        Manage YatraFlow platform
                    </p>
                </div>
            </div>
        </aside>
    );
};

export default AdminSidebar;
