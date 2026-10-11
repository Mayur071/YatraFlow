
import { BrowserRouter, Route, Routes } from "react-router-dom";
import PublicLayout from "../layouts/PublicLayout";
import HeaderOnlyLayout from "../layouts/HeaderOnlyLayout";

const AppRouter = () => {
    return (
        <BrowserRouter>
            <Routes>
                {/* Pages with Header and Footer */}
                <Route element={<PublicLayout />}>
                    <Route path="/" element={<div>YatraFlow Home</div>} />
                    <Route path="/login" element={<div>Login</div>} />
                    <Route path="/register" element={<div>Register</div>} />
                    <Route path="/verify-email" element={<div>Verify Email</div>} />
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
