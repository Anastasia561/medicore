import {useMutation, useQueryClient} from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";

export const useCreateMedicalRecord = () => {
    const axiosPrivate = useAxiosPrivate();
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async (recordData) => {
            const res = await axiosPrivate.post("/records", recordData);
            if (res.data?.error) {
                throw new Error(res.data.error);
            }
            return res.data.data;
        },
        onSuccess: (responseData, variables) => {
            queryClient.invalidateQueries({queryKey: ["records-preview"]});
            if (variables.appointmentId) {
                queryClient.invalidateQueries({queryKey: ["appointments"]});
            }
        }
    });
};