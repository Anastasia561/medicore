import ScheduleDayCard from "./ScheduleDayCard";
import {useSchedule} from "./hooks/useSchedule.jsx";
import {useParams} from "react-router-dom";

const DoctorSchedule = () => {
    const {doctorId} = useParams();
    const {data, isLoading} = useSchedule(doctorId);

    if (isLoading) return <p>Loading...</p>;

    return (
        <div className="schedule-container">
            <h2>Weekly Schedule</h2>

            <div className="schedule-grid">
                {data.map(day => (
                    <ScheduleDayCard key={day.publicId} day={day}/>
                ))}
            </div>

            <button>Add Consultation Hours</button>
        </div>
    );
};

export default DoctorSchedule;