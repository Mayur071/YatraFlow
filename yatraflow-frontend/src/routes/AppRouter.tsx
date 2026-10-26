
import { BrowserRouter, Route, Routes } from "react-router-dom";
import PublicLayout from "../layouts/PublicLayout";
import HeaderOnlyLayout from "../layouts/HeaderOnlyLayout";
import DestinationTicker from "../components/home/DestinationTicker";

import AuthLayout from "../components/auth/AuthLayout";
import LoginPage from "../pages/auth/LoginPage";
import RegisterPage from "../pages/auth/RegisterPage";

const AppRouter = () => {
    return (
        <BrowserRouter>
            <Routes>
                {/* Pages with Header and Footer */}
                {/* Pages with Header and Footer */}
                <Route element={<PublicLayout />}>
                    <Route path="/" element={<DestinationTicker />} />
                    <Route path="/verify-email" element={<div>Verify Email</div>} />
                </Route>

                {/* Authentication pages */}
                <Route element={<AuthLayout />}>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
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
        </BrowserRouter>
    );
};

export default AppRouter;
