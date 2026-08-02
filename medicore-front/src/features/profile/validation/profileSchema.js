import * as yup from "yup";

export const profileSchema = yup.object().shape({
    firstName: yup
        .string()
        .required("First name is required")
        .min(2, "First name must be between 2 and 20 characters")
        .max(20, "First name must be between 2 and 20 characters"),
    lastName: yup
        .string()
        .required("Last name is required")
        .min(2, "Last name must be between 2 and 20 characters")
        .max(20, "Last name must be between 2 and 20 characters"),
    gender: yup
        .string()
        .required("Gender is required"),
    phoneNumber: yup
        .string()
        .matches(/^\+?[0-9]{7,15}$/, "Invalid phone number")
        .required("Phone number is required"),
    address: yup.object().shape({
        country: yup
            .string()
            .required("Country is required")
            .min(3, "Country name must be between 3 and 30 characters")
            .max(30, "Country name must be between 3 and 30 characters"),
        city: yup
            .string()
            .required("City is required")
            .min(3, "City name must be between 3 and 30 characters")
            .max(30, "City name must be between 3 and 30 characters"),
        street: yup
            .string()
            .required("Street is required")
            .min(3, "Street name must be between 3 and 40 characters")
            .max(40, "Street name must be between 3 and 40 characters"),
        number: yup
            .string()
            .required("Number is required")
            .matches(/^[1-9][0-9]*[a-zA-Z0-9/\- ]*$/, "Must start with a positive number"),
    }),
});