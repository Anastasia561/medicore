import {useNavigate} from "react-router-dom";

const Unauthorized = () => {
    const navigate = useNavigate();

    return (
        <div className="d-flex flex-column justify-content-center align-items-center vh-100 text-center bg-light">

            <h1 className="display-1 fw-bold text-danger">403</h1>

            <h2 className="mb-3">Access Denied</h2>

            <p className="text-muted mb-4">
                You don’t have permission to view this page.
            </p>

            <button onClick={() => navigate("/home")} className="btn btn-primary">
                Go Home
            </button>
        </div>
    );
};

export default Unauthorized;