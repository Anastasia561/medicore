import {useFormContext} from "react-hook-form";

export const Step3ContactDetails = () => {
    const {register, formState: {errors}} = useFormContext();

    return (
        <>
            <div className="mb-3">
                <label htmlFor="phoneNumber" className="form-label">Phone Number</label>
                <input
                    id="phoneNumber"
                    {...register('phoneNumber')}
                    className={`form-control ${errors.phoneNumber ? 'is-invalid' : ''}`}
                />
                {errors.phoneNumber && <div className="invalid-feedback">{errors.phoneNumber.message}</div>}
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
        </>
    )
}
