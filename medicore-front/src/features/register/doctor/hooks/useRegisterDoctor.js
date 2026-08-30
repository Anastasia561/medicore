import {useMutation} from '@tanstack/react-query';
import axios from '../../../../api/axios.js';

export const useRegisterDoctor = (setError, setGeneralError, onSuccess) => {
    return useMutation({
        mutationFn: async (doctorData) => {
            const res = await axios.post('/doctors/register', doctorData);
            return res.data;
        },
        onSuccess: (data, variables, context) => {
            if (setGeneralError) setGeneralError('');
            if (onSuccess) {
                onSuccess(data, variables, context);
            }
        },
        onError: (err) => {
            if (!err?.response) {
                setGeneralError('Server is not responding');
                return;
            }

            const {status, data} = err.response;

            if (status === 400) {
                const validationErrors = data?.error?.validationErrors;

                if (Array.isArray(validationErrors)) {
                    validationErrors.forEach((errObj) => {
                        setError(errObj.field, {message: errObj.message});
                    });
                    setGeneralError(data?.error?.message || 'Validation failed');
                } else {
                    setGeneralError(data?.error?.message || 'Invalid input');
                }
            } else {
                setGeneralError('Something went wrong');
            }
        },
    });
};
