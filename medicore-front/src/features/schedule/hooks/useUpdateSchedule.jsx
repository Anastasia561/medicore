import {useMutation, useQueryClient} from "@tanstack/react-query";
import {axiosPrivate} from "../../../api/axios.js";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";

export const useUpdateSchedule = (doctorId, {setGeneralError, setFormError} = {}) => {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async ({id, startTime, endTime}) => {
            const response = await axiosPrivate.put(`/consultations/${id}`, {
                startTime,
                endTime
            });
            return response.data;
        },
        onSuccess: () => {
            toast.success('Schedule updated successfully');
            queryClient.invalidateQueries({queryKey: ['doctor-schedule', doctorId]});
        },
        onError: (err) => {
            if (!err?.response) {
                toast.error("Server is not responding.");
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
            } else if (status === 404) {
                toast.error("Consultation not found");
            } else {
                toast.error("Something went wrong");
            }
        }
    });
};
