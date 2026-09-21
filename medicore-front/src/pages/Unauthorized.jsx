import {useNavigate} from "react-router-dom";
import useAuth from "../hooks/useAuth.jsx";
import {getHomePath} from "../utils/homePath.js";

const Unauthorized = () => {
    const navigate = useNavigate();
    const {auth} = useAuth();

    return (
        <div className="d-flex flex-column justify-content-center align-items-center vh-100 text-center bg-light">

            <h1 className="display-1 fw-bold text-danger">403</h1>

            <h2 className="mb-3">Access Denied</h2>

            <p className="text-muted mb-4">
                You don’t have permission to view this page.
            </p>

            <button onClick={() => navigate(getHomePath(auth?.role))} className="btn btn-primary">
                Go Home
            </button>
        </div>
    );
};

export default Unauthorized;