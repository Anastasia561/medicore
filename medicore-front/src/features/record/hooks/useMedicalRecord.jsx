import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useMedicalRecord = (appId) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["record", appId],
        queryFn: async () => {
            const res = await axiosPrivate.get(`/records/appointment/${appId}`);
            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
    });
};