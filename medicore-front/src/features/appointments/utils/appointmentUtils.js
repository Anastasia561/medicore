const formatDateString = (dateObj) => {
    return dateObj.toISOString().split('T')[0];
};

export const getStatusAlertClass = (status) => {
    if (status === 'CANCELLED') return 'bg-danger-subtle border-danger';
    if (status === 'COMPLETED') return 'bg-success-subtle border-success';
    return 'bg-info-subtle border-info'; // SCHEDULED / default
};

export const getStatusTextClass = (status) => {
    if (status === 'CANCELLED') return 'text-danger';
    if (status === 'COMPLETED') return 'text-success';
    return 'text-info-emphasis';
};

export const groupAppointmentsByDate = (appointments = []) => {
    return appointments.reduce((groups, appointment) => {
        const date = appointment.date;
        if (!groups[date]) groups[date] = [];
        groups[date].push(appointment);
        return groups;
    }, {});
};

export const getMonday = (date) => {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - (day === 0 ? 6 : day - 1);

    const monday = new Date(d.setDate(diff));
    return formatDateString(monday);
};

export const getDaysArray = (startStr) => {
    const start = new Date(startStr);
    return Array.from({length: 5}, (_, i) => {
        const nextDay = new Date(start);
        nextDay.setDate(start.getDate() + i);
        return formatDateString(nextDay);
    });
};