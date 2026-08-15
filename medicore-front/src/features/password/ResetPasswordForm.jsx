import {useEffect, useState} from 'react';
import {Link, useSearchParams} from 'react-router-dom';
import {useForm} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import {resetPasswordSchema} from './validation/resetPasswordSchema.js';
import {useResetPassword} from './hooks/useResetPassword.jsx';

const ResetPasswordForm = () => {
    const [searchParams] = useSearchParams();
    const [generalError, setGeneralError] = useState('');
    const [showPassword, setShowPassword] = useState(false);
    const [showRepeatPassword, setShowRepeatPassword] = useState(false);
    const token = searchParams.get('token') || '';

    const {
        register,
        handleSubmit,
        setError,
        setValue,
        formState: {errors},
    } = useForm({
        resolver: yupResolver(resetPasswordSchema),
        defaultValues: {
            token,
            email: '',
            password: '',
            repeatPassword: '',
        },
    });

    const resetPasswordMutation = useResetPassword({
        setError,
        setGeneralError
    });

    useEffect(() => {
        setValue('token', token);
    }, [setValue, token]);

    const onSubmit = (data) => {
        resetPasswordMutation.mutate(data);
    };

    return (
        <div className="container py-5">
            <div className="card shadow-sm border-0 mx-auto" style={{maxWidth: '500px'}}>
                <div className="card-body p-4">
                    <h2 className="h4 fw-bold text-center mb-3">Set a new password</h2>

                    {!token && (
                        <div className="alert alert-danger" role="alert">
                            This reset link is missing a token.
                        </div>
                    )}

                    {generalError && (
                        <div className="alert alert-danger" role="alert">
                            {generalError}
                        </div>
                    )}

                    <form onSubmit={handleSubmit(onSubmit)} noValidate>
                        <input type="hidden" {...register("token")} />

                        <div className="mb-3">
                            <label htmlFor="email" className="form-label">
                                Email address
                            </label>
                            <input
                                {...register("email")}
                                id="email"
                                type="email"
                                className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                                disabled={resetPasswordMutation.isPending}
                            />
                            {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
                        </div>

                        <div className="mb-3">
                            <label htmlFor="password" className="form-label">
                                New password
                            </label>
                            <div className={`input-group ${errors.password ? 'has-validation' : ''}`}>
                                <input
                                    {...register("password")}
                                    id="password"
                                    type={showPassword ? 'text' : 'password'}
                                    className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                                    disabled={resetPasswordMutation.isPending}
                                />
                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={() => setShowPassword((prev) => !prev)}
                                    disabled={resetPasswordMutation.isPending}
                                    aria-label={showPassword ? "Hide password" : "Show password"}
                                >
                                    <i className={`bi ${showPassword ? 'bi-eye' : 'bi-eye-slash'}`}></i>
                                </button>
                                {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
                            </div>
                        </div>

                        <div className="mb-3">
                            <label htmlFor="repeatPassword" className="form-label">
                                Confirm new password
                            </label>
                            <div className={`input-group ${errors.repeatPassword ? 'has-validation' : ''}`}>
                                <input
                                    {...register("repeatPassword")}
                                    id="repeatPassword"
                                    type={showRepeatPassword ? 'text' : 'password'}
                                    className={`form-control ${errors.repeatPassword ? 'is-invalid' : ''}`}
                                    disabled={resetPasswordMutation.isPending}
                                />
                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={() => setShowRepeatPassword((prev) => !prev)}
                                    disabled={resetPasswordMutation.isPending}
                                    aria-label={showRepeatPassword ? "Hide password" : "Show password"}
                                >
                                    <i className={`bi ${showRepeatPassword ? 'bi-eye' : 'bi-eye-slash'}`}></i>
                                </button>
                                {errors.repeatPassword &&
                                    <div className="invalid-feedback">{errors.repeatPassword.message}</div>}
                            </div>
                        </div>

                        <div className="d-grid">
                            <button className="btn btn-primary" disabled={resetPasswordMutation.isPending || !token}>
                                {resetPasswordMutation.isPending ? 'Updating...' : 'Reset password'}
                            </button>
                        </div>

                        <div className="text-center mt-3">
                            <Link to="/login" className="small text-decoration-none">
                                Back to login
                            </Link>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default ResetPasswordForm;