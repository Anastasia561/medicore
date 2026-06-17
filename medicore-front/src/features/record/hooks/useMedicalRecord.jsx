import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useMedicalRecord = (id) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["record", id],
        queryFn: async () => {
            const res = await axiosPrivate.get(`/records/${id}`);
            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
    });
};