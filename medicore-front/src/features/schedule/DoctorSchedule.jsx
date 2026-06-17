import ScheduleDayCard from "./components/ScheduleDayCard.jsx";
import {useSchedule} from "./hooks/useSchedule.jsx";
import {useLocation, useParams} from "react-router-dom";
import "./DoctorSchedule.css";
import useAuth from "../../hooks/useAuth.jsx";
import {useState} from "react";
import ConsultationForm from "./components/ConsultationForm.jsx";
import {useDeleteSchedule} from "./hooks/useDeleteSchedule.jsx";
import ConfirmModal from "../../components/ConfirmModal.jsx";

const DoctorSchedule = () => {
    const {doctorId} = useParams();
    const {data, isLoading, isError} = useSchedule(doctorId);
    const {state} = useLocation();
    const {auth} = useAuth();
    const [selectedSchedule, setSelectedSchedule] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [confirmId, setConfirmId] = useState(null);

    const displayName = state?.doctorName || data?.doctorName || "Doctor";
    const {mutate: deleteConsultation} = useDeleteSchedule(doctorId);

    const handleDelete = (id) => {
        setConfirmId(id);
    };

    const handleCloseModal = () => {
        setSelectedSchedule(null);
        setIsModalOpen(false);
    };

    const handleUpdate = (day) => {
        setSelectedSchedule(day);
    };

    if (isLoading) {
        return (
            <div className="container p-4 text-center mt-5">
                <div className="spinner-border text-primary" role="status"/>
                <div className="mt-3 text-muted">
                    Loading schedule...
                </div>
            </div>
        );
    }

    if (isError) return <p>Failed to load schedule</p>;

    return (
        <div className="schedule-container">
            <h2>{displayName}'s Weekly Schedule</h2>

            <div className="schedule-grid">
                {data.map(day => (
                    <ScheduleDayCard key={day.id} day={day} onDelete={handleDelete} onUpdate={handleUpdate}/>
                ))}
            </div>

            {auth?.role === 'ROLE_ADMIN' && (
                <div className="d-flex justify-content-center mt-4">
                    <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>Add Consultation Hours
                    </button>
                </div>
            )}

            {(isModalOpen || selectedSchedule) && (
                <div className="modal-content-custom">
                    <h3>{selectedSchedule ? "Edit Hours" : "Add New Hours"}</h3>
                    <ConsultationForm
                        doctorId={doctorId}
                        initialData={selectedSchedule}
                        onSubmit={handleCloseModal}
                        onCancel={handleCloseModal}
                    />
                </div>
            )}

            {confirmId && (
                <ConfirmModal
                    title="Delete consulation hours?"
                    message="This action cannot be undone."
                    cancelText="No, keep it"
                    onCancel={() => setConfirmId(null)}
                    onConfirm={() => {
                        deleteConsultation(confirmId)
                        setConfirmId(null);
                    }}
                />
            )}
        </div>
    );
};

export default DoctorSchedule;