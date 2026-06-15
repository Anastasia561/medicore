import {useQuery} from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";

export const useSchedule = (doctorId) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["doctor-schedule", doctorId],
        queryFn: async () => {
            const res = await axiosPrivate.get(`/consultations/doctor/${doctorId}`);
            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
        staleTime: 1000 * 60 * 5
    });
};