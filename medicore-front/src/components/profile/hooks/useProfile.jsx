import {useQuery} from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/auth/useAxiosPrivate.jsx";

export const useProfile = () => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["me"],
        queryFn: async () => {
            const res = await axiosPrivate.get("/profiles");
            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
    });
};