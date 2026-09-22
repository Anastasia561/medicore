import {useAdminStatistics} from "./hooks/useAdminStatistics.jsx";
import {
    APPOINTMENT_STATUSES,
    buildMonthlySeries,
    specializationLabel,
} from "./statisticsModel.js";
import "./StatisticsDashboard.css";

const SUMMARY_CARDS = [
    {
        key: "totalPatients",
        label: "Patients",
        hint: "Registered patients",
        icon: "fa-user-injured",
        tone: {background: "#e7f1ff", color: "#0b5ed7"},
    },
    {
        key: "totalDoctors",
        label: "Doctors",
        hint: "Registered doctors",
        icon: "fa-user-md",
        tone: {background: "#e8f7f1", color: "#198754"},
    },
    {
        key: "consultationsToday",
        label: "Consultations today",
        hint: "Appointments dated today",
        icon: "fa-calendar-day",
        tone: {background: "#fff4e5", color: "#fd7e14"},
    },
];

const StatCard = ({label, hint, value, icon, tone}) => (
    <div className="stats-card d-flex align-items-center gap-3">
        <div className="stat-icon" style={tone}>
            <i className={`fas ${icon}`} aria-hidden="true"/>
        </div>
        <div>
            <div className="text-muted small">{label}</div>
            <div className="stat-value">{Number(value || 0).toLocaleString()}</div>
            <div className="text-muted small">{hint}</div>
        </div>
    </div>
);

const MonthlyConsultations = ({rows}) => {
    const series = buildMonthlySeries(rows);
    const max = Math.max(...series.map((month) => month.total), 0);
    const yearTotal = series.reduce((sum, month) => sum + month.total, 0);
    const year = new Date().getFullYear();

    return (
        <div className="stats-card">
            <div className="d-flex flex-column flex-sm-row justify-content-between align-items-sm-start gap-3 mb-3">
                <div>
                    <h2 className="h5 mb-1">Consultations in {year}</h2>
                    <p className="text-muted small mb-0">Appointments grouped by month and status.</p>
                </div>
                <div className="text-sm-end">
                    <div className="stat-value">{yearTotal.toLocaleString()}</div>
                    <div className="text-muted small">this year</div>
                </div>
            </div>

            {yearTotal === 0 ? (
                <p className="text-muted mb-0">No consultations recorded this year.</p>
            ) : (
                <>
                    <div className="chart-plot" role="img" aria-label={`Monthly consultations in ${year}`}>
                        {series.map((month) => (
                            <div className="chart-column" key={month.label}>
                                <div
                                    className="chart-stack"
                                    style={{height: month.total ? `max(${(month.total / max) * 100}%, 4px)` : 0}}
                                    title={APPOINTMENT_STATUSES
                                        .map((status) => `${status.label}: ${month.counts[status.key]}`)
                                        .join(", ")}
                                >
                                    {APPOINTMENT_STATUSES.map((status) => (
                                        month.counts[status.key] > 0 && (
                                            <div
                                                key={status.key}
                                                className="chart-segment"
                                                style={{
                                                    flexGrow: month.counts[status.key],
                                                    background: status.color,
                                                }}
                                            />
                                        )
                                    ))}
                                </div>
                            </div>
                        ))}
                    </div>
                    <div className="chart-labels">
                        {series.map((month) => (
                            <span className="chart-month" key={month.label}>{month.label}</span>
                        ))}
                    </div>
                    <div className="d-flex flex-wrap gap-3 mt-3">
                        {APPOINTMENT_STATUSES.map((status) => (
                            <span key={status.key} className="small text-muted d-inline-flex align-items-center gap-2">
                                <span className="chart-legend-swatch" style={{background: status.color}}/>
                                {status.label}
                            </span>
                        ))}
                    </div>
                </>
            )}
        </div>
    );
};

const DoctorsBySpecialization = ({rows = []}) => {
    const items = [...rows].sort((a, b) => Number(b.count) - Number(a.count));
    const max = Math.max(...items.map((item) => Number(item.count) || 0), 0);

    return (
        <div className="stats-card">
            <h2 className="h5 mb-1">Doctors by specialization</h2>
            <p className="text-muted small mb-4">How the medical staff is distributed.</p>

            {items.length === 0 ? (
                <p className="text-muted mb-0">No doctors registered.</p>
            ) : (
                items.map((item) => {
                    const count = Number(item.count) || 0;
                    const width = max ? `${(count / max) * 100}%` : "0%";
                    return (
                        <div className="mb-3" key={item.specialization}>
                            <div className="d-flex justify-content-between mb-1">
                                <span>{specializationLabel(item.specialization)}</span>
                                <span className="text-muted">{count.toLocaleString()}</span>
                            </div>
                            <div className="spec-track" aria-hidden="true">
                                <div className="spec-fill" style={{width}}/>
                            </div>
                        </div>
                    );
                })
            )}
        </div>
    );
};

const StatisticsDashboard = () => {
    const {data, isLoading, isError} = useAdminStatistics();

    if (isLoading) {
        return (
            <div className="container p-4 text-center mt-5">
                <div className="spinner-border text-primary" role="status"/>
                <div className="mt-3 text-muted">Loading statistics...</div>
            </div>
        );
    }

    if (isError) {
        return <div className="alert alert-danger m-4">Failed to load statistics</div>;
    }

    return (
        <div className="stats-page">
            <h1 className="h3 mb-4">Statistics</h1>
            <div className="row g-3 mb-3">
                {SUMMARY_CARDS.map((card) => (
                    <div className="col-md-4" key={card.key}>
                        <StatCard
                            label={card.label}
                            hint={card.hint}
                            value={data?.[card.key]}
                            icon={card.icon}
                            tone={card.tone}
                        />
                    </div>
                ))}
            </div>
            <div className="row g-3">
                <div className="col-lg-7">
                    <MonthlyConsultations rows={data?.monthlyConsultations}/>
                </div>
                <div className="col-lg-5">
                    <DoctorsBySpecialization rows={data?.doctorsBySpecialization}/>
                </div>
            </div>
        </div>
    );
};

export default StatisticsDashboard;
