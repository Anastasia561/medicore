import {useMutation} from '@tanstack/react-query';
import useAxiosPrivate from "../../../../hooks/useAxiosPrivate.jsx";

export const useInviteDoctor = (setError, setGeneralError, onSuccess) => {
    const axiosPrivate = useAxiosPrivate();
    return useMutation({
        mutationFn: async (inviteData) => {
            const res = await axiosPrivate.post('/doctors/invite', inviteData);
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
