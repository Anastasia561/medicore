import {useFormContext} from 'react-hook-form';

export const Step5MedicalInfo = () => {
    const {register, watch, setValue, formState: {errors}} = useFormContext();
    const selectedGender = watch('gender');

    const genderRegister = register('gender');

    const handleGenderChange = (e) => {
        genderRegister.onChange(e);

        const gender = e.target.value;
        if (gender === 'MALE') {
            setValue('pregnancyStatus', 'NOT_APPLICABLE', {shouldValidate: true});
        } else if (gender === 'FEMALE' || gender === 'OTHER') {
            setValue('pregnancyStatus', 'UNKNOWN', {shouldValidate: true});
        }
    };

    return (
        <>
            <div className="mb-3">
                <label htmlFor="gender" className="form-label">Gender</label>
                <select
                    id="gender"
                    {...genderRegister}
                    onChange={handleGenderChange}
                    className={`form-select ${errors.gender ? 'is-invalid' : ''}`}
                >
                    <option value="">Select Gender</option>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                </select>
                {errors.gender && <div className="invalid-feedback">{errors.gender.message}</div>}
            </div>

            <div className="mb-3">
                <label htmlFor="weight" className="form-label">Weight (kg)</label>
                <input
                    id="weight"
                    type="number"
                    step="0.1"
                    {...register('weight')}
                    className={`form-control ${errors.weight ? 'is-invalid' : ''}`}
                />
                {errors.weight && <div className="invalid-feedback">{errors.weight.message}</div>}
            </div>

            <div className="mb-3">
                <label htmlFor="height" className="form-label">Height (cm)</label>
                <input
                    id="height"
                    type="number"
                    step="0.1"
                    {...register('height')}
                    className={`form-control ${errors.height ? 'is-invalid' : ''}`}
                />
                {errors.height && <div className="invalid-feedback">{errors.height.message}</div>}
            </div>

            <div className="mb-3">
                <label htmlFor="pregnancyStatus" className="form-label">Pregnancy Status</label>
                <select
                    id="pregnancyStatus"
                    {...register('pregnancyStatus')}
                    className={`form-select ${errors.pregnancyStatus ? 'is-invalid' : ''}`}
                    disabled={selectedGender === 'MALE'}
                >
                    {selectedGender === 'MALE' ? (
                        <option value="NOT_APPLICABLE">Not Applicable</option>
                    ) : (
                        <>
                            <option value="UNKNOWN">Unknown</option>
                            <option value="NOT_PREGNANT">Not Pregnant</option>
                            <option value="PREGNANT">Pregnant</option>
                        </>
                    )}
                </select>
                {errors.pregnancyStatus && <div className="invalid-feedback">{errors.pregnancyStatus.message}</div>}
            </div>
        </>
    );
};