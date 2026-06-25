const IconField = ({ label, isEditing, register, error, iconClass, className, value }) => (
    <div className="field mb-3">
        <label className="form-label fw-bold small text-dark">{label}</label>
        <div className="input-group">
            <span className="input-group-text bg-light">
                <i className={iconClass}></i>
            </span>
            <input
                type="text"
                className={`form-control ${className || ''} ${error ? 'is-invalid' : ''}`}
                disabled={!isEditing}
                {...(isEditing && register ? register : { value: value || '' })}
            />
            {error && <div className="invalid-feedback">{error.message}</div>}
        </div>
    </div>
);

export default IconField;