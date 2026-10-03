import { useTheme } from "./context/ThemeProvider.tsx";

const App = () => {
    const { theme, toggleTheme } = useTheme();

    return (
        <main className="flex min-h-screen items-center justify-center bg-background text-text-primary">
            <div className="text-center">
                <h1 className="font-brand text-4xl font-bold">
                    YatraFlow
                </h1>

                <p className="mt-3 text-text-secondary">
                    Current theme: {theme}
                </p>

                <button
                    type="button"
                    onClick={toggleTheme}
                    className="mt-6 rounded-lg bg-brand-primary px-5 py-3 font-semibold text-white transition-colors hover:bg-brand-hover"
                >
                    Toggle Theme
                </button>
            </div>
        </main>
    );
};

export default App;