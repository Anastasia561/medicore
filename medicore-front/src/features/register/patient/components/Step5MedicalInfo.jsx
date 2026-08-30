import {useFormContext} from 'react-hook-form';

const genderOptions = [
    {value: 'MALE', label: 'Male'},
    {value: 'FEMALE', label: 'Female'},
    {value: 'OTHER', label: 'Other'},
];

const malePregnancyOptions = [
    {value: 'NOT_APPLICABLE', label: 'Not Applicable'},
];

const femalePregnancyOptions = [
    {value: 'UNKNOWN', label: 'Unknown'},
    {value: 'NOT_PREGNANT', label: 'Not Pregnant'},
    {value: 'PREGNANT', label: 'Pregnant'},
];

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

    const currentPregnancyOptions = selectedGender === 'MALE'
        ? malePregnancyOptions
        : femalePregnancyOptions;

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
                    {genderOptions.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
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
                    {currentPregnancyOptions.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
                {errors.pregnancyStatus && <div className="invalid-feedback">{errors.pregnancyStatus.message}</div>}
            </div>
        </>
    );
};
