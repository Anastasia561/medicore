import {useState, useEffect} from 'react';
import axios from '../api/axios.js';
import useAuth from '../hooks/useAuth';
import {Link, useNavigate, useLocation} from 'react-router-dom';
import {jwtDecode} from "jwt-decode";

const LOGIN_URL = '/auth/login';

const Login = () => {
    const {setAuth} = useAuth();

    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/";

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
            } else if (err?.response?.status === 400) {
                setError('Validation failed');
            } else if (err?.response?.status === 401) {
                setError('Unauthorized');
            } else {
                setError('Login failed');
            }
        }
    }

    return (
        <>
            <p>{error}</p>
            <h1>Sign in</h1>
            <form onSubmit={handleSubmit}>
                <label htmlFor="username">Username: </label>
                <input type="text"
                       id="username"
                       onChange={(e) => setUsername(e.target.value)}
                       value={username}
                       required
                />
                <br/>
                <label htmlFor="password">Password: </label>
                <input type="password"
                       id="password"
                       onChange={(e) => setPassword(e.target.value)}
                       value={password}
                       required
                />
                <br/>
                <button>Sign in</button>
            </form>
        </>
    )
}

export default Login