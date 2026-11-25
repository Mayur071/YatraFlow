
import { BrowserRouter, Route, Routes } from "react-router-dom";

import PublicLayout from "../layouts/PublicLayout";
import HeaderOnlyLayout from "../layouts/HeaderOnlyLayout";
import AuthLayout from "../components/auth/AuthLayout";
import AdminLayout from "../layouts/Adminlayout";

import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";
import EmailOtpVerificationPage from "../pages/auth/EmailOtpVerificationPage";
import OAuthSuccessPage from "../pages/auth/OAuthSuccessPage";

import HomePage from "../pages/dashboard/HomePage";
import AdminDashboardPage from "../pages/admin/AdminDashboardPage";
import AdminModulePage from "../pages/admin/AdminModulePage";

import { AuthProvider } from "../context/AuthProvider";
import AdminRoute from "./AdminRoute";

const AppRouter = () => {
    return (
        <BrowserRouter>
            <AuthProvider>
                <Routes>

                    {/* Public Pages */}
                    <Route element={<PublicLayout />}>
                        <Route path="/" element={<HomePage />} />
                    </Route>

                    {/* Authentication */}
                    <Route element={<AuthLayout />}>
                        <Route path="/sign-in" element={<LoginPage />} />
                        <Route path="/sign-up" element={<RegisterPage />} />

                        <Route
                            path="/verify-email"
                            element={<EmailOtpVerificationPage />}
                        />

                        <Route
                            path="/oauth2/success"
                            element={<OAuthSuccessPage />}
                        />
                    </Route>

                    {/* Protected Admin Routes */}
                    <Route element={<AdminRoute />}>
                        <Route element={<AdminLayout />}>

                            <Route
                                path="/admin"
                                element={<AdminDashboardPage />}
                            />

                            <Route
                                path="/admin/locations"
                                element={
                                    <AdminModulePage
                                        title="Locations"
                                        description="Manage destinations, cities, states and travel locations."
                                    />
                                }
                            />

                            <Route
                                path="/admin/hotels"
                                element={
                                    <AdminModulePage
                                        title="Hotels"
                                        description="Manage hotel listings, properties and accommodation data."
                                    />
                                }
                            />

                            <Route
                                path="/admin/flights"
                                element={
                                    <AdminModulePage
                                        title="Flights"
                                        description="Manage flight data, routes and airline information."
                                    />
                                }
                            />

                            <Route
                                path="/admin/trains"
                                element={
                                    <AdminModulePage
                                        title="Trains"
                                        description="Manage train information, routes and schedules."
                                    />
                                }
                            />

                            <Route
                                path="/admin/buses"
                                element={
                                    <AdminModulePage
                                        title="Buses"
                                        description="Manage bus operators, routes and travel data."
                                    />
                                }
                            />

                            <Route
                                path="/admin/cabs"
                                element={
                                    <AdminModulePage
                                        title="Cabs"
                                        description="Manage cab services, providers and intercity travel."
                                    />
                                }
                            />

                            <Route
                                path="/admin/users"
                                element={
                                    <AdminModulePage
                                        title="Users"
                                        description="Manage YatraFlow users and account information."
                                    />
                                }
                            />

                        </Route>
                    </Route>

                    {/* Header Only Pages */}
                    <Route element={<HeaderOnlyLayout />}>
                        <Route path="/flights" element={<div>Flights</div>} />
                        <Route path="/hotels" element={<div>Hotels</div>} />
                        <Route path="/trains" element={<div>Trains</div>} />
                        <Route path="/buses" element={<div>Buses</div>} />
                        <Route path="/cabs" element={<div>Cabs</div>} />
                        <Route path="/explore" element={<div>Explore</div>} />
                        <Route path="/trips" element={<div>My Trips</div>} />

                        <Route
                            path="*"
                            element={<div>Page Not Found</div>}
                        />
                    </Route>

                </Routes>
            </AuthProvider>
        </BrowserRouter>
    );
};

export default AppRouter;
