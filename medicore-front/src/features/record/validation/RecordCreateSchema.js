import * as yup from "yup";

export const recordCreateSchema = yup.object().shape({
    diagnosis: yup
        .string()
        .required("Diagnosis is required")
        .min(3, "Diagnosis must be between 3 and 100 characters")
        .max(100, "Diagnosis must be between 3 and 100 characters"),
    summary: yup
        .string()
        .required("Summary is required")
        .min(10, "Summary must be between 10 and 255 characters")
        .max(255, "Summary must be between 10 and 255 characters"),
    prescriptions: yup.array().of(
        yup.object().shape({
            medicine: yup
                .string()
                .required("Medicine name is required")
                .min(3, "Summary must be between 3 and 60 characters")
                .max(60, "Summary must be between 3 and 60 characters"),
            dosage: yup
                .string()
                .required("Dosage description is required")
                .min(3, "Summary must be between 3 and 20 characters")
                .max(20, "Summary must be between 3 and 20 characters"),
            frequency: yup
                .string()
                .required("Frequency is required")
                .min(3, "Summary must be between 3 and 50 characters")
                .max(50, "Summary must be between 3 and 50 characters"),
            startDate: yup
                .string()
                .required("Start date is required"),
            endDate: yup
                .string()
                .nullable()
                .transform((value) => (value === "" ? null : value))
        })
    )
});
