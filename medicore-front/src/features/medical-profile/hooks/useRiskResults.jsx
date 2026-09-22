import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useRiskResults = (patientId) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["risks", patientId],
        enabled: Boolean(patientId),
        queryFn: async () => {
            const res = await axiosPrivate.get(`/risks/${patientId}`);
            if (res.data?.error) throw new Error(res.data.error);
            return res.data?.data ?? [];
        },
    });
};
