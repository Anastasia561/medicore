import DataCard from "../common/DataCard.jsx";
import ListContainer from "../common/ListContainer.jsx";
import {useState} from "react";
import {usePatients} from "../hooks/usePatients.jsx";
import Pagination from "../../common/Pagination.jsx";
import SearchInput from "../../common/SearchInput.jsx";

const PatientList = () => {
    const [currentPage, setCurrentPage] = useState(1);
    const [searchTerm, setSearchTerm] = useState("");
    const pageSize = 3;

    const handleSearch = (value) => {
        setSearchTerm(value);
        setCurrentPage(1);
    }

    const {data, isLoading} = usePatients(currentPage - 1, pageSize, searchTerm);

    const patients = data?.content || [];
    const totalPages = data?.totalPages || 0;

    return (
        <ListContainer
            title="Patients"
            headerActions={
                <SearchInput onSearch={handleSearch}/>
            }
        >
            <div className="row row-cols-1 g-4 mt-2">
                {isLoading ? (
                    <div className="text-center p-5">Searching...</div>
                ) : patients.length > 0 ? (
                    patients.map(patient => (
                        <DataCard
                            key={patient.public_id}
                            name={`${patient.firstName} ${patient.lastName}`}
                            details={[
                                {label: 'Email', value: patient.email},
                                {label: 'Phone', value: patient.phoneNumber},
                                {
                                    label: 'Address',
                                    value: `${patient.address?.street}, ${patient.address?.number}, ${patient.address?.city}, ${patient.address?.country}`
                                }
                            ]}
                            renderActions={() => (
                                <button className="btn btn-outline-success btn-sm">Appointments</button>
                            )}
                        />
                    ))
                ) : (
                    <div className="text-center p-5 text-muted">No patients matched your search.</div>
                )}
            </div>

            <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={setCurrentPage}
            />
        </ListContainer>
    );
};

export default PatientList;