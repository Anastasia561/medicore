import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useAdminStatistics = () => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["admin-statistics"],
        queryFn: async () => {
            const res = await axiosPrivate.get("/statistics/admin");
            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
    });
};
