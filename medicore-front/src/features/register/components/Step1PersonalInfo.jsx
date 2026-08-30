import {useFormContext} from 'react-hook-form';

export const Step1PersonalInfo = () => {
    const {register, formState: {errors}} = useFormContext();

    return (
        <>
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
                <label htmlFor="birthDate" className="form-label">Birth Date</label>
                <input
                    id="birthDate"
                    type="date"
                    {...register('birthDate')}
                    className={`form-control ${errors.birthDate ? 'is-invalid' : ''}`}
                />
                {errors.birthDate && <div className="invalid-feedback">{errors.birthDate.message}</div>}
            </div>
        </>
    );
};
