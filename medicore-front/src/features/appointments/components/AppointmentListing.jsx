import {useState} from 'react';
import {useAppointments} from '../hooks/useAppointments';
import {useLocation, useParams} from "react-router-dom";
import {
    getMonday,
    groupAppointmentsByDate,
    getStatusAlertClass,
    getStatusTextClass,
    getDaysArray
} from "../utils/appointmentUtils.js"

const AppointmentDashboard = () => {
    const {userId} = useParams();
    const {state} = useLocation();

    const displayName = state?.doctorName || "Doctor";

    const [startDate, setStartDate] = useState(getMonday(new Date()));
    const [statusFilter, setStatusFilter] = useState("ALL");

    const currentDays = getDaysArray(startDate);
    const endDate = currentDays[4];

    const handlePrevWeek = () => {
        const currentStart = new Date(startDate);
        currentStart.setDate(currentStart.getDate() - 7);
        setStartDate(getMonday(currentStart));
    };

    const handleNextWeek = () => {
        const currentStart = new Date(startDate);
        currentStart.setDate(currentStart.getDate() + 7);
        setStartDate(getMonday(currentStart));
    };

    const {data: appointments = [], isLoading, isError} = useAppointments({
        userId,
        startDate,
        endDate,
        status: statusFilter
    });

    const groupedAppointments = groupAppointmentsByDate(appointments);

    if (isLoading) {
        return (
            <div className="container p-4 text-center mt-5">
                <div className="spinner-border text-primary" role="status"/>
                <div className="mt-3 text-muted">
                    Loading appointments...
                </div>
            </div>
        );
    }

    if (isError) return <div className="alert alert-danger m-4">Failed to load appointments</div>;

    return (
        <div className="container-fluid p-4 bg-light min-vh-100">
            <h2 className="mb-4">Appointments for {displayName}</h2>

            <div className="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">

                <div className="d-flex gap-3 align-items-end">
                    <div>
                        <label className="form-label small fw-bold text-secondary mb-1">Start Date</label>
                        <input
                            type="date"
                            className="form-control"
                            value={startDate}
                            onChange={(e) => setStartDate(e.target.value)}
                        />
                    </div>
                    <div>
                        <label className="form-label small fw-bold text-secondary mb-1">Filter By Status</label>
                        <select
                            className="form-select"
                            value={statusFilter}
                            onChange={(e) => setStatusFilter(e.target.value)}
                        >
                            <option value="ALL">All Statuses</option>
                            <option value="SCHEDULED">Scheduled</option>
                            <option value="CANCELLED">Cancelled</option>
                            <option value="COMPLETED">Completed</option>
                            <option value="MISSED">Missed</option>
                        </select>
                    </div>
                </div>

                <div className="d-flex gap-2">
                    <button onClick={handlePrevWeek} className="btn btn-outline-secondary">
                        &larr; Previous Week
                    </button>
                    <button onClick={() => setStartDate(getMonday(new Date()))} className="btn btn-outline-secondary">
                        Today
                    </button>
                    <button onClick={handleNextWeek} className="btn btn-outline-secondary">
                        Next Week &rarr;
                    </button>
                </div>
            </div>

            <div className="row row-cols-1 row-cols-md-5 g-3">
                {currentDays.map((day) => {
                    const dayAppointments = groupedAppointments[day] || [];

                    const formattedHeader = new Date(day).toLocaleDateString('en-US', {
                        weekday: 'short',
                        month: 'short',
                        day: 'numeric',
                        timeZone: 'UTC'
                    });

                    return (
                        <div key={day} className="col">
                            <div className="card h-100 shadow-sm border-light" style={{minHeight: '450px'}}>
                                <div className="card-body p-3">
                                    <h3 className="card-title h5 text-dark pb-2 mb-3 border-bottom border-2 border-light fw-semibold">
                                        {formattedHeader}
                                    </h3>

                                    <div className="d-flex flex-column gap-2">
                                        {dayAppointments.length === 0 ? (
                                            <div className="text-muted small text-center mt-4">
                                                No appointments
                                            </div>
                                        ) : (
                                            dayAppointments.map((app) => (
                                                <div
                                                    key={app.id}
                                                    className={`p-3 rounded-2 border-start border-4 ${getStatusAlertClass(app.status)}`}
                                                >
                                                    <div className="fw-bold text-dark small mb-1">
                                                        {app.startTime?.substring(0, 5) || "N/A"} - {app.endTime?.substring(0, 5) || "N/A"}
                                                    </div>
                                                    <div className="text-dark small fw-medium">
                                                        {app.firstName} {app.lastName}
                                                    </div>
                                                    <div className="text-muted extra-small mb-2"
                                                         style={{fontSize: '0.8rem'}}>
                                                        {app.phoneNumber}
                                                    </div>
                                                    <span
                                                        className={`badge bg-white bg-opacity-75 ${getStatusTextClass(app.status)}`}>
                                                        {app.status}
                                                    </span>
                                                </div>
                                            ))
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
};

export default AppointmentDashboard;