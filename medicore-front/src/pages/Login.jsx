import {useState} from 'react';
import useAuth from '../hooks/auth/useAuth.jsx';
import {useNavigate, useLocation} from 'react-router-dom';
import logo from '../../public/logo.png';
import {useLogin} from "../hooks/auth/useLogin.jsx";

const Login = () => {
    const {setAuth} = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/home";

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const loginMutation = useLogin({
        setAuth, navigate, from, setError, username,
    });

    const handleSubmit = async (e) => {
        e.preventDefault();

        loginMutation.mutate({
            email: username,
            password,
        });
    }

    return (
        <div className="text-center mt-4 mb-5">

            <div className="mb-4">
                <img
                    src={logo}
                    alt="MediCore Logo"
                    width="100"
                    height="70"
                    className="mb-2"
                />
                <h2 className="fw-bold">MediCore</h2>
            </div>

            <div className="row justify-content-center">
                <div className="col-md-5">
                    <div className="card shadow-lg p-4">
                        {error && (
                            <div className="alert alert-danger py-2 text-center" role="alert">
                                {error}
                            </div>
                        )}

                        <h4 className="mb-4">Log in</h4>

                        <form onSubmit={handleSubmit}>

                            <div className="mb-3">
                                <label htmlFor="username" className="form-label">
                                    Username
                                </label>
                                <input
                                    type="text"
                                    id="username"
                                    className="form-control"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                />
                            </div>

                            <div className="mb-3">
                                <label htmlFor="password" className="form-label">
                                    Password
                                </label>
                                <input
                                    type="password"
                                    id="password"
                                    className="form-control"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                />
                            </div>

                            <div className="d-grid">
                                <button className="btn btn-primary">
                                    Log in
                                </button>
                            </div>

                        </form>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default Login