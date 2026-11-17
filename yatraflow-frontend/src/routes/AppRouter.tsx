
import { BrowserRouter, Route, Routes } from "react-router-dom";

import PublicLayout from "../layouts/PublicLayout";
import HeaderOnlyLayout from "../layouts/HeaderOnlyLayout";

import AuthLayout from "../components/auth/AuthLayout";
import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";
import { AuthProvider } from "../context/AuthProvider";

import HomePage from "../pages/dashboard/HomePage";
import EmailOtpVerificationPage from "../pages/auth/EmailOtpVerificationPage";

import OAuthSuccessPage from "../pages/auth/OAuthSuccessPage";

const AppRouter = () => {
    return (
        <BrowserRouter>
            <AuthProvider>
            <Routes>

                {/* Pages with Header and Footer */}
                <Route element={<PublicLayout />}>
                    <Route path="/" element={<HomePage />} />

                </Route>

                {/* Authentication pages */}
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


                {/* Pages with Header only */}
                <Route element={<HeaderOnlyLayout />}>
                    <Route path="/flights" element={<div>Flights</div>} />
                    <Route path="/hotels" element={<div>Hotels</div>} />
                    <Route path="/trains" element={<div>Trains</div>} />
                    <Route path="/buses" element={<div>Buses</div>} />
                    <Route path="/cabs" element={<div>Cabs</div>} />
                    <Route path="/explore" element={<div>Explore</div>} />
                    <Route path="/trips" element={<div>My Trips</div>} />

                    {/* Unknown routes */}
                    <Route path="*" element={<div>Page Not Found</div>} />
                </Route>

            </Routes>
          </AuthProvider>
        </BrowserRouter>



    );
};

export default AppRouter;
