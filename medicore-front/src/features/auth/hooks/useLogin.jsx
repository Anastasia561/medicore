import {useMutation} from "@tanstack/react-query";
import {loginRequest} from "../../../api/auth.js";
import {jwtDecode} from "jwt-decode";

const redirectPathByRole = {
    ROLE_PATIENT: "/appointments",
    ROLE_DOCTOR: "/appointments",
    ROLE_ADMIN: "/statistics",
};

export const useLogin = ({setAuth, navigate, setGeneralError, setFormError}) => {
    return useMutation({
        mutationFn: loginRequest,

        onSuccess: (data) => {
            const accessToken = data?.accessToken;
            const decoded = jwtDecode(accessToken);
            const redirectPath = redirectPathByRole[decoded.role] ?? "/";

            setAuth({
                accessToken,
                role: decoded.role,
            });

            navigate(redirectPath, {replace: true});
        },
        onError: (err) => {
            if (!err?.response) {
                setGeneralError("Server is not responding. Try again later.");
                return;
            }

            const {status, data} = err.response;

            if (status === 400) {
                const validationErrors = data?.error?.validationErrors;

                if (Array.isArray(validationErrors)) {
                    validationErrors.forEach((errObj) => {
                        const fieldName = errObj.field === 'email' ? 'username' : errObj.field;

                        setFormError(fieldName, {
                            type: "server",
                            message: errObj.message
                        });
                    });
                }
                setGeneralError("Validation failed");

            } else if (status === 401) {
                setGeneralError("Invalid email or password");
            } else if (status === 403) {
                navigate("/unauthorized");
            } else {
                setGeneralError("Something went wrong. Please try again");
            }
        }
    });
};