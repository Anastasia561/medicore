import {useFormContext} from 'react-hook-form';

export const Step2Password = () => {
    const {register, formState: {errors}} = useFormContext();

    return (
        <>
            <div className="mb-3">
                <label htmlFor="password" className="form-label">Password</label>
                <input
                    id="password"
                    type="password"
                    {...register('password')}
                    className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                />
                {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
            </div>
            <div className="mb-3">
                <label htmlFor="repeatPassword" className="form-label">Repeat Password</label>
                <input
                    id="repeatPassword"
                    type="password"
                    {...register('repeatPassword')}
                    className={`form-control ${errors.repeatPassword ? 'is-invalid' : ''}`}
                />
                {errors.repeatPassword && <div className="invalid-feedback">{errors.repeatPassword.message}</div>}
            </div>
        </>

    );
};
