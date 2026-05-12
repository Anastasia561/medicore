import useAuth from "../../hooks/auth/useAuth.jsx";

const ScheduleDayCard = ({day, onDelete, onUpdate}) => {
    const {auth} = useAuth();

    return (
        <div className="day-card">
            <h3>{day.day}</h3>

            <div key={day.publicId} className="slot">
                <p>{day.startTime.slice(0, 5)} - {day.endTime.slice(0, 5)}</p>

                {auth?.role === 'ROLE_ADMIN' && (
                    <div className="actions">
                        <button className="edit" onClick={() => onUpdate(day.publicId)}>Edit</button>
                        <button className="delete" onClick={() => onDelete(day.publicId)}>Delete</button>
                    </div>
                )}
            </div>

        </div>
    );
};

export default ScheduleDayCard;