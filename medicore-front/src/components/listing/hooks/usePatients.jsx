import useAxiosPrivate from "../../../hooks/auth/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const usePatients = (page = 0, size = 5, search = "") => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["doctors", page, size, search],
        queryFn: async () => {
            const res = await axiosPrivate.get("/patients", {
                params: {page, size, search: search || undefined},
            });

            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
        keepPreviousData: true,
    });
};