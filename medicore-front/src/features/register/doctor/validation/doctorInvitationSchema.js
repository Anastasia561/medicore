import * as yup from 'yup';

export const doctorInvitationSchema = yup.object().shape({
    firstName: yup
        .string()
        .required('First name is required')
        .max(20, 'First name must be at most 20 characters'),
    lastName: yup
        .string()
        .required('Last name is required')
        .max(20, 'Last name must be at most 20 characters'),
    email: yup
        .string()
        .required('Email is required')
        .email('Email should be valid'),
});
