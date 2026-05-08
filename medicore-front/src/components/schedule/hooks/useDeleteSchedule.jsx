import { useMutation, useQueryClient } from "@tanstack/react-query";
import {axiosPrivate} from "../../../api/axios.js";

export const useDeleteSchedule = (doctorId) => {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (consultationId) => {
            await axiosPrivate.delete(`/consultations/${consultationId}`);
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['doctor-schedule', doctorId] });
        }
    });
};