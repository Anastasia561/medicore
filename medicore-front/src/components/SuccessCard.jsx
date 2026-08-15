import {Link} from 'react-router-dom';

export const SuccessCard = ({
                                title = 'Success',
                                message,
                                buttonText = 'Go to login',
                                buttonLink = '/login',
                                children,
                            }) => {
    return (
        <div className="container py-5">
            <div className="card shadow-sm border-0 mx-auto" style={{maxWidth: '500px'}}>
                <div className="card-body p-4 text-center">
                    <div className="text-success fs-1 mb-3">✓</div>
                    <h2 className="h4 fw-bold mb-2">{title}</h2>

                    {message && (
                        typeof message === 'string' ? (
                            <p className="text-muted mb-4">{message}</p>
                        ) : (
                            <div className="text-muted mb-4">{message}</div>
                        )
                    )}

                    {children}

                    <div className="d-grid mt-3">
                        <Link to={buttonLink} className="btn btn-primary">
                            {buttonText}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
};
