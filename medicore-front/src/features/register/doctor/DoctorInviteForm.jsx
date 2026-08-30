import {useState} from 'react';
import {useForm} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import logo from '../../../assets/logo.png';
import {SuccessCard} from '../../../components/SuccessCard.jsx';
import {doctorInvitationSchema} from './validation/doctorInvitationSchema.js';
import {useInviteDoctor} from './hooks/useInviteDoctor.js';

const DoctorInviteForm = () => {
    const [generalError, setGeneralError] = useState('');
    const [isSuccess, setIsSuccess] = useState(false);
    const [invitedEmail, setInvitedEmail] = useState('');

    const {
        register,
        handleSubmit,
        setError,
        formState: {errors},
    } = useForm({
        resolver: yupResolver(doctorInvitationSchema),
        mode: 'onTouched',
    });

    const {mutate: inviteDoctor, isPending} = useInviteDoctor(
        setError,
        setGeneralError,
        () => setIsSuccess(true)
    );

    const onSubmit = (data) => {
        setInvitedEmail(data.email);
        inviteDoctor(data);
    };

    if (isSuccess) {
        return (
            <SuccessCard
                title="Invitation sent"
                message={
                    <>
                        An invitation email was sent to{' '}
                        <span className="fw-semibold text-dark">{invitedEmail}</span>.
                    </>
                }
                buttonText="Back to doctors"
                buttonLink="/doctors"
            />
        );
    }

    return (
        <div className="container py-4 py-md-5">
            <div className="row justify-content-center">
                <div className="col-lg-7 col-xl-6">
                    <div className="text-center mb-4">
                        <img
                            src={logo}
                            alt="MediCore Logo"
                            width="100"
                            height="70"
                            className="mb-2"
                        />
                        <h2 className="fw-bold mb-1">Invite Doctor</h2>
                    </div>

                    <div className="card shadow-lg border-0">
                        <div className="card-body p-4 p-md-5">
                            {generalError && (
                                <div className="alert alert-danger py-2 text-center" role="alert">
                                    {generalError}
                                </div>
                            )}

                            <form onSubmit={handleSubmit(onSubmit)}>
                                <div className="mb-3">
                                    <label htmlFor="firstName" className="form-label">First Name</label>
                                    <input
                                        id="firstName"
                                        {...register('firstName')}
                                        className={`form-control ${errors.firstName ? 'is-invalid' : ''}`}
                                    />
                                    {errors.firstName && <div className="invalid-feedback">{errors.firstName.message}</div>}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="lastName" className="form-label">Last Name</label>
                                    <input
                                        id="lastName"
                                        {...register('lastName')}
                                        className={`form-control ${errors.lastName ? 'is-invalid' : ''}`}
                                    />
                                    {errors.lastName && <div className="invalid-feedback">{errors.lastName.message}</div>}
                                </div>

                                <div className="mb-3">
                                    <label htmlFor="email" className="form-label">Email</label>
                                    <input
                                        id="email"
                                        type="email"
                                        {...register('email')}
                                        className={`form-control ${errors.email ? 'is-invalid' : ''}`}
                                    />
                                    {errors.email && <div className="invalid-feedback">{errors.email.message}</div>}
                                </div>

                                <div className="d-grid">
                                    <button type="submit" disabled={isPending} className="btn btn-primary">
                                        {isPending ? 'Sending invite...' : 'Send Invite'}
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DoctorInviteForm;
