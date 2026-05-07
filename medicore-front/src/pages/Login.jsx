import {useState} from 'react';
import useAuth from '../hooks/auth/useAuth.jsx';
import {useNavigate, useLocation} from 'react-router-dom';
import logo from '../../public/logo.png';
import {useLogin} from "../hooks/auth/useLogin.jsx";
import * as yup from 'yup';
import {useForm} from 'react-hook-form';
import {yupResolver} from "@hookform/resolvers/yup/src/index.ts";

const Login = () => {
    const {setAuth} = useAuth();
    const navigate = useNavigate();
    const location = useLocation();
    const from = location.state?.from?.pathname || "/home";

    const [generalError, setGeneralError] = useState('');

    const schema = yup.object().shape({
        username: yup.string().email().required('Username is required'),
        password: yup.string().required('Password is required'),
    })

    const {
        register, handleSubmit,
        setError, formState: {errors}
    } = useForm({
        resolver: yupResolver(schema),
    })

    const loginMutation = useLogin({
        setAuth, navigate, from, setGeneralError, setFormError: setError,
    });

    const onSubmit = async (data) => {
        loginMutation.mutate({
            email: data.username,
            password: data.password,
        });
    }

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

                        <form onSubmit={handleSubmit(onSubmit)}>
                            <div className="mb-3">
                                <label htmlFor="username" className="form-label">
                                    Username
                                </label>
                                <input
                                    {...register("username")}
                                    id="username"
                                    className={`form-control ${errors.username ? 'is-invalid' : ''}`}
                                    type="text"
                                />
                                {errors.username && <div className="invalid-feedback">{errors.username.message}</div>}
                            </div>


                            <div className="mb-3">
                                <label htmlFor="password" className="form-label">
                                    Password
                                </label>
                                <input
                                    {...register("password")}
                                    id="password"
                                    className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                                    type="password"
                                />
                                {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
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