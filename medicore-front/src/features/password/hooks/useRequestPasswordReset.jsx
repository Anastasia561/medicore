import {useMutation} from "@tanstack/react-query";
import axios from "../../../api/axios.js";

export const useRequestPasswordReset = ({setError, setGeneralError, onSuccess}) => {
    return useMutation({
        mutationFn: async ({email}) => {
            const response = await axios.post("/auth/reset-password/request", {email});
            return response.data;
        },
        onSuccess: (data, variables, context) => {
            setGeneralError("");

            if (onSuccess) {
                onSuccess(data, variables, context);
            }
        },
        onError: (err) => {
            if (!err?.response) {
                setGeneralError("Server is not responding");
                return;
            }

            const {status, data} = err.response;

            if (status === 400) {
                const validationErrors = data?.error?.validationErrors;

                if (Array.isArray(validationErrors)) {
                    validationErrors.forEach((errObj) => {
                        setError(errObj.field, {
                            type: "server",
                            message: errObj.message,
                        });
                    });
                }

                setGeneralError(data?.error?.message || "Invalid input");
                return;
            }

            setGeneralError(data?.error?.message || "Something went wrong");
        },
    });
};