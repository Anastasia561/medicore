import {useMutation, useQueryClient} from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

export const useUpdateProfile = ({setGeneralError, setError}) => {
    const axiosPrivate = useAxiosPrivate();
    const queryClient = useQueryClient();
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async (profileData) => {
            const res = await axiosPrivate.put("/profiles", profileData);
            if (res.data?.error) {
                throw new Error(res.data.error);
            }
            return res.data.data;
        },
        onSuccess: () => {
            toast.success("Profile updated successfully");
            queryClient.invalidateQueries({queryKey: ["me"]});
            setGeneralError("");
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
                    validationErrors.forEach((errObj) => {
                        setError(errObj.field, {
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
            } else {
                toast.error("Something went wrong");
            }
        }
    });
};