import {useState} from 'react';
import {useMedicalRecordPreview} from './hooks/useMedicalRecordPreview.jsx';
import {formatDateStandard} from '../../utils/dateUtils.js';
import useAuth from '../../hooks/useAuth.jsx';
import {useNavigate} from "react-router-dom";
import Pagination from "../../components/Pagination.jsx";
import SearchInput from "../../components/SearchInput.jsx";

const MedicalRecordListing = () => {
    const {auth} = useAuth();
    const isDoctor = auth?.role === "ROLE_DOCTOR";
    const navigate = useNavigate();

    const [currentPage, setCurrentPage] = useState(1);
    const [pageSize] = useState(5);
    const [startDate, setStartDate] = useState('');
    const [endDate, setEndDate] = useState('');
    const [searchTerm, setSearchTerm] = useState("");

    const handleSearch = (value) => {
        setSearchTerm(value);
        setCurrentPage(1);
    }

    const {data, isLoading, isError} = useMedicalRecordPreview({
        page: currentPage - 1,
        size: pageSize,
        startDate,
        endDate,
        searchTerm
    });

    if (isError) return <div className="alert alert-danger m-4">Failed to load medical records</div>;

    const records = data?.content || [];
    const totalPages = data?.totalPages || 0;

    return (
        <div className="container-fluid p-4 bg-light min-vh-100 d-flex justify-content-center">
            <div className="w-100" style={{maxWidth: '1000px'}}>

                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2 className="text-dark fw-semibold mb-0" style={{color: '#1a2b49'}}>
                        Medical History Preview
                    </h2>
                    <span className="badge bg-secondary px-3 py-2 rounded-pill">
                        Total Records: {data?.totalElements || 0}
                    </span>
                </div>

                <div className="card shadow-sm border-0 rounded-3 p-3 bg-white mb-4">

                    <div className="row g-3 align-items-end">

                        <div className="col-12 col-md-2">
                            <label className="form-label small fw-bold text-secondary mb-1">From Date</label>
                            <input
                                type="date"
                                className="form-control form-control-sm"
                                value={startDate}
                                onChange={(e) => {
                                    setStartDate(e.target.value);
                                    setCurrentPage(1);
                                }}
                            />
                        </div>

                        <div className="col-12 col-md-2">
                            <label className="form-label small fw-bold text-secondary mb-1">To Date</label>
                            <input
                                type="date"
                                className="form-control form-control-sm"
                                value={endDate}
                                onChange={(e) => {
                                    setEndDate(e.target.value);
                                    setCurrentPage(1);
                                }}
                            />
                        </div>

                        <div className="col-12 col-md-6">
                            <label className="form-label small fw-bold text-secondary mb-1">Search</label>
                            <SearchInput onSearch={handleSearch} placeholder="Search by email..."/>
                        </div>

                        <div className="col-12 col-md-2">
                            <button
                                className="btn btn-outline-secondary btn-sm w-100"
                                onClick={() => {
                                    setStartDate('');
                                    setEndDate('');
                                    setCurrentPage(1);
                                    setSearchTerm("");
                                }}
                            >
                                Clear Filters
                            </button>
                        </div>

                    </div>
                </div>

                <div className="card shadow-sm border-0 rounded-3 bg-white overflow-hidden mb-3"
                     style={{minHeight: '200px'}}>
                    {isLoading ? (
                        <div className="p-5 text-center">
                            <div className="spinner-border text-primary" role="status"/>
                            <div className="mt-3 text-muted">Updating results...</div>
                        </div>
                    ) : records.length === 0 ? (
                        <div className="p-5 text-center text-muted">
                            <p className="mb-0">No medical records found matching your selection criteria.</p>
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <table className="table table-hover align-middle mb-0">
                                <thead className="table-light border-bottom">
                                <tr>
                                    <th className="px-4 py-3 text-secondary small fw-bold" style={{width: '20%'}}>Date
                                    </th>
                                    {isDoctor ? (
                                        <th className="px-4 py-3 text-secondary small fw-bold"
                                            style={{width: '55%'}}>Patient Info</th>
                                    ) : (
                                        <th className="px-4 py-3 text-secondary small fw-bold"
                                            style={{width: '55%'}}>Doctor Info</th>
                                    )}
                                    <th className="px-4 py-3 text-secondary small fw-bold text-end"
                                        style={{width: '25%'}}>Action
                                    </th>
                                </tr>
                                </thead>
                                <tbody>
                                {records.map((record, index) => {
                                    const patient = record.patient;
                                    const doctor = record.doctor;

                                    return (
                                        <tr key={index}>
                                            <td className="px-4 py-3 fw-medium text-dark">
                                                {formatDateStandard(record.date)}
                                            </td>

                                            {isDoctor ? (
                                                <td className="px-4 py-3">
                                                    {patient ? (
                                                        <div>
                                                            <div className="fw-semibold text-dark text-capitalize">
                                                                {patient.firstName} {patient.lastName}
                                                            </div>
                                                            <div className="text-muted extra-small"
                                                                 style={{fontSize: '0.8rem'}}>
                                                                {patient.email}
                                                            </div>
                                                        </div>
                                                    ) : (
                                                        <span
                                                            className="text-muted italic small">Anonymous/Unassigned</span>
                                                    )}
                                                </td>
                                            ) : (
                                                <td className="px-4 py-3">
                                                    {doctor ? (
                                                        <div>
                                                            <div className="fw-semibold text-dark text-capitalize">
                                                                {doctor.firstName} {doctor.lastName}
                                                            </div>
                                                            <div className="text-muted extra-small"
                                                                 style={{fontSize: '0.8rem'}}>
                                                                {doctor.specialization}
                                                            </div>
                                                        </div>
                                                    ) : (
                                                        <span
                                                            className="text-muted italic small">Anonymous/Unassigned</span>
                                                    )}
                                                </td>
                                            )}

                                            <td className="px-4 py-3 text-end">
                                                <button
                                                    onClick={() => navigate(`/records/${record.id}`)}
                                                    className="btn btn-sm btn-outline-primary px-3 fw-medium"
                                                >
                                                    View Full Details
                                                </button>
                                            </td>
                                        </tr>
                                    );
                                })}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>

                {totalPages > 1 && (
                    <div className="d-flex justify-content-center mt-4">
                        <Pagination
                            currentPage={currentPage}
                            totalPages={totalPages}
                            onPageChange={(page) => setCurrentPage(page)}
                        />
                    </div>
                )}

            </div>
        </div>
    );
};

export default MedicalRecordListing;