import {useFormContext} from 'react-hook-form';

const specializationOptions = [
    {value: 'CARDIOLOGIST', label: 'Cardiologist'},
    {value: 'DERMATOLOGIST', label: 'Dermatologist'},
    {value: 'NEUROLOGIST', label: 'Neurologist'},
    {value: 'PEDIATRICIAN', label: 'Pediatrician'},
    {value: 'ONCOLOGIST', label: 'Oncologist'},
];

const genderOptions = [
    {value: 'MALE', label: 'Male'},
    {value: 'FEMALE', label: 'Female'},
    {value: 'OTHER', label: 'Other'},
];

export const Step5ProfessionalInfo = () => {
    const {register, formState: {errors}} = useFormContext();

    return (
        <>
            <div className="mb-3">
                <label htmlFor="specialization" className="form-label">Specialization</label>
                <select
                    id="specialization"
                    {...register('specialization')}
                    className={`form-select ${errors.specialization ? 'is-invalid' : ''}`}
                >
                    <option value="">Select Specialization</option>
                    {specializationOptions.map((option) => (
                        <option key={option.value} value={option.value}>
                            {option.label}
                        </option>
                    ))}
                </select>
                {errors.specialization && <div className="invalid-feedback">{errors.specialization.message}</div>}
            </div>

            <div className="mb-3">
                <label htmlFor="gender" className="form-label">Gender</label>
                <select
                    id="gender"
                    {...register('gender')}
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
                <label htmlFor="experience" className="form-label">Experience (years)</label>
                <input
                    id="experience"
                    type="number"
                    min="1"
                    step="1"
                    {...register('experience')}
                    className={`form-control ${errors.experience ? 'is-invalid' : ''}`}
                />
                {errors.experience && <div className="invalid-feedback">{errors.experience.message}</div>}
            </div>
        </>
    );
};
