import {useQuery} from "@tanstack/react-query";
import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";

export const useAvailableSlots = (doctorId, date) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["available-slots", doctorId, date],
        queryFn: async () => {
            const res = await axiosPrivate.get(`/doctors/${doctorId}/times`, {
                params: {date}
            });

            if (res.data?.error) throw new Error(res.data.error);
            return res.data?.data || [];
        },
        enabled: !!doctorId && !!date,
        retry: (failureCount, error) => {
            if (error?.response?.status === 400) {
                return false;
            }
            return failureCount < 2;
        }
    });
};