import {useMutation, useQueryClient} from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

export const useUploadLabResult = () => {
    const axiosPrivate = useAxiosPrivate();
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async ({file, date}) => {
            const formData = new FormData();
            formData.append("file", file);
            formData.append("date", date);

            const res = await axiosPrivate.post("/tests", formData, {
                headers: {"Content-Type": undefined},
            });

            if (res.data?.error) {
                throw new Error(res.data.error);
            }
            return res.data.data;
        },
        onSuccess: () => {
            toast.success("Lab result uploaded successfully");
            queryClient.invalidateQueries({queryKey: ["lab-results"]});
        },
        onError: (err) => {
            if (!err?.response) {
                toast.error("Server is not responding");
                return;
            }

            const {status, data} = err.response;

            if (status === 400) {
                const validationErrors = data?.error?.validationErrors;
                if (Array.isArray(validationErrors) && validationErrors.length > 0) {
                    validationErrors.forEach((error) => toast.error(error.message));
                } else {
                    toast.error(data?.error?.message || "Invalid lab result upload");
                }
            } else if (status === 401) {
                navigate("/login");
            } else if (status === 403) {
                navigate("/unauthorized");
            } else {
                toast.error("Something went wrong");
            }
        },
    });
};
