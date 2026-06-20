import {useMutation, useQueryClient} from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

export const useCreateAppointment = () => {
    const axiosPrivate = useAxiosPrivate();
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async (appointmentPayload) => {
            const res = await axiosPrivate.post("/appointments", appointmentPayload);
            if (res.data?.error) {
                throw new Error(res.data.error);
            }
            return res.data;
        },

        onSuccess: (responseData, variables) => {
            toast.success("Appointment booked successfully");
            queryClient.invalidateQueries({queryKey: ["appointments"]});
            if (variables.doctorId && variables.date) {
                queryClient.invalidateQueries({
                    queryKey: ["available-slots", variables.doctorId, variables.date]
                });
            }
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
            } else {
                toast.error("Something went wrong");
            }
        }
    });
};