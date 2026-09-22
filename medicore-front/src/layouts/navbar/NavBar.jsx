import {useEffect, useState} from "react";
import {NavLink, useLocation, useNavigate} from "react-router-dom";
import "./NavBar.css";
import logo from "../../assets/logo.png";
import {useLogout} from "../../features/auth/hooks/useLogout.jsx";
import useAuth from "../../hooks/useAuth.jsx";
import {getHomePath} from "../../utils/homePath.js";

const NavBar = () => {
    const {mutateAsync: logout} = useLogout();
    const navigate = useNavigate();
    const location = useLocation();
    const {auth} = useAuth();
    const landingPath = getHomePath(auth?.role);
    const [menuOpen, setMenuOpen] = useState(false);

    const navLinks = [
        {to: "/statistics", label: "Statistics", roles: ["ROLE_ADMIN"]},
        {to: "/patients", label: "Patients", roles: ["ROLE_ADMIN", "ROLE_DOCTOR"]},
        {to: "/doctors", label: "Doctors", roles: ["ROLE_ADMIN", "ROLE_PATIENT"]},
        {to: "/appointments", label: "Appointments", roles: ["ROLE_PATIENT", "ROLE_DOCTOR"]},
        {to: "/records", label: "Records", roles: ["ROLE_PATIENT", "ROLE_DOCTOR"]},
        {to: "/lab-results", label: "Lab results", roles: ["ROLE_PATIENT"]},
        {to: "/medical-profile", label: "Medical profile", roles: ["ROLE_PATIENT"]},
        {to: "/schedule", label: "Schedule", roles: ["ROLE_DOCTOR"]}
    ];

    useEffect(() => {
        setMenuOpen(false);
    }, [location.pathname]);

    useEffect(() => {
        const onResize = () => {
            if (window.innerWidth > 992) {
                setMenuOpen(false);
            }
        };
        window.addEventListener("resize", onResize);
        return () => window.removeEventListener("resize", onResize);
    }, []);

    const handleLogout = async () => {
        setMenuOpen(false);
        await logout();
        navigate("/");
    };

    const visibleLinks = navLinks.filter(link => link.roles.includes(auth?.role));

    return (
        <div className={`header ${menuOpen ? "menu-open" : ""}`}>
            <div className="header-bar">
                <NavLink to={landingPath} className="header-left" onClick={() => setMenuOpen(false)}>
                    <img src={logo} alt="Logo" className="logo"/>
                    <h1 className="title">MediCore</h1>
                </NavLink>

                <button
                    type="button"
                    className="menu-toggle"
                    aria-label={menuOpen ? "Close menu" : "Open menu"}
                    aria-expanded={menuOpen}
                    onClick={() => setMenuOpen(open => !open)}
                >
                    <i className={`fas ${menuOpen ? "fa-times" : "fa-bars"}`} aria-hidden="true"/>
                </button>

                <nav className={`header-nav ${menuOpen ? "is-open" : ""}`}>
                    <div className="nav-links">
                        {visibleLinks.map(link => (
                            <NavLink key={link.label} to={link.to} className="nav-item">
                                {link.label}
                            </NavLink>
                        ))}
                    </div>

                    <div className="nav-actions">
                        <NavLink to="/profile" className="nav-item">
                            <i className="fas fa-user-circle"></i> Profile
                        </NavLink>

                        <button onClick={handleLogout} className="nav-item logout-btn">
                            <i className="fas fa-sign-out-alt"></i> Log out
                        </button>
                    </div>
                </nav>
            </div>
        </div>
    );
};

export default NavBar;
