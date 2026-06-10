import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useAppointments = ({ userId, startDate, endDate, status = "" }) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["appointments", userId, startDate, endDate, status],
        queryFn: async () => {
            const res = await axiosPrivate.get(`/appointments/${userId}`, {
                params: {
                    startDate,
                    endDate,
                    status: status !== "ALL" && status ? status : undefined
                },
            });

            if (res.data.error) throw new Error(res.data.error);

            return res.data.data;
        },
        keepPreviousData: true,
    });
};