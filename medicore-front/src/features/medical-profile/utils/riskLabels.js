const FIELD_LABELS = {
    gender: "gender",
    pregnancyStatus: "pregnancy status",
    weight: "weight",
    height: "height",
    glucoseTestResult: "glucose lab result",
    creatinineTestResult: "creatinine lab result",
    hgbTestResult: "hemoglobin (HGB) lab result",
    hctTestResult: "hematocrit (HCT) lab result",
    rbcTestResult: "red blood cell (RBC) lab result",
};

const DISEASE_LABELS = {
    ANEMIA: "Anemia",
    DIABETES: "Diabetes",
    CKD: "Chronic kidney disease",
};

const RISK_GROUP_STYLES = {
    UNKNOWN: "secondary",
    NONE: "success",
    LOW: "info",
    MEDIUM: "warning",
    HIGH: "danger",
};

export const formatMissingFields = (missingFields) => {
    if (!missingFields) return [];
    return missingFields
        .split(",")
        .map((field) => field.trim())
        .filter(Boolean)
        .map((field) => FIELD_LABELS[field] || field);
};

export const formatDisease = (disease) => DISEASE_LABELS[disease] || disease;

export const riskGroupBadgeClass = (riskGroup) =>
    `badge text-bg-${RISK_GROUP_STYLES[riskGroup] || "secondary"}`;
