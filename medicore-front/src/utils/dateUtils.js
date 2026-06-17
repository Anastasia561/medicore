const toSafeDate = (dateValue) => {
    if (!dateValue) return null;
    if (dateValue instanceof Date) return dateValue;
    const parts = typeof dateValue === 'string' ? dateValue.split('-') : [];
    if (parts.length === 3) {
        return new Date(parts[0], parts[1] - 1, parts[2]);
    }

    return new Date(dateValue);
};

export const formatDateStandard = (dateValue) => {
    const dateObj = toSafeDate(dateValue);
    if (!dateObj || isNaN(dateObj)) return "N/A";

    return dateObj.toLocaleDateString(undefined, {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
};

export const formatDateHeader = (dateValue) => {
    const dateObj = toSafeDate(dateValue);
    if (!dateObj || isNaN(dateObj)) return "N/A";

    return dateObj.toLocaleDateString(undefined, {
        weekday: 'short',
        month: 'short',
        day: 'numeric'
    });
};