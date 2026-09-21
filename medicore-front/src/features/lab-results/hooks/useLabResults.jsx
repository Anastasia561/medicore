import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useLabResults = () => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["lab-results"],
        queryFn: async () => {
            const res = await axiosPrivate.get("/tests");
            if (res.data.error) throw new Error(res.data.error);
            return res.data.data ?? [];
        },
    });
};
