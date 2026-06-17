import {useParams} from "react-router-dom";
import {useMedicalRecord} from "../hooks/useMedicalRecord.jsx";
import {formatDateStandard} from "../../../utils/dateUtils.js";

const MedicalRecord = () => {
    const {appId} = useParams();
    const {data, isLoading, isError} = useMedicalRecord(appId);

    if (isLoading) {
        return (
            <div className="container p-4 text-center mt-5">
                <div className="spinner-border text-primary" role="status"/>
                <div className="mt-3 text-muted">Loading medical record details...</div>
            </div>
        );
    }

    if (isError) return <p>Failed to load medical record</p>;

    const {doctor, patient, date, diagnosis, summary, prescriptions = []} = data;

    return (
        <div className="container-fluid p-4 bg-light min-vh-100 d-flex justify-content-center">
            <div className="w-100" style={{maxWidth: '900px'}}>

                <h2 className="mb-4 text-dark fw-semibold ps-2" style={{color: '#1a2b49'}}>
                    Medical Record Details
                </h2>

                <div className="card shadow-sm border-0 rounded-3 p-4 bg-white">

                    <div className="row g-4 mb-4">
                        <div className="col-12 col-md-6">
                            <h3 className="h5 text-dark fw-bold mb-3">Doctor Info</h3>
                            <div className="ps-2">
                                <p className="mb-2 text-dark">
                                    <span
                                        className="fw-bold">Name:</span> {doctor ? `${doctor.firstName} ${doctor.lastName}` : 'N/A'}
                                </p>
                                <p className="mb-0 text-dark">
                                    <span className="fw-bold">Specialization:</span> {doctor?.specialization || 'N/A'}
                                </p>
                            </div>
                        </div>

                        <div className="col-12 col-md-6">
                            <h3 className="h5 text-dark fw-bold mb-3">Patient Info</h3>
                            <div className="ps-2">
                                <p className="mb-2 text-dark">
                                    <span
                                        className="fw-bold">Name:</span> {patient ? `${patient.firstName} ${patient.lastName}` : 'N/A'}
                                </p>
                                <p className="mb-0 text-dark">
                                    <span className="fw-bold">Email:</span> {patient?.email || 'N/A'}
                                </p>
                            </div>
                        </div>
                    </div>

                    <hr className="text-muted opacity-25 my-4"/>

                    <div className="mb-4">
                        <h3 className="h5 text-dark fw-bold mb-3">Appointment Info</h3>
                        <div className="ps-2">
                            <p className="mb-0 text-dark">
                                <span className="fw-bold">Date:</span> {formatDateStandard(date)}
                            </p>
                        </div>
                    </div>

                    <hr className="text-muted opacity-25 my-4"/>

                    <div className="mb-4">
                        <h3 className="h5 text-dark fw-bold mb-3">Medical Notes</h3>
                        <div className="ps-2 mb-3">
                            <p className="fw-bold text-dark mb-2">Diagnosis:</p>
                            <p className="text-secondary ps-3 mb-0">{diagnosis || "No diagnosis provided"}</p>
                        </div>
                        <div className="ps-2">
                            <p className="fw-bold text-dark mb-2">Summary:</p>
                            <p className="text-secondary ps-3 mb-0">{summary || "No summary provided"}</p>
                        </div>
                    </div>

                    <hr className="text-muted opacity-25 my-4"/>

                    <div className="mb-2">
                        <h3 className="h5 text-dark fw-bold mb-3">Prescriptions</h3>
                        <div className="ps-2">
                            {prescriptions.length === 0 ? (
                                <p className="text-muted small italic mb-0">No prescriptions recorded</p>
                            ) : (
                                <div className="table-responsive">
                                    <table className="table table-bordered table-striped align-middle mt-2">
                                        <thead className="table-light">
                                        <tr>
                                            <th className="small fw-bold">Medicine</th>
                                            <th className="small fw-bold">Dosage</th>
                                            <th className="small fw-bold">Frequency</th>
                                            <th className="small fw-bold">Duration</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        {prescriptions.map((p, idx) => (
                                            <tr key={idx}>
                                                <td className="small fw-medium text-dark">{p.medicine}</td>
                                                <td className="small text-secondary">{p.dosage}</td>
                                                <td className="small text-secondary">{p.frequency}</td>
                                                <td className="small text-secondary">
                                                    {formatDateStandard(p.startDate)} - {formatDateStandard(p.endDate)}
                                                </td>
                                            </tr>
                                        ))}
                                        </tbody>
                                    </table>
                                </div>
                            )}
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
};

export default MedicalRecord;