import {useState} from "react";
import {useLocation, useParams} from "react-router-dom";
import {useLabResults} from "./hooks/useLabResults.jsx";
import {useUploadLabResult} from "./hooks/useUploadLabResult.jsx";
import {useLabResultFileActions} from "./hooks/useLabResultFileActions.jsx";
import {formatDateStandard} from "../../utils/dateUtils.js";
import ListContainer from "../listing/ListContainer.jsx";

const LabResults = () => {
    const {patientId} = useParams();
    const {state} = useLocation();
    const canUpload = !patientId;

    const [date, setDate] = useState("");
    const [file, setFile] = useState(null);
    const [actionId, setActionId] = useState(null);

    const {data: results = [], isLoading, isError} = useLabResults(patientId);
    const {mutate: upload, isPending: isUploading} = useUploadLabResult();
    const {openView, download} = useLabResultFileActions();

    const today = new Date().toISOString().split("T")[0];
    const title = patientId && state?.userName
        ? `Lab results — ${state.userName}`
        : "Lab results";

    const handleUpload = (event) => {
        event.preventDefault();
        if (!file || !date) return;

        upload(
            {file, date},
            {
                onSuccess: () => {
                    setFile(null);
                    setDate("");
                    event.target.reset();
                },
            }
        );
    };

    const runAction = async (id, action) => {
        setActionId(id);
        try {
            await action(id);
        } finally {
            setActionId(null);
        }
    };

    if (isError) {
        return <div className="alert alert-danger m-4">Failed to load lab results</div>;
    }

    return (
        <ListContainer title={title}>
            {canUpload && (
                <form className="border rounded-3 bg-light p-3 mb-4" onSubmit={handleUpload}>
                    <h3 className="h6 mb-3">Upload lab result</h3>
                    <div className="row g-3 align-items-end">
                        <div className="col-md-4">
                            <label htmlFor="lab-result-date" className="form-label small fw-semibold">
                                Test date
                            </label>
                            <input
                                id="lab-result-date"
                                type="date"
                                className="form-control"
                                max={today}
                                value={date}
                                onChange={(event) => setDate(event.target.value)}
                                required
                            />
                        </div>
                        <div className="col-md-5">
                            <label htmlFor="lab-result-file" className="form-label small fw-semibold">
                                PDF file
                            </label>
                            <input
                                id="lab-result-file"
                                type="file"
                                className="form-control"
                                accept="application/pdf,.pdf"
                                onChange={(event) => setFile(event.target.files?.[0] || null)}
                                required
                            />
                        </div>
                        <div className="col-md-3">
                            <button
                                type="submit"
                                className="btn btn-primary w-100"
                                disabled={isUploading || !file || !date}
                            >
                                {isUploading ? "Uploading..." : "Upload"}
                            </button>
                        </div>
                    </div>
                </form>
            )}

            <div className="card border-0 shadow-sm overflow-hidden" style={{minHeight: "200px"}}>
                {isLoading ? (
                    <div className="p-5 text-center">
                        <div className="spinner-border text-primary" role="status"/>
                        <div className="mt-3 text-muted">Loading lab results...</div>
                    </div>
                ) : results.length === 0 ? (
                    <div className="p-5 text-center text-muted">
                        No lab results uploaded yet.
                    </div>
                ) : (
                    <div className="table-responsive">
                        <table className="table table-hover align-middle mb-0">
                            <thead className="table-light border-bottom">
                            <tr>
                                <th className="px-4 py-3 text-secondary small fw-bold">Test date</th>
                                <th className="px-4 py-3 text-secondary small fw-bold text-end">Actions</th>
                            </tr>
                            </thead>
                            <tbody>
                            {results.map((result) => (
                                <tr
                                    key={result.id}
                                    role="button"
                                    onClick={() => runAction(result.id, openView)}
                                    style={{cursor: "pointer"}}
                                >
                                    <td className="px-4 py-3">
                                        <i className="fas fa-file-pdf text-danger me-2" aria-hidden="true"/>
                                        {formatDateStandard(result.date)}
                                    </td>
                                    <td className="px-4 py-3 text-end">
                                        <button
                                            type="button"
                                            className="btn btn-outline-primary btn-sm"
                                            disabled={actionId === result.id}
                                            onClick={(event) => {
                                                event.stopPropagation();
                                                runAction(result.id, download);
                                            }}
                                        >
                                            <i className="fas fa-download me-1" aria-hidden="true"/>
                                            Download
                                        </button>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </ListContainer>
    );
};

export default LabResults;
