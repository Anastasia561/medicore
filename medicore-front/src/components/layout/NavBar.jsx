import {NavLink, useNavigate} from "react-router-dom";
import "../../styles/layout/NavBar.css";
import logo from "../../../public/logo.png";
import useLogout from "../../hooks/useLogout.jsx";
import useAuth from "../../hooks/useAuth.jsx";

const NavBar = () => {
    const logout = useLogout();
    const navigate = useNavigate();
    const {auth} = useAuth();

    const navLinks = [
        {to: "/home", label: "Home", roles: ["ROLE_ADMIN", "ROLE_PATIENT", "ROLE_DOCTOR"]},
        {to: "/hh", label: "Statistics", roles: ["ROLE_ADMIN"]},
        {to: "/hh", label: "Patients", roles: ["ROLE_ADMIN", "ROLE_DOCTOR"]},
        {to: "/doctors", label: "Doctors", roles: ["ROLE_ADMIN"]},
        {to: "/hh", label: "Appointments", roles: ["ROLE_PATIENT", "ROLE_DOCTOR"]},
        {to: "/hh", label: "Lab results", roles: ["ROLE_PATIENT"]},
        {to: "/hh", label: "Risks", roles: ["ROLE_PATIENT"]},
        {to: "/hh", label: "Schedule", roles: ["ROLE_DOCTOR"]}
    ];

    const handleLogout = async () => {
        await logout();
        navigate("/");
    };

    return (
        <div className="header">

            <div className="header-left">
                <img src={logo} alt="Logo" className="logo"/>
                <h1 className="title">MediCore</h1>
            </div>

            <nav className="header-nav">

                <div className="nav-links">
                    {navLinks
                        .filter(link => link.roles.includes(auth?.role))
                        .map(link => (
                            <NavLink key={link.to} to={link.to} className="nav-item">
                                {link.label}
                            </NavLink>
                        ))}
                </div>

                <div className="nav-actions">
                    <NavLink to="/admin" className="nav-item">
                        <i className="fas fa-user-circle"></i> Profile
                    </NavLink>

                    <button onClick={handleLogout} className="nav-item logout-btn">
                        <i className="fas fa-sign-out-alt"></i> Log out
                    </button>
                </div>

            </nav>
        </div>
    );
};

export default NavBar;