import {useState} from 'react';
import useAuth from '../../hooks/useAuth.jsx';
import {Link, useNavigate} from 'react-router-dom';
import logo from '../../assets/logo.png';
import {useLogin} from "./hooks/useLogin.jsx";
import {loginSchema} from "./validation/loginSchema.js";
import {useForm} from 'react-hook-form';
import {yupResolver} from "@hookform/resolvers/yup";

const Login = () => {
    const {setAuth} = useAuth();
    const navigate = useNavigate();

    const [generalError, setGeneralError] = useState('');
    const [showPassword, setShowPassword] = useState(false);

    const {
        register, handleSubmit,
        setError, formState: {errors}
    } = useForm({
        resolver: yupResolver(loginSchema),
    });

    const loginMutation = useLogin({
        setAuth, navigate, setGeneralError, setFormError: setError,
    });

    const onSubmit = async (data) => {
        loginMutation.mutate({
            email: data.username,
            password: data.password,
        });
    };

    return (
        <div className="test text-center mt-4 mb-5">

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
                        {generalError && (
                            <div className="alert alert-danger py-2 text-center" role="alert">
                                {generalError}
                            </div>
                        )}

                        <h4 className="mb-4">Log in</h4>

                        <form onSubmit={handleSubmit(onSubmit)} noValidate className="text-start">
                            <div className="mb-3">
                                <label htmlFor="username" className="form-label">
                                    Username
                                </label>
                                <input
                                    {...register("username")}
                                    id="username"
                                    className={`form-control ${errors.username ? 'is-invalid' : ''}`}
                                    type="text"
                                    disabled={loginMutation.isPending}
                                />
                                {errors.username && <div className="invalid-feedback">{errors.username.message}</div>}
                            </div>

                            <div className="mb-3">
                                <label htmlFor="password" className="form-label">
                                    Password
                                </label>
                                <div className={`input-group ${errors.password ? 'has-validation' : ''}`}>
                                    <input
                                        {...register("password")}
                                        id="password"
                                        className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                                        type={showPassword ? 'text' : 'password'}
                                        disabled={loginMutation.isPending}
                                    />
                                    <button
                                        type="button"
                                        className="btn btn-outline-secondary"
                                        onClick={() => setShowPassword((prev) => !prev)}
                                        disabled={loginMutation.isPending}
                                        aria-label={showPassword ? "Hide password" : "Show password"}
                                    >
                                        <i className={`bi ${showPassword ? 'bi-eye' : 'bi-eye-slash'}`}></i>
                                    </button>
                                    {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
                                </div>
                            </div>

                            <div className="d-grid">
                                <button className="btn btn-primary" disabled={loginMutation.isPending}>
                                    {loginMutation.isPending ? 'Logging in...' : 'Log in'}
                                </button>
                            </div>

                            <div className="text-center mt-3">
                                <Link to="/reset-password/request" className="small text-decoration-none">
                                    Forgot password?
                                </Link>
                            </div>

                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Login;