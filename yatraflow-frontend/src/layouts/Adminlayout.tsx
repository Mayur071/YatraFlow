
import { Link, Outlet } from "react-router-dom";
import AdminSidebar from "../components/admin/AdminSidebar.tsx";

const AdminLayout = () => {
    return (
        <div className="flex min-h-screen bg-gray-50">
            <AdminSidebar />

            <div className="flex min-w-0 flex-1 flex-col">
                <header className="flex h-20 items-center justify-between border-b border-gray-200 bg-white px-8">
                    <div>
                        <h2 className="text-lg font-semibold text-gray-900">
                            Admin Workspace
                        </h2>
                        <p className="text-sm text-gray-500">
                            Manage YatraFlow travel services
                        </p>
                    </div>

                    <Link
                        to="/"
                        className="rounded-lg border border-gray-200 px-4 py-2 text-sm font-medium text-gray-700 transition hover:bg-gray-100"
                    >
                        Back to Website
                    </Link>
                </header>

                <main className="flex-1 overflow-y-auto p-8">
                    <Outlet />
                </main>
            </div>
        </div>
    );
};

export default AdminLayout;
