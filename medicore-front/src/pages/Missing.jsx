import {useNavigate} from "react-router-dom";

const Missing = () => {
    const navigate = useNavigate();

    return (
        <div className="d-flex flex-column justify-content-center align-items-center vh-100 text-center bg-light">

            <h1 className="display-1 fw-bold text-primary">404</h1>

            <h2 className="mb-3">Page Not Found</h2>

            <p className="text-muted mb-4">
                The page you are looking for doesn’t exist or has been moved.
            </p>

            <button onClick={() => navigate("/home")} className="btn btn-primary">
                Go Home
            </button>
        </div>
    );
};

export default Missing;