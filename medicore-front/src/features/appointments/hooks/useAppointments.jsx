import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {keepPreviousData, useQuery} from "@tanstack/react-query";

export const useAppointments = ({userId = null, startDate, endDate, status = ""}) => {

    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["appointments", userId, startDate, endDate, status],

        queryFn: async () => {
            const url = userId ? `/appointments/user/${userId}` : `/appointments`;

            const res = await axiosPrivate.get(url, {
                params: {
                    startDate,
                    endDate,
                    status: status !== "ALL" ? status : undefined
                }
            });

            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
        placeholderData: keepPreviousData
    });
};