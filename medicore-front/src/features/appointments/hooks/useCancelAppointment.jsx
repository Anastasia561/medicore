import {useMutation, useQueryClient} from "@tanstack/react-query";
import {axiosPrivate} from "../../../api/axios.js";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";

export const useCancelAppointment = () => {
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async (appId) => {
            const response = await axiosPrivate.put(`/appointments/cancel/${appId}`);
            return response.data;
        },
        onSuccess: () => {
            toast.success('Appointment cancelled successfully');
            queryClient.invalidateQueries({queryKey: ['appointments']});
        },
        onError: (err) => {
            if (!err?.response) {
                toast.error("Server is not responding.");
                return;
            }
            const {status} = err.response;

            if (status === 401) {
                navigate("/login");
            } else if (status === 409) {
                toast.error("Can not cancel appointment");
            } else if (status === 404) {
                toast.error("Appointment not found");
            } else {
                toast.error("Something went wrong");
            }
        }
    });
};
