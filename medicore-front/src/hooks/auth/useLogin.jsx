import {useMutation} from "@tanstack/react-query";
import {loginRequest} from "../../api/auth.js";
import {jwtDecode} from "jwt-decode";

export const useLogin = ({setAuth, navigate, from, setError, username}) => {
    return useMutation({
        mutationFn: loginRequest,

        onSuccess: (data) => {
            const accessToken = data?.accessToken;
            const decoded = jwtDecode(accessToken);

            setAuth({
                username,
                accessToken,
                role: decoded.role,
            });

            navigate(from, {replace: true});
        },

        onError: (err) => {
            if (!err?.response) {
                setError("Server is not responding. Try again later.");
            } else if (err.response.status === 400) {
                setError("Invalid request. Please check your input.");
            } else if (err.response.status === 401) {
                setError("Incorrect email or password.");
            } else if (err.response.status === 403) {
                navigate("/unauthorized");
            } else {
                setError("Something went wrong. Please try again.");
            }
        },
    });
};