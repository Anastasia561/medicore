import {useMutation, useQueryClient} from "@tanstack/react-query";
import {axiosPrivate} from "../../../api/axios.js";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

export const useDeleteSchedule = (doctorId) => {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async (consultationId) => {
            await axiosPrivate.delete(`/consultations/${consultationId}`);
        },
        onSuccess: () => {
            toast.success("Consultation deleted successfully");
            queryClient.invalidateQueries({queryKey: ['doctor-schedule', doctorId]});
        },
        onError: (err) => {
            if (!err?.response) {
                toast.error("Server is not responding.");
                return;
            }
            const {status} = err.response;

            if (status === 401) {
                navigate("/login");
            } else if (status === 403) {
                navigate("/unauthorized");
            } else if (status === 404) {
                toast.error("Consultation not found");
            } else {
                toast.error("Something went wrong");
            }
        }
    });
};