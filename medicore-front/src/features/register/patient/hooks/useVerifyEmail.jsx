import {useMutation} from '@tanstack/react-query';
import {useNavigate} from 'react-router-dom';
import axios from '../../../../api/axios.js';

export const useVerifyEmail = () => {
    const navigate = useNavigate();

    return useMutation({
        mutationFn: async (token) => {
            const response = await axios.post('/patients/verify-email', {token});
            return response.data;
        },
        onSuccess: () => {
            setTimeout(() => navigate('/login'), 1500);
        },
    });
};
