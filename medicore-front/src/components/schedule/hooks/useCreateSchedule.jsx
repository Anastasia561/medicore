import {useMutation, useQueryClient} from "@tanstack/react-query";
import {axiosPrivate} from "../../../api/axios.js";
import {useNavigate} from "react-router-dom";

export const useCreateSchedule = (doctorId, {setGeneralError, setFormError}) => {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async (newSlot) => {
            const response = await axiosPrivate.post(`/consultations`, {...newSlot, doctorId});
            return response.data;
        },
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: ['doctor-schedule', doctorId]});
            setGeneralError("");
        },
        onError: (err) => {
            if (!err?.response) {
                setGeneralError("Server is not responding.");
                return;
            }
            const {status, data} = err.response;

            if (status === 400) {
                const validationErrors = data?.error?.validationErrors;
                if (Array.isArray(validationErrors) && validationErrors.length > 0) {
                    validationErrors.forEach((errObj) => {
                        setFormError(errObj.field, {
                            type: "server",
                            message: errObj.message
                        });
                    });
                    setGeneralError("Validation failed");
                } else {
                    setGeneralError(data?.error?.message);
                }
            } else if (status === 401) {
                navigate("/login");
            } else if (status === 403) {
                navigate("/unauthorized");
            } else if (status === 409) {
                setGeneralError(data?.error?.message);
            } else {
                setGeneralError("Something went wrong.");
            }
        }
    });
};