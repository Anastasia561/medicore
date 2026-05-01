import "../../styles/layout/Footer.css";

const Footer = () => {
    return (
        <footer className="footer">
            <p className="footer-text">
                © {new Date().getFullYear()} MediCore Healthcare System
            </p>
        </footer>
    );
};

export default Footer;