import * as yup from 'yup';

export const registerSchema = yup.object().shape({
    firstName: yup
        .string()
        .required('First name is required')
        .max(20, 'First name must be at most 20 characters'),
    lastName: yup
        .string()
        .required('Last name is required')
        .max(20, 'Last name must be at most 20 characters'),
    birthDate: yup
        .date()
        .nullable()
        .typeError('Invalid date format')
        .required('Birth date is required')
        .max(new Date(), 'Birth date must be in the past')
        .test('min-age', 'You must be at least 18 years old', (value) => {
            if (!value) return false;
            const today = new Date();
            const cutoff = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());
            return value <= cutoff;
        }),

    password: yup
        .string()
        .required('Password is required')
        .min(8, 'Password must be at least 8 characters')
        .matches(/\d/, 'Password must contain at least one digit')
        .matches(/[^a-zA-Z0-9]/, 'Password must contain at least one special character'),
    repeatPassword: yup
        .string()
        .required('Repeat password is required')
        .oneOf([yup.ref('password')], 'Passwords must match'),

    phoneNumber: yup
        .string()
        .required('Phone number is required')
        .matches(/^\+?[0-9]{7,15}$/, 'Phone number must be valid and contain 7-15 digits'),
    email: yup
        .string()
        .required('Email is required')
        .email('Email should be valid'),

    address: yup.object().shape({
        country: yup
            .string()
            .required('Country name is required')
            .min(3, 'Country name must be between 3 and 30 characters')
            .max(30, 'Country name must be between 3 and 30 characters'),
        city: yup
            .string()
            .required('City name is required')
            .min(3, 'City name must be between 3 and 30 characters')
            .max(30, 'City name must be between 3 and 30 characters'),
        street: yup
            .string()
            .required('Street name is required')
            .min(3, 'Street name must be between 3 and 40 characters')
            .max(40, 'Street name must be between 3 and 40 characters'),
        number: yup
            .string()
            .required('House number is required')
            .matches(/^[1-9][0-9]*[a-zA-Z0-9/\- ]*$/, 'House number must start with a positive integer'),
    }),

    gender: yup
        .string()
        .required('Gender is required'),
    weight: yup
        .number()
        .nullable()
        .transform((value, originalValue) => (originalValue === '' ? null : value))
        .min(1.0, 'Weight must be greater than 0')
        .max(500.0, 'Weight must be less than 500'),
    height: yup
        .number()
        .nullable()
        .transform((value, originalValue) => (originalValue === '' ? null : value))
        .min(30.0, 'Height must be greater than 30 cm')
        .max(300.0, 'Height must be less than 300 cm'),
    pregnancyStatus: yup
        .string()
        .required('Pregnancy status is required')
        .test('male-pregnancy', 'Male patients must be marked as NOT_APPLICABLE', function (value) {
            const {gender} = this.parent;
            if (gender === 'MALE') return value === 'NOT_APPLICABLE';
            return true;
        }),
});
