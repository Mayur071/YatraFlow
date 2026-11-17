
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

const OAuthSuccessPage = () => {

    const navigate = useNavigate();
    const { refreshUser } = useAuth();

    const [error, setError] = useState(false);

    useEffect(() => {
        let active = true;

        const completeOAuthLogin = async () => {
            try {
                await refreshUser();

                if (active) {
                    navigate("/", { replace: true });
                }
            } catch {
                if (active) {
                    setError(true);
                }
            }
        };

        completeOAuthLogin();

        return () => {
            active = false;
        };
    }, [refreshUser, navigate]);

    return (
        <div className="flex min-h-screen items-center justify-center">
            <div className="text-center">
                {error ? (
                    <>
                        <p className="text-sm font-medium text-danger">
                            Sign-in could not be completed.
                        </p>

                        <button
                            onClick={() => navigate("/sign-in", { replace: true })}
                            className="mt-3 text-sm font-semibold text-brand-primary"
                        >
                            Back to Sign In
                        </button>
                    </>
                ) : (
                    <p className="text-sm font-medium text-text-secondary">
                        Completing your sign-in...
                    </p>
                )}
            </div>
        </div>
    );
};

export default OAuthSuccessPage;
