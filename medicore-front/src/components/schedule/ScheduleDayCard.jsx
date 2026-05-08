import useAuth from "../../hooks/auth/useAuth.jsx";

const ScheduleDayCard = ({day, onDelete}) => {
    const {auth} = useAuth();

    return (
        <div className="day-card">
            <h3>{day.day}</h3>

            <div key={day.publicId} className="slot">
                <p>{day.startTime} - {day.endTime}</p>

                {auth?.role === 'ROLE_ADMIN' && (
                    <div className="actions">
                        <button className="edit">Edit</button>
                        <button className="delete" onClick={() => onDelete(day.publicId)}>Delete</button>
                    </div>
                )}
            </div>

        </div>
    );
};

export default ScheduleDayCard;