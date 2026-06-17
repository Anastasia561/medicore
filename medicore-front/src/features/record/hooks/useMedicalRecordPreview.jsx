import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useMedicalRecordPreview = ({page, size, startDate, endDate, searchTerm}) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["records-preview", page, size, startDate, endDate, searchTerm],
        queryFn: async () => {
            const params = {
                page,
                size,
                ...(startDate && {startDate}),
                ...(endDate && {endDate}),
                ...(searchTerm && {email: searchTerm})
            };

            const res = await axiosPrivate.get("/records", {params});

            if (res.data?.error) throw new Error(res.data.error.message);

            return res.data.data;
        }
    });
};