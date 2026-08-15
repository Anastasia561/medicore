import {useMutation} from '@tanstack/react-query';
import axios from "../../../api/axios.js";

export const useResetPassword = ({setError, setGeneralError, onSuccess}) => {
    return useMutation({
        mutationFn: async ({token, password, repeatPassword}) => {
            const response = await axios.post("/auth/reset-password", {
                token,
                password,
                repeatPassword
            });
            return response.data;
        },
        onSuccess: (data, variables, context) => {
            if (setGeneralError) setGeneralError("");
            if (onSuccess) {
                onSuccess(data, variables, context);
            }
        },
        onError: (err) => {
            const backendErrors = err.response?.data?.errors;
            const message = err.response?.data?.message || "Failed to reset password";

            if (backendErrors && setError) {
                Object.keys(backendErrors).forEach((field) => {
                    setError(field, {message: backendErrors[field]});
                });
            } else if (setGeneralError) {
                setGeneralError(message);
            }
        }
    });
};