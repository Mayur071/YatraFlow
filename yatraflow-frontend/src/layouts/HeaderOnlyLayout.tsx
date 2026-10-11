import { Outlet } from "react-router-dom";
import Header from "../components/layout/Header";

const HeaderOnlyLayout = () => {

    return (
        <div className="flex min-h-screen flex-col bg-background text-text-primary">
            <Header />

            <main className ="flex-1">

                <Outlet/>

            </main>
        </div>
    );
};

export default HeaderOnlyLayout;

