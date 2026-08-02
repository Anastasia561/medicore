import {useMutation} from "@tanstack/react-query";
import axios from "../../../../api/axios.js";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

export const useRegisterPatient = (setError, setGeneralError) => {
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async (patientData) => {
            const res = await axios.post("/patients/register", patientData);
            return res.data;
        },
        onSuccess: () => {
            toast.success("Registration successful\nPlease check email for verification");
            navigate("/login");
        },
        onError: (err) => {
            if (!err?.response) {
                setGeneralError("Server is not responding");
                return;
            }

            const {status, data} = err.response;

            if (status === 400) {
                const validationErrors = data?.error?.validationErrors;

                if (Array.isArray(validationErrors)) {
                    const emailError = validationErrors.find((errObj) => errObj.field === "email");

                    if (emailError) {
                        setGeneralError(emailError.message);
                    } else {
                        setGeneralError(data?.error?.message || "Validation failed");
                    }

                    validationErrors.forEach((errObj) => {
                        setError(errObj.field, {message: errObj.message});
                    });
                } else {
                    setGeneralError(data?.error?.message || "Invalid input");
                }
            } else {
                setGeneralError("Something went wrong");
            }
        },
    });
};