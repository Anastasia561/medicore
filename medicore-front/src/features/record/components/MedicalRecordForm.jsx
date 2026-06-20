import {useParams, useNavigate} from "react-router-dom";
import {useForm, useFieldArray} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import {recordCreateSchema} from "../validation/RecordCreateSchema.js";
import {useCreateMedicalRecord} from "../hooks/useCreateMedicalRecord.jsx";
import {useState} from "react";

const MedicalRecordForm = () => {
    const {appId} = useParams();
    const navigate = useNavigate();
    const {mutateAsync: createRecord} = useCreateMedicalRecord();
    const [serverError, setServerError] = useState("");

    const {
        register,
        control,
        handleSubmit,
        formState: {errors, isSubmitting}
    } = useForm({
        resolver: yupResolver(recordCreateSchema),
        defaultValues: {
            diagnosis: "",
            summary: "",
            prescriptions: []
        }
    });

    const {fields, append, remove} = useFieldArray({
        control,
        name: "prescriptions"
    });

    const onSubmitForm = async (data) => {
        setServerError("");

        const payload = {
            appointmentId: appId,
            ...data
        };

        try {
            const newRecordId = await createRecord(payload);

            if (newRecordId) {
                navigate(`/records/${newRecordId}`, {replace: true});
            } else {
                navigate("/appointments", {replace: true});
            }
        } catch (err) {
            setServerError(err?.response?.data?.message || "Failed to create medical record.");
        }
    };

    return (
        <div className="container-fluid p-4 bg-light min-vh-100 d-flex justify-content-center">
            <div className="w-100" style={{maxWidth: '900px'}}>

                <h2 className="mb-4 text-dark fw-semibold ps-2" style={{color: '#1a2b49'}}>
                    Create Medical Record
                </h2>

                <form onSubmit={handleSubmit(onSubmitForm)} className="card shadow-sm border-0 rounded-3 p-4 bg-white">

                    {serverError && (
                        <div className="alert alert-danger mb-4" role="alert">{serverError}</div>
                    )}

                    <h3 className="h5 text-dark fw-bold mb-3">Medical Notes</h3>

                    <div className="mb-3">
                        <label className="form-label fw-bold text-dark small">Diagnosis</label>
                        <input
                            type="text"
                            className={`form-control ${errors.diagnosis ? 'is-invalid' : ''}`}
                            placeholder="Enter main diagnosis"
                            {...register("diagnosis")}
                        />
                        <div className="invalid-feedback">{errors.diagnosis?.message}</div>
                    </div>

                    <div className="mb-4">
                        <label className="form-label fw-bold text-dark small">Summary</label>
                        <textarea
                            className={`form-control ${errors.summary ? 'is-invalid' : ''}`}
                            rows="4"
                            placeholder="Provide a clinical summary of the consultation"
                            {...register("summary")}
                        />
                        <div className="invalid-feedback">{errors.summary?.message}</div>
                    </div>

                    <hr className="text-muted opacity-25 my-4"/>

                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h3 className="h5 text-dark fw-bold mb-0">Prescriptions</h3>
                        <button
                            type="button"
                            className="btn btn-outline-primary btn-sm fw-medium"
                            onClick={() => append({
                                medicine: "",
                                dosage: "",
                                frequency: "",
                                startDate: "",
                                endDate: ""
                            })}
                        >
                            + Add Medicine
                        </button>
                    </div>

                    {fields.length === 0 ? (
                        <p className="text-muted small italic mb-4">No prescriptions added yet.</p>
                    ) : (
                        <div className="table-responsive mb-4">
                            <table className="table table-bordered align-middle">
                                <thead className="table-light">
                                <tr>
                                    <th className="small fw-bold" style={{width: '25%'}}>Medicine</th>
                                    <th className="small fw-bold" style={{width: '20%'}}>Dosage</th>
                                    <th className="small fw-bold" style={{width: '20%'}}>Frequency</th>
                                    <th className="small fw-bold" style={{width: '15%'}}>Start Date</th>
                                    <th className="small fw-bold" style={{width: '15%'}}>End Date</th>
                                    <th className="small fw-bold text-center" style={{width: '5%'}}></th>
                                </tr>
                                </thead>
                                <tbody>
                                {fields.map((item, index) => {
                                    const rowErrors = errors.prescriptions?.[index];

                                    return (
                                        <tr key={item.id}>
                                            <td>
                                                <input
                                                    type="text"
                                                    className={`form-control form-control-sm ${errors.prescriptions?.[index]?.medicine ? 'is-invalid' : ''}`}
                                                    {...register(`prescriptions.${index}.medicine`)}
                                                />
                                                {rowErrors?.medicine && (
                                                    <div className="invalid-feedback d-block text-nowrap"
                                                         style={{fontSize: '0.75rem'}}>
                                                        {rowErrors.medicine.message}
                                                    </div>
                                                )}
                                            </td>
                                            <td>
                                                <input
                                                    type="text"
                                                    className={`form-control form-control-sm ${errors.prescriptions?.[index]?.dosage ? 'is-invalid' : ''}`}
                                                    placeholder="e.g. 500mg"
                                                    {...register(`prescriptions.${index}.dosage`)}
                                                />
                                                {rowErrors?.dosage && (
                                                    <div className="invalid-feedback d-block text-nowrap"
                                                         style={{fontSize: '0.75rem'}}>
                                                        {rowErrors.dosage.message}
                                                    </div>
                                                )}
                                            </td>
                                            <td>
                                                <input
                                                    type="text"
                                                    className={`form-control form-control-sm ${errors.prescriptions?.[index]?.frequency ? 'is-invalid' : ''}`}
                                                    placeholder="e.g. 2x daily"
                                                    {...register(`prescriptions.${index}.frequency`)}
                                                />
                                                {rowErrors?.frequency && (
                                                    <div className="invalid-feedback d-block text-nowrap"
                                                         style={{fontSize: '0.75rem'}}>
                                                        {rowErrors.frequency.message}
                                                    </div>
                                                )}
                                            </td>
                                            <td>
                                                <input
                                                    type="date"
                                                    className={`form-control form-control-sm ${errors.prescriptions?.[index]?.startDate ? 'is-invalid' : ''}`}
                                                    {...register(`prescriptions.${index}.startDate`)}
                                                />
                                                {rowErrors?.startDate && (
                                                    <div className="invalid-feedback d-block text-nowrap"
                                                         style={{fontSize: '0.75rem'}}>
                                                        {rowErrors.startDate.message}
                                                    </div>
                                                )}
                                            </td>
                                            <td>
                                                <input
                                                    type="date"
                                                    className="form-control form-control-sm"
                                                    {...register(`prescriptions.${index}.endDate`)}
                                                />
                                                {rowErrors?.endDate && (
                                                    <div className="invalid-feedback d-block text-nowrap"
                                                         style={{fontSize: '0.75rem'}}>
                                                        {rowErrors.endDate.message}
                                                    </div>
                                                )}
                                            </td>
                                            <td className="text-center">
                                                <button
                                                    type="button"
                                                    className="btn btn-outline-danger btn-sm border-0"
                                                    onClick={() => remove(index)}
                                                >
                                                    ✕
                                                </button>
                                            </td>
                                        </tr>
                                    )
                                })}
                                </tbody>
                            </table>
                        </div>
                    )}

                    <div className="d-flex justify-content-end gap-2 mt-2">
                        <button
                            type="button"
                            className="btn btn-outline-secondary px-4"
                            onClick={() => navigate(-1)}
                            disabled={isSubmitting}
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="btn btn-success px-4 fw-medium"
                            disabled={isSubmitting}
                        >
                            {isSubmitting ? "Saving..." : "Save Record"}
                        </button>
                    </div>

                </form>
            </div>
        </div>
    );
};

export default MedicalRecordForm;