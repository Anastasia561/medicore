const homePathByRole = {
    ROLE_PATIENT: "/appointments",
    ROLE_DOCTOR: "/appointments",
    ROLE_ADMIN: "/statistics",
};

export const getHomePath = (role) => homePathByRole[role] ?? "/";
