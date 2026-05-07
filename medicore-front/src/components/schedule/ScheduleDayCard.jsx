const ScheduleDayCard = ({day}) => {
    return (
        <div className="day-card">
            <h3>{day.day}</h3>

            <div key={day.publicId} className="slot">
                <p>{day.startTime} - {day.endTime}</p>

                <div className="actions">
                    <button className="edit">Edit</button>
                    <button className="delete">Delete</button>
                </div>
            </div>

        </div>
    );
};

export default ScheduleDayCard;