import useAxiosPrivate from "../../../hooks/auth/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useDoctors = (page = 0, size = 5, query="") => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["doctors", page, size, query],
        queryFn: async () => {
            const res = await axiosPrivate.get("/doctors", {
                params: {page, size, query : query || undefined},
            });

            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
        keepPreviousData: true,
    });
};