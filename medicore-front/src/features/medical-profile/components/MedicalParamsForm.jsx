const malePregnancyOptions = [
    {value: "NOT_APPLICABLE", label: "Not Applicable"},
];

const femalePregnancyOptions = [
    {value: "UNKNOWN", label: "Unknown"},
    {value: "NOT_PREGNANT", label: "Not Pregnant"},
    {value: "PREGNANT", label: "Pregnant"},
];

const otherPregnancyOptions = [
    {value: "UNKNOWN", label: "Unknown"},
    {value: "NOT_APPLICABLE", label: "Not Applicable"},
];

const MedicalParamsForm = ({
    register,
    errors,
    gender,
    canEdit,
    isEditing,
    isSaving,
    generalError,
    onEdit,
    onCancel,
}) => {
    const pregnancyOptions = gender === "MALE"
        ? malePregnancyOptions
        : gender === "OTHER"
            ? otherPregnancyOptions
            : femalePregnancyOptions;

    const fieldsDisabled = !canEdit || !isEditing;

    return (
        <div className="card border-0 shadow-sm h-100">
            <div className="card-body p-4">
                <h3 className="h5 mb-1">Medical parameters</h3>
                <p className="text-muted small mb-4">
                    {canEdit
                        ? "Update your weight, height, and pregnancy status."
                        : "Patient medical parameters (read-only)."}
                </p>

                {generalError && (
                    <div className="alert alert-danger py-2" role="alert">
                        {generalError}
                    </div>
                )}

                <div className="mb-3">
                    <label className="form-label small fw-semibold">Gender</label>
                    <input
                        type="text"
                        className="form-control"
                        value={gender || "—"}
                        disabled
                        readOnly
                    />
                </div>

                <div className="mb-3">
                    <label htmlFor="weight" className="form-label small fw-semibold">
                        Weight (kg)
                    </label>
                    <input
                        id="weight"
                        type="number"
                        step="0.1"
                        {...register("weight")}
                        className={`form-control ${errors.weight ? "is-invalid" : ""}`}
                        disabled={fieldsDisabled}
                    />
                    {errors.weight && (
                        <div className="invalid-feedback">{errors.weight.message}</div>
                    )}
                </div>

                <div className="mb-3">
                    <label htmlFor="height" className="form-label small fw-semibold">
                        Height (cm)
                    </label>
                    <input
                        id="height"
                        type="number"
                        step="0.1"
                        {...register("height")}
                        className={`form-control ${errors.height ? "is-invalid" : ""}`}
                        disabled={fieldsDisabled}
                    />
                    {errors.height && (
                        <div className="invalid-feedback">{errors.height.message}</div>
                    )}
                </div>

                <div className="mb-4">
                    <label htmlFor="pregnancyStatus" className="form-label small fw-semibold">
                        Pregnancy status
                    </label>
                    <select
                        id="pregnancyStatus"
                        {...register("pregnancyStatus")}
                        className={`form-select ${errors.pregnancyStatus ? "is-invalid" : ""}`}
                        disabled={fieldsDisabled || gender === "MALE"}
                    >
                        {pregnancyOptions.map((option) => (
                            <option key={option.value} value={option.value}>
                                {option.label}
                            </option>
                        ))}
                    </select>
                    {errors.pregnancyStatus && (
                        <div className="invalid-feedback">{errors.pregnancyStatus.message}</div>
                    )}
                </div>

                {canEdit && (
                    <div className="d-flex gap-2">
                        {isEditing ? (
                            <>
                                <button
                                    type="button"
                                    className="btn btn-outline-secondary"
                                    onClick={onCancel}
                                    disabled={isSaving}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-primary"
                                    disabled={isSaving}
                                >
                                    {isSaving ? "Saving..." : "Save changes"}
                                </button>
                            </>
                        ) : (
                            <button type="button" className="btn btn-outline-primary" onClick={onEdit}>
                                Edit
                            </button>
                        )}
                    </div>
                )}
            </div>
        </div>
    );
};

export default MedicalParamsForm;
