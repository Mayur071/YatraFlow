import {useTheme} from "../../context/ThemeProvider"

interface YatraFlowLogoProps {
    className?: string;
}

const YatraFlowLogo = ({
                           className = "h-9 w-auto",
                       }: YatraFlowLogoProps) => {
    const { theme } = useTheme();

    const logoSrc =
        theme === "dark"
            ? "/branding/yatraflow-dark.svg"
            : "/branding/yatraflow-light.svg";

    return (
        <img
            src={logoSrc}
            alt="YatraFlow"
            className={className}
            draggable={false}
        />
    );
};

export default YatraFlowLogo;