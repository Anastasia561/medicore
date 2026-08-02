import {Link} from 'react-router-dom';
import banner from '../assets/banner.png';
import logo from '../assets/logo.png';

const PublicHome = () => {
    return (
        <div className="min-vh-100 bg-body-tertiary">
            <header className="bg-primary px-3 px-md-5 py-3">
                <Link to="/" className="d-inline-flex align-items-center gap-3 text-decoration-none">
                    <img src={logo} alt="MediCore Logo" width="100" height="70"/>
                    <div>
                        <h1 className="h3 mb-0 text-white">MediCore</h1>
                        <span className="text-white-50 small">Healthcare made simple</span>
                    </div>
                </Link>
            </header>

            <main className="container py-5">
                <div className="row align-items-center g-4">
                    <div className="col-lg-6">
                        <p className="d-inline-block px-3 py-1 rounded-pill bg-primary-subtle text-primary fw-semibold mb-3">
                            Welcome to MediCore
                        </p>
                        <h2 className="display-5 fw-bold mb-3 text-primary-emphasis">
                            Reliable care, appointments, and records in one place
                        </h2>
                        <p className="lead mb-4">
                            Manage your healthcare experience with a clean, secure platform for patients and doctors.
                        </p>
                        <div className="d-flex gap-3 flex-wrap">
                            <Link to="/login" className="btn btn-primary btn-lg px-4">
                                Login
                            </Link>
                            <Link to="/register" className="btn btn-outline-primary btn-lg px-4">
                                Register
                            </Link>
                        </div>
                    </div>

                    <div className="col-lg-6">
                        <div className="rounded-4 overflow-hidden shadow-lg">
                            <img src={banner} alt="MediCore banner" className="img-fluid w-100"/>
                        </div>
                    </div>
                </div>
            </main>
        </div>
    );
};

export default PublicHome;
