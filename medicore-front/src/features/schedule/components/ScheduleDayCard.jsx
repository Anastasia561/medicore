import useAuth from "../../../hooks/useAuth.jsx";

const ScheduleDayCard = ({day, onDelete, onUpdate}) => {
    const {auth} = useAuth();

    return (
        <div className="day-card">
            <h3>{day.day}</h3>

            <div key={day.id} className="slot">
                <p>{day.startTime.slice(0, 5)} - {day.endTime.slice(0, 5)}</p>

                {auth?.role === 'ROLE_ADMIN' && (
                    <div className="actions">
                        <button className="edit" onClick={() => onUpdate(day)}>Edit</button>
                        <button className="delete" onClick={() => onDelete(day.id)}>Delete</button>
                    </div>
                )}
            </div>

        </div>
    );
};

export default ScheduleDayCard;