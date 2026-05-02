import DataCard from "../common/DataCard.jsx";
import ListContainer from "../common/ListContainer.jsx";
import {useState} from "react";
import {usePatients} from "../hooks/usePatients.jsx";
import Pagination from "../../common/Pagination.jsx";

const PatientList = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const pageSize = 3;

    const {data, isLoading, isError} = usePatients(currentPage - 1, pageSize);

    if (isLoading) return <div className="text-center p-5">Loading patients...</div>;
    if (isError) return <div className="alert alert-danger m-4">Failed to load patients</div>;

    const patients = data?.content || [];
    const totalPages = data?.totalPages || 0;

    return (
        <ListContainer title="Patients">
            <div className="row row-cols-1 g-4 mt-4">
                {patients.map(patient => (
                    <DataCard
                        key={patient.public_id}
                        name={patient.firstName + " " + patient.lastName}
                        details={[
                            {label: 'Email', value: patient.email},
                            {label: 'Phone', value: patient.phoneNumber},
                            {label: 'Birth Date', value: patient.birthDate},
                            {
                                label: 'Address',
                                value: `${patient.address?.street}, ${patient.address?.number}, ${patient.address?.city}, ${patient.address?.country}`
                            },
                        ]}
                        renderActions={() => (
                            <button className="btn btn-outline-success btn-sm text-center">Appointments</button>
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

export default PatientList;