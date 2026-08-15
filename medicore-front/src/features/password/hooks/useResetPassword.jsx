import {useMutation} from '@tanstack/react-query';
import axios from "../../../api/axios.js";
import {useNavigate} from "react-router-dom";
import toast from "react-hot-toast";

export const useResetPassword = ({setError, setGeneralError}) => {
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async ({token, email, password, repeatPassword}) => {
            const response = await axios.post("/auth/reset-password", {
                token,
                email,
                password,
                repeatPassword
            });
            return response.data;
        },
        onSuccess: () => {
            toast.success("Password reset successfully");
            navigate("/login");
        },
        onError: (err) => {
            const backendErrors = err.response?.data?.errors;
            const message = err.response?.data?.message || "Failed to reset password";

            if (backendErrors && setError) {
                Object.keys(backendErrors).forEach((field) => {
                    setError(field, {message: backendErrors[field]});
                });
            } else if (setGeneralError) {
                setGeneralError(message);
            }
        }
    });
};
