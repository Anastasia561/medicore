import * as yup from "yup";

export const scheduleSchema = yup.object().shape({
    day: yup.string().required("Please select a day"),
    startTime: yup.string().required("Start time is required"),
    endTime: yup.string()
        .required("End time is required")
        .test("is-after", "End time must be after start time", function (value) {
            const {startTime} = this.parent;
            return !startTime || !value || value > startTime;
        }),
});
