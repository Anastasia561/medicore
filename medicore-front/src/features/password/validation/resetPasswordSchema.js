import * as yup from "yup";

export const resetPasswordSchema = yup.object().shape({
    token: yup
        .string()
        .required("Reset token is required"),
    password: yup
        .string()
        .required('Password is required')
        .min(8, 'Password must be at least 8 characters')
        .matches(/\d/, 'Password must contain at least one digit')
        .matches(/[^a-zA-Z0-9]/, 'Password must contain at least one special character'),
    repeatPassword: yup
        .string()
        .required('Repeat password is required')
        .oneOf([yup.ref('password')], 'Passwords must match')
});
