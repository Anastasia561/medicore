const Field = ({label, isEditing, register, error}) => (
    <div className="field mb-3">
        <label className="form-label fw-bold small text-dark">{label}</label>
        <input
            type="text"
            className={`form-control ${error ? 'is-invalid' : ''}`}
            disabled={!isEditing}
            {...register}
        />
        {error && <div className="invalid-feedback">{error.message}</div>}
    </div>
);

export default Field;