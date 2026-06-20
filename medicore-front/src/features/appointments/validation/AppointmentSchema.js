import * as yup from "yup";

export const appointmentSchema = yup.object().shape({
    date: yup
        .string()
        .required("Please select an appointment date")
        .test("is-future-or-present", "Date must be today or in the future", (value) => {
            if (!value) return false;
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            return new Date(value) >= today;
        }),
    startTime: yup
        .string()
        .required("Please select a time slot")
});