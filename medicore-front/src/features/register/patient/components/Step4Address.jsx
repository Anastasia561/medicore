import {useFormContext} from "react-hook-form";

export const Step4Address = () => {
    const {register, formState: {errors}} = useFormContext();

    return (
        <>
            <div className="mb-3">
                <label htmlFor="country" className="form-label">Country</label>
                <input
                    id="country"
                    {...register('address.country')}
                    className={`form-control ${errors.address?.country ? 'is-invalid' : ''}`}
                />
                {errors.address?.country && <div className="invalid-feedback">{errors.address.country.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="city" className="form-label">City</label>
                <input
                    id="city"
                    {...register('address.city')}
                    className={`form-control ${errors.address?.city ? 'is-invalid' : ''}`}
                />
                {errors.address?.city && <div className="invalid-feedback">{errors.address.city.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="street" className="form-label">Street</label>
                <input
                    id="street"
                    {...register('address.street')}
                    className={`form-control ${errors.address?.street ? 'is-invalid' : ''}`}
                />
                {errors.address?.street && <div className="invalid-feedback">{errors.address.street.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="houseNumber" className="form-label">House Number</label>
                <input
                    id="houseNumber"
                    {...register('address.number')}
                    className={`form-control ${errors.address?.number ? 'is-invalid' : ''}`}
                />
                {errors.address?.number && <div className="invalid-feedback">{errors.address.number.message}</div>}
            </div>
        </>
    )
}
