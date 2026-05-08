import ScheduleDayCard from "./ScheduleDayCard";
import {useSchedule} from "./hooks/useSchedule.jsx";
import {useLocation, useParams} from "react-router-dom";
import "./Schedule.css";
import useAuth from "../../hooks/auth/useAuth.jsx";
import {useState} from "react";
import ConsultationForm from "./ConsultationForm.jsx";
import {useDeleteSchedule} from "./hooks/useDeleteSchedule.jsx";

const DoctorSchedule = () => {
    const {doctorId} = useParams();
    const {data, isLoading} = useSchedule(doctorId);
    const {state} = useLocation();
    const {auth} = useAuth();
    const [isModalOpen, setIsModalOpen] = useState(false);

    const displayName = state?.doctorName || data?.doctorName || "Doctor";
    const { mutate: deleteConsultation } = useDeleteSchedule(doctorId);

    const handleDelete = (id) => {
        if (window.confirm("Are you sure you want to delete this consultation?")) {
            deleteConsultation(id);
        }
    };

    const handleAddConsultation = () => {
        setIsModalOpen(false);
    };

    if (isLoading) return <p>Loading...</p>;

    return (
        <div className="schedule-container">
            <h2>{displayName}'s Weekly Schedule</h2>

            <div className="schedule-grid">
                {data.map(day => (
                    <ScheduleDayCard key={day.publicId} day={day} onDelete={handleDelete} />
                ))}
            </div>

            {auth?.role === 'ROLE_ADMIN' && (
                <div className="d-flex justify-content-center mt-4">
                    <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>Add Consultation Hours
                    </button>
                </div>
            )}

            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content-custom">
                        <h3>Add New Hours</h3>
                        <ConsultationForm
                            doctorId={doctorId}
                            onSubmit={handleAddConsultation}
                            onCancel={() => setIsModalOpen(false)}
                        />
                    </div>
                </div>
            )}
        </div>
    );
};

export default DoctorSchedule;