import { useMutation, useQueryClient } from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";

export const useCreateAppointment = () => {
    const axiosPrivate = useAxiosPrivate();
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (appointmentPayload) => {
            const res = await axiosPrivate.post("/appointments", appointmentPayload);
            if (res.data?.error) {throw new Error(res.data.error);}
            return res.data;
        },

        onSuccess: (responseData, variables) => {
            queryClient.invalidateQueries({ queryKey: ["appointments"] });
            if (variables.doctorId && variables.date) {
                queryClient.invalidateQueries({
                    queryKey: ["available-slots", variables.doctorId, variables.date]
                });
            }
        }
    });
};