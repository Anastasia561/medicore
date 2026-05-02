import DataCard from "../common/DataCard.jsx";
import useAuth from "../../../hooks/auth/useAuth.jsx";
import {useDoctors} from "../hooks/useDoctors.jsx";
import ListContainer from "../common/ListContainer.jsx";
import Pagination from "../../common/Pagination.jsx";
import {useState} from "react";

const DoctorList = () => {
    const {auth} = useAuth();

    const [currentPage, setCurrentPage] = useState(1);
    const pageSize = 3;

    const {data, isLoading, isError} = useDoctors(currentPage - 1, pageSize);

    if (isLoading) return <div className="text-center p-5">Loading doctors...</div>;
    if (isError) return <div className="alert alert-danger m-4">Failed to load doctors</div>;

    const doctors = data?.content || [];
    const totalPages = data?.totalPages || 0;

    return (
        <ListContainer
            title="Doctors"
            headerActions={
                <>
                    {auth?.role === 'ROLE_ADMIN' &&
                        <button className="bi bi-person-plus-fill me-2">Add Doctor</button>}
                </>
            }
        >
            <div className="row row-cols-1 g-4 mt-4">
                {doctors.map(doc => (
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
                ))}
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