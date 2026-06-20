import {useMutation, useQueryClient} from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

export const useCreateMedicalRecord = () => {
    const axiosPrivate = useAxiosPrivate();
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async (recordData) => {
            const res = await axiosPrivate.post("/records", recordData);
            if (res.data?.error) {
                throw new Error(res.data.error);
            }
            return res.data.data;
        },
        onSuccess: (responseData, variables) => {
            toast.success("Medical record created successfully");
            queryClient.invalidateQueries({queryKey: ["records-preview"]});
            if (variables.appointmentId) {
                queryClient.invalidateQueries({queryKey: ["appointments"]});
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