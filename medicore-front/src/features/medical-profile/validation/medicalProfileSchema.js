import * as yup from "yup";

export const medicalProfileSchema = yup.object({
    gender: yup.string().nullable(),
    weight: yup
        .number()
        .nullable()
        .transform((value, originalValue) => (originalValue === "" ? null : value))
        .min(1.0, "Weight must be greater than 0")
        .max(500.0, "Weight must be less than 500"),
    height: yup
        .number()
        .nullable()
        .transform((value, originalValue) => (originalValue === "" ? null : value))
        .min(30.0, "Height must be greater than 30 cm")
        .max(300.0, "Height must be less than 300 cm"),
    pregnancyStatus: yup
        .string()
        .required("Pregnancy status is required")
        .test("male-pregnancy", "Male patients must be marked as NOT_APPLICABLE", function (value) {
            const {gender} = this.parent;
            if (gender === "MALE") return value === "NOT_APPLICABLE";
            return true;
        }),
});
