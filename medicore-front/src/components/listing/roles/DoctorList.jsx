import DataCard from "../common/DataCard.jsx";
import useAuth from "../../../hooks/auth/useAuth.jsx";
import {useDoctors} from "../hooks/useDoctors.jsx";
import ListContainer from "../common/ListContainer.jsx";
import Pagination from "../../common/Pagination.jsx";
import {useState} from "react";
import {Link} from "react-router-dom";
import SearchInput from "../../common/SearchInput.jsx";

const DoctorList = () => {
    const {auth} = useAuth();

    const [specialization, setSpecialization] = useState("");
    const [currentPage, setCurrentPage] = useState(1);
    const [searchTerm, setSearchTerm] = useState("");
    const pageSize = 3;

    const handleSearch = (value) => {
        setSearchTerm(value);
        setCurrentPage(1);
    }

    const {data, isLoading, isError} = useDoctors(currentPage - 1, pageSize, searchTerm, specialization);

    if (isError) return <div className="alert alert-danger m-4">Failed to load doctors</div>;

    const doctors = data?.content || [];
    const totalPages = data?.totalPages || 0;

    return (
        <ListContainer
            title="Doctors"
            headerActions={
                <div>
                    {auth?.role === 'ROLE_ADMIN' && (
                        <Link to="/admin/doctors/register" className="btn btn-primary">
                            <i className="fas fa-user-plus me-1"></i>
                            <span>Add Doctor</span>
                        </Link>
                    )}
                </div>
            }
        >
            <div className="d-flex flex-wrap gap-3 p-3 mb-4 rounded shadow-sm border bg-light">
                <div style={{flex: '1 1 300px'}}>
                    <SearchInput onSearch={handleSearch} placeholder="Search by name..."/>
                </div>
                <div style={{minWidth: '200px'}}>
                    <select
                        className="form-select"
                        onChange={(e) => setSpecialization(e.target.value)}
                    >
                        <option value="">All Specializations</option>
                        <option value="CARDIOLOGIST">Cardiologist</option>
                        <option value="DERMATOLOGIST">Dermatologist</option>
                        <option value="NEUROLOGIST">Neurologist</option>
                        <option value="PEDIATRICIAN">Pediatrician</option>
                        <option value="ONCOLOGIST">Oncologist</option>
                    </select>
                </div>
            </div>


            <div className="row row-cols-1 g-4 mt-2">
                {isLoading ? (
                    <div className="text-center p-5">Searching...</div>
                ) : doctors.length > 0 ? (
                    doctors.map(doc => (
                        <DataCard
                            key={doc.public_id}
                            name={doc.firstName + " " + doc.lastName}
                            details={[
                                {label: 'Email', value: doc.email},
                                {label: 'Experience', value: `${doc.experience} years`},
                                {label: 'Specialization', value: doc.specialization},
                                {label: 'Employment date', value: doc.employmentDate}
                            ]}
                            renderActions={() => (
                                <>
                                    {auth?.role === 'ROLE_PATIENT' &&
                                        <button className="btn btn-outline-success btn-sm text-center">Book</button>}
                                    {auth?.role === 'ROLE_ADMIN' &&
                                        <button
                                            className="btn btn-outline-success btn-sm text-center">Appointments</button>}
                                    <button className="btn btn-outline-primary btn-sm text-center">Schedule</button>
                                </>
                            )}
                        />
                    ))
                ) : (
                    <div className="text-center p-5 text-muted">No doctors matched your search.</div>
                )}
            </div>

            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={(page) => setCurrentPage(page)}
            />
        </ListContainer>
    );
};

export default DoctorList;