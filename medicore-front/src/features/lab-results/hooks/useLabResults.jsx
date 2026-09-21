import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useLabResults = (patientId = null) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["lab-results", patientId],
        queryFn: async () => {
            const url = patientId ? `/tests/patient/${patientId}` : "/tests";
            const res = await axiosPrivate.get(url);
            if (res.data.error) throw new Error(res.data.error);
            return res.data.data ?? [];
        },
    });
};
