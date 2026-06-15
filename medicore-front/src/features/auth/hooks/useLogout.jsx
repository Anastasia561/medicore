import {useMutation, useQueryClient} from "@tanstack/react-query";
import useAuth from "../../../hooks/useAuth.jsx";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";

export const useLogout = () => {
    const axiosPrivate = useAxiosPrivate();
    const {setAuth} = useAuth();
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: async () => {
            return await axiosPrivate.post("/auth/logout");
        },

        onSettled: () => {
            setAuth(null);
            queryClient.clear();
        }
    });
};