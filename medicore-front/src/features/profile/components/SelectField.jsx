const SelectField = ({label, isEditing, register, error, options}) => (
    <div className="field mb-3">
        <label className="form-label fw-bold small text-dark">{label}</label>
        <select
            className={`form-select ${error ? 'is-invalid' : ''}`}
            disabled={!isEditing}
            {...register}
        >
            <option value="">Select {label}...</option>
            {options.map((opt) => (
                <option key={opt.value} value={opt.value}>
                    {opt.label}
                </option>
            ))}
        </select>
        {error && <div className="invalid-feedback">{error.message}</div>}
    </div>
);

export default SelectField;