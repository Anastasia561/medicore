import useAxiosPrivate from "../../../hooks/auth/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useDoctors = (page = 0, size = 5, query = "", specialization = "") => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["doctors", page, size, query, specialization],
        queryFn: async () => {
            const res = await axiosPrivate.get("/doctors", {
                params: {page, size, query: query || undefined, specialization: specialization || undefined},
            });

            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
        keepPreviousData: true,
    });
};