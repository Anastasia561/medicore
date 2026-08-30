import {useState} from 'react';
import {useFormContext} from 'react-hook-form';

export const Step2Password = () => {
    const {register, formState: {errors}} = useFormContext();
    const [showPassword, setShowPassword] = useState(false);
    const [showRepeatPassword, setShowRepeatPassword] = useState(false);

    return (
        <>
            <div className="mb-3">
                <label htmlFor="password" className="form-label">Password</label>
                <div className={`input-group ${errors.password ? 'has-validation' : ''}`}>
                    <input
                        id="password"
                        type={showPassword ? 'text' : 'password'}
                        {...register('password')}
                        className={`form-control ${errors.password ? 'is-invalid' : ''}`}
                    />
                    <button
                        type="button"
                        className="btn btn-outline-secondary"
                        onClick={() => setShowPassword((prev) => !prev)}
                        aria-label={showPassword ? "Hide password" : "Show password"}
                    >
                        <i className={`bi ${showPassword ? 'bi-eye' : 'bi-eye-slash'}`}></i>
                    </button>
                    {errors.password && <div className="invalid-feedback">{errors.password.message}</div>}
                </div>
            </div>

            <div className="mb-3">
                <label htmlFor="repeatPassword" className="form-label">Repeat Password</label>
                <div className={`input-group ${errors.repeatPassword ? 'has-validation' : ''}`}>
                    <input
                        id="repeatPassword"
                        type={showRepeatPassword ? 'text' : 'password'}
                        {...register('repeatPassword')}
                        className={`form-control ${errors.repeatPassword ? 'is-invalid' : ''}`}
                    />
                    <button
                        type="button"
                        className="btn btn-outline-secondary"
                        onClick={() => setShowRepeatPassword((prev) => !prev)}
                        aria-label={showRepeatPassword ? "Hide password" : "Show password"}
                    >
                        <i className={`bi ${showRepeatPassword ? 'bi-eye' : 'bi-eye-slash'}`}></i>
                    </button>
                    {errors.repeatPassword && <div className="invalid-feedback">{errors.repeatPassword.message}</div>}
                </div>
            </div>
        </>
    );
};