import {formatDateStandard} from "../../../utils/dateUtils.js";
import {
    formatDisease,
    formatMissingFields,
    riskGroupBadgeClass,
} from "../utils/riskLabels.js";

const RiskResultsPanel = ({risks = [], isLoading, isError}) => {
    return (
        <div className="card border-0 shadow-sm h-100">
            <div className="card-body p-4">
                <h3 className="h5 mb-1">Disease risk estimates</h3>
                <p className="text-muted small mb-4">
                    Latest estimated risk for selected diseases based on medical parameters and lab results.
                </p>

                {isLoading ? (
                    <div className="text-center py-5">
                        <div className="spinner-border text-primary" role="status"/>
                        <div className="mt-3 text-muted">Loading risk estimates...</div>
                    </div>
                ) : isError ? (
                    <div className="alert alert-danger mb-0">Failed to load risk estimates</div>
                ) : risks.length === 0 ? (
                    <div className="text-center text-muted py-5">
                        No risk estimates available yet. Upload lab results to generate disease risk scores.
                    </div>
                ) : (
                    <div className="d-flex flex-column gap-3">
                        {risks.map((risk) => {
                            const missing = formatMissingFields(risk.missingFields);
                            const isUnknown = risk.riskGroup === "UNKNOWN" || missing.length > 0;

                            return (
                                <div
                                    key={risk.disease}
                                    className="border rounded-3 p-3 bg-light"
                                >
                                    <div className="d-flex justify-content-between align-items-start gap-3 mb-2">
                                        <div>
                                            <div className="fw-semibold">{formatDisease(risk.disease)}</div>
                                            {risk.calculatedAt && (
                                                <div className="text-muted small">
                                                    Calculated {formatDateStandard(risk.calculatedAt)}
                                                    {risk.testDate
                                                        ? ` · test date ${formatDateStandard(risk.testDate)}`
                                                        : ""}
                                                </div>
                                            )}
                                        </div>
                                        <span className={riskGroupBadgeClass(risk.riskGroup)}>
                                            {risk.riskGroup}
                                        </span>
                                    </div>

                                    {isUnknown ? (
                                        <div className="small">
                                            <div className="text-muted mb-1">
                                                Risk cannot be calculated.
                                                {missing.length > 0 ? " Missing:" : ""}
                                            </div>
                                            {missing.length > 0 && (
                                                <ul className="mb-0 ps-3">
                                                    {missing.map((field) => (
                                                        <li key={field}>{field}</li>
                                                    ))}
                                                </ul>
                                            )}
                                        </div>
                                    ) : (
                                        <div className="display-6 fw-semibold mb-0">
                                            {Number(risk.riskPercent).toFixed(1)}
                                            <span className="fs-5 ms-1">%</span>
                                        </div>
                                    )}
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>
        </div>
    );
};

export default RiskResultsPanel;
