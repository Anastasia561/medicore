import {useQuery} from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";

export const useSchedule = (doctorId) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["doctor-schedule", doctorId || "mine"],
        queryFn: async () => {
            const url = doctorId
                ? `/consultations/doctor/${doctorId}`
                : "/consultations";

            const res = await axiosPrivate.get(url);
            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
        staleTime: 1000 * 60 * 5
    });
};