import {useEffect} from 'react';
import {Link, useSearchParams} from 'react-router-dom';
import {useVerifyEmail} from './hooks/useVerifyEmail';

const VerifyEmailPage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token')?.replace(/\s/g, '+');

    const {mutate: verifyEmail, isPending, isSuccess, isError, error} = useVerifyEmail();

    useEffect(() => {
        if (token) {
            verifyEmail(token);
        }
    }, [token, verifyEmail]);

    const errorMessage = error?.response?.data?.message || error?.response?.data?.error || 'Verification failed. The link may be expired or invalid.';

    if (!token) {
        return (
            <div className="container py-5">
                <div className="card shadow-sm border-0 mx-auto" style={{maxWidth: '500px'}}>
                    <div className="card-body p-4 text-center">
                        <h2 className="h4 fw-bold mb-3">Email verification</h2>
                        <div className="alert alert-danger" role="alert">
                            Invalid verification link. Missing token.
                        </div>
                        <Link to="/" className="btn btn-primary">Back to home</Link>
                    </div>
                </div>
            </div>
        );
    }

    if (isPending) {
        return (
            <div className="container py-5">
                <div className="card shadow-sm border-0 mx-auto" style={{maxWidth: '500px'}}>
                    <div className="card-body p-4 text-center">
                        <div className="spinner-border text-primary mb-3" role="status">
                            <span className="visually-hidden">Loading...</span>
                        </div>
                        <h2 className="h4 fw-bold mb-2">Verifying your email</h2>
                        <p className="text-muted mb-0">Please wait while we confirm your account.</p>
                    </div>
                </div>
            </div>
        );
    }

    if (isSuccess) {
        return (
            <div className="container py-5">
                <div className="card shadow-sm border-0 mx-auto" style={{maxWidth: '500px'}}>
                    <div className="card-body p-4 text-center">
                        <div className="text-success fs-1 mb-3">✓</div>
                        <h2 className="h4 fw-bold mb-2">Email verified</h2>
                        <p className="text-muted mb-0">Your account is now active. Redirecting to login...</p>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="container py-5">
            <div className="card shadow-sm border-0 mx-auto" style={{maxWidth: '500px'}}>
                <div className="card-body p-4">
                    <h2 className="h4 fw-bold mb-3 text-center">Verify your email</h2>

                    {isError && (
                        <div className="alert alert-danger" role="alert">
                            {errorMessage}
                        </div>
                    )}

                    <p className="text-muted text-center mb-4">
                        Your verification link was detected. Please continue to confirm your email.
                    </p>

                    <div className="d-flex justify-content-between align-items-center gap-2">
                        <Link to="/" className="btn btn-outline-secondary">
                            Home
                        </Link>
                        <button type="button" className="btn btn-primary" onClick={() => verifyEmail(token)}>
                            Verify email
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default VerifyEmailPage;