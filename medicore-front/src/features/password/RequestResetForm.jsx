import {useState} from 'react';
import {Link} from 'react-router-dom';
import {useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import {requestResetSchema} from "./validation/requestResetSchema.js";
import {useRequestPasswordReset} from "./hooks/useRequestPasswordReset.jsx";
import {SuccessCard} from "../../components/SuccessCard.jsx";

const RequestResetForm = () => {
    const [generalError, setGeneralError] = useState('');
    const [submittedEmail, setSubmittedEmail] = useState('');
    const [isSuccess, setIsSuccess] = useState(false);

    const {
        register,
        handleSubmit,
        setError,
        formState: {errors},
    } = useForm({
        resolver: yupResolver(requestResetSchema),
        defaultValues: {
            email: '',
        },
    });

    const requestResetMutation = useRequestPasswordReset({
        setError,
        setGeneralError,
        onSuccess: () => setIsSuccess(true),
    });

    const onSubmit = (data) => {
        setSubmittedEmail(data.email);
        requestResetMutation.mutate({email: data.email});
    };

    if (isSuccess) {
        return (
            <SuccessCard
                title="Check your email"
                message={
                    <>
                        If an account exists for{' '}
                        <span className="fw-semibold text-dark">{submittedEmail}</span>, a reset link has
                        been sent to your inbox.
                    </>
                }
                buttonText="Go to login"
                buttonLink="/login"
            />
        );
    }

    return (
        <div className="container py-5">
            <div className="card shadow-sm border-0 mx-auto" style={{maxWidth: '500px'}}>
                <div className="card-body p-4 text-center">
                    <h2 className="h4 fw-bold mb-3">Reset password</h2>
                    <form onSubmit={handleSubmit(onSubmit)} noValidate className="text-start">
                        <div className="mb-3">
                            <label htmlFor="email" className="form-label">
                                Email address
                            </label>
                            <input
                                {...register("email")}
                                id="email"
                                type="email"
                                className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                                disabled={requestResetMutation.isPending}
                            />
                            {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
                        </div>

                        {generalError && (
                            <div className="alert alert-danger py-2" role="alert">
                                {generalError}
                            </div>
                        )}

                        <div className="d-grid">
                            <button className="btn btn-primary" disabled={requestResetMutation.isPending}>
                                {requestResetMutation.isPending ? 'Sending...' : 'Send reset link'}
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

export default RequestResetForm;