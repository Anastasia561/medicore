import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import {useQuery} from "@tanstack/react-query";

export const useMedicalProfile = (patientId = null) => {
    const axiosPrivate = useAxiosPrivate();

    return useQuery({
        queryKey: ["medical-profile", patientId],
        queryFn: async () => {
            const url = patientId
                ? `/patients/${patientId}/medical-profile`
                : "/patients/medical-profile";
            const res = await axiosPrivate.get(url);
            if (res.data.error) throw new Error(res.data.error);
            return res.data.data;
        },
    });
};
