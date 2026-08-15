import {useQuery} from '@tanstack/react-query';
import axios from '../../../../api/axios.js';

export const useVerifyEmail = (token) => {
    return useQuery({
        queryKey: ['verifyEmail', token],
        queryFn: async () => {
            const response = await axios.post('/patients/verify-email', {token});
            return response.data;
        },
        enabled: Boolean(token),
        retry: false,
        staleTime: Infinity,
    });
};