import {useMutation} from "@tanstack/react-query";
import {loginRequest} from "../../api/auth.js";
import {jwtDecode} from "jwt-decode";

export const useLogin = ({setAuth, navigate, from, setGeneralError, setFormError}) => {
    return useMutation({
        mutationFn: loginRequest,

        onSuccess: (data) => {
            const accessToken = data?.accessToken;
            const decoded = jwtDecode(accessToken);

            setAuth({
                accessToken,
                role: decoded.role,
            });

            navigate(from, {replace: true});
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