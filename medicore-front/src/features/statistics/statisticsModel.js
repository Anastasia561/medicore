export const MONTHS = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

export const APPOINTMENT_STATUSES = [
    {key: "COMPLETED", label: "Completed", color: "#198754"},
    {key: "SCHEDULED", label: "Scheduled", color: "#0b5ed7"},
    {key: "CANCELLED", label: "Cancelled", color: "#dc3545"},
    {key: "MISSED", label: "Missed", color: "#fd7e14"},
];

const SPECIALIZATION_LABELS = {
    CARDIOLOGIST: "Cardiologist",
    DERMATOLOGIST: "Dermatologist",
    NEUROLOGIST: "Neurologist",
    PEDIATRICIAN: "Pediatrician",
    ONCOLOGIST: "Oncologist",
};

export const specializationLabel = (value) =>
    SPECIALIZATION_LABELS[value] || value;

export const buildMonthlySeries = (rows = []) => {
    const series = MONTHS.map((label) => ({
        label,
        counts: Object.fromEntries(APPOINTMENT_STATUSES.map((status) => [status.key, 0])),
        total: 0,
    }));

    rows.forEach((row) => {
        const index = Number(row.month) - 1;
        const bucket = series[index];
        if (!bucket || bucket.counts[row.status] === undefined) return;
        bucket.counts[row.status] += Number(row.count) || 0;
        bucket.total += Number(row.count) || 0;
    });

    return series;
};
