
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

const AdminProtectedRoute = () => {
    const { user, isAuthenticated, isLoading } = useAuth();

    if (isLoading) {
        return (
            <div className="flex min-h-screen items-center justify-center">
                <p className="text-gray-600">Checking authentication...</p>
            </div>
        );
    }

    if (!isAuthenticated || !user) {
        return <Navigate to="/sign-in" replace />;
    }

    const isAdmin = user.roles.includes("ROLE_ADMIN");

    if (!isAdmin) {
        return <Navigate to="/" replace />;
    }

    return <Outlet />;
};

export default AdminProtectedRoute;
