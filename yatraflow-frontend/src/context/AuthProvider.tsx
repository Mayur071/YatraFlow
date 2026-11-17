
import {
    createContext,
    useCallback,
    useEffect,
    useState,
    type ReactNode,
} from "react";

import {
    getCurrentUser,
    loginUser,
    logoutUser,
    type CurrentUserResponse,
    type LoginRequest,
} from "../services/authServices";

interface AuthContextType {
    user: CurrentUserResponse | null;
    isAuthenticated: boolean;
    isLoading: boolean;
    login: (payload: LoginRequest) => Promise<void>;
    logout: () => Promise<void>;
    refreshUser: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType | undefined>(
    undefined
);

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider = ({ children }: AuthProviderProps) => {

    const [user, setUser] = useState<CurrentUserResponse | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    const refreshUser = useCallback(async () => {
        try {
            const currentUser = await getCurrentUser();
            setUser(currentUser);
        } catch (error) {
            setUser(null);
            throw error;
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        refreshUser().catch(() => {
            // No active session is a normal state for guest users.
        });
    }, [refreshUser]);

    const login = async (payload: LoginRequest): Promise<void> => {
        await loginUser(payload);

        const currentUser = await getCurrentUser();
        setUser(currentUser);
    };

    const logout = async (): Promise<void> => {
        try {
            await logoutUser();
        } finally {
            setUser(null);
        }
    };

    const value: AuthContextType = {
        user,
        isAuthenticated: user !== null,
        isLoading,
        login,
        logout,
        refreshUser,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
