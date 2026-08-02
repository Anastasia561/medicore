import {Link} from 'react-router-dom';

const Register = () => {
    return (
        <div className="container min-vh-100 d-flex align-items-center py-5">
            <div className="row justify-content-center">
                <div className="col-md-8 col-lg-6">
                    <div className="card shadow-lg border-0">
                        <div className="card-body p-5 text-center">
                            <span className="badge text-bg-primary mb-3">Coming soon</span>
                            <h2 className="fw-bold mb-3">Registration page placeholder</h2>
                            <p className="text-muted mb-4">
                                The real registration flow will be added here later.
                            </p>
                            <div className="d-flex justify-content-center gap-3 flex-wrap">
                                <Link to="/login" className="btn btn-primary px-4">
                                    Go to login
                                </Link>
                                <Link to="/" className="btn btn-outline-primary px-4">
                                    Back to home
                                </Link>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Register;
