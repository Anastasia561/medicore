import {useState, useEffect} from 'react';
import axios from '../api/axios.js';
import useAuth from '../hooks/useAuth';
import {useNavigate, useLocation} from 'react-router-dom';
import {jwtDecode} from "jwt-decode";
import logo from '../../public/logo.png';

const LOGIN_URL = '/auth/login';

const Login = () => {
    const {setAuth} = useAuth();

    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/home";

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        setError('');
    }, [username, password]);

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            const response = await axios.post(LOGIN_URL,
                JSON.stringify({email: username, password}), {
                    headers: {'Content-Type': 'application/json'},
                    withCredentials: true
                });
            console.log(JSON.stringify(response?.data));
            const accessToken = response?.data?.accessToken;
            const decoded = jwtDecode(accessToken);

            setAuth({username, accessToken, role: decoded.role});

            setUsername('');
            setPassword('');
            navigate(from, {replace: true});
        } catch (err) {
            if (!err?.response) {
                setError('No response');
            } else if (err.response.status === 400) {
                setError('Validation failed');
            } else if (err.response.status === 401) {
                setError('Invalid email or password');
            } else if (err.response.status === 403) {
                navigate("/unauthorized");
            } else {
                setError('Login failed');
            }
        }
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