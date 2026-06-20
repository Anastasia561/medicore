import {useParams, useNavigate} from "react-router-dom";
import {useForm, useWatch} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import {appointmentSchema} from "./validation/AppointmentSchema.js";
import {useAvailableSlots} from "./hooks/useAvailableSlots";
import {useCreateAppointment} from "./hooks/useCreateAppointment";
import {useState} from "react";

const AppointmentBookingForm = () => {
    const {doctorId} = useParams();
    const navigate = useNavigate();
    const {mutateAsync: createAppointment} = useCreateAppointment();
    const [serverError, setServerError] = useState("");

    const {
        register,
        handleSubmit,
        control,
        setValue,
        formState: {errors, isSubmitting}
    } = useForm({
        resolver: yupResolver(appointmentSchema),
        mode: "onChange",
        defaultValues: {
            date: "",
            startTime: ""
        }
    });

    const selectedDate = useWatch({
        control,
        name: "date"
    });

    const {
        data: availableSlots = [],
        isLoading: isLoadingSlots,
        isError: isSlotError,
        error: slotError
    } = useAvailableSlots(doctorId, selectedDate);

    const onSubmitForm = async (data) => {
        const payload = {
            doctorId,
            date: data.date,
            startTime: data.startTime
        };

        try {
            await createAppointment(payload);
            navigate("/appointments", {replace: true});
        } catch (err) {
            setServerError(err?.response?.data?.message || "Failed to create medical record.");
        }
    };

    const todayStr = new Date().toISOString().split("T")[0];

    return (
        <div className="container p-4 d-flex justify-content-center">
            <div className="card shadow-sm border-0 rounded-3 p-4 bg-white w-100" style={{maxWidth: "600px"}}>

                <h4>Book an Appointment</h4>

                <form onSubmit={handleSubmit(onSubmitForm)}>

                    {serverError && (
                        <div className="alert alert-danger mb-4" role="alert">{serverError}</div>
                    )}

                    <div className="mb-4">
                        <label className="form-label fw-bold small text-dark">Step 1: Choose a Date</label>
                        <input
                            type="date"
                            min={todayStr}
                            className={`form-control form-control-lg ${errors.date ? "is-invalid" : ""}`}
                            {...register("date", {
                                onChange: () => setValue("startTime", "")
                            })}
                        />
                        <div className="invalid-feedback">{errors.date?.message}</div>
                    </div>

                    {selectedDate && !errors.date && (
                        <div className="mb-4">
                            <label className="form-label fw-bold small text-dark mb-2">
                                Step 2: Select an Available Time Slot
                            </label>

                            {isLoadingSlots ? (
                                <div className="d-flex align-items-center gap-2 text-muted small ps-1">
                                    <div className="spinner-border spinner-border-sm text-primary" role="status"/>
                                    Checking available schedules...
                                </div>
                            ) : isSlotError ? (
                                <div className="alert alert-danger small p-2 mb-0" role="alert">
                                    {slotError?.response?.data?.error?.message || "Failed to load available slots"}
                                </div>
                            ) : availableSlots.length === 0 ? (
                                <div className="alert alert-warning small p-2 mb-0">
                                    No available slots found for this day. Please try another date.
                                </div>
                            ) : (
                                <div className="row g-2">
                                    {availableSlots.map((slot) => (
                                        <div key={slot} className="col-4 col-sm-3">
                                            <input
                                                type="radio"
                                                className="btn-check"
                                                id={`slot-${slot}`}
                                                value={slot}
                                                {...register("startTime")}
                                            />
                                            <label
                                                className={`btn btn-outline-primary w-100 py-2 small fw-medium ${errors.startTime ? "border-danger" : ""}`}
                                                htmlFor={`slot-${slot}`}
                                            >
                                                {slot.substring(0, 5)}
                                            </label>
                                        </div>
                                    ))}
                                </div>
                            )}

                            {errors.startTime && (
                                <div className="text-danger small mt-2 d-block">{errors.startTime?.message}</div>
                            )}
                        </div>
                    )}

                    <div className="d-flex justify-content-end gap-2 mt-4 pt-2 border-top">
                        <button type="button" className="btn btn-outline-secondary px-4"
                                onClick={() => navigate(-1)}>
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="btn btn-success px-4 fw-semibold"
                            disabled={isSubmitting || !selectedDate || availableSlots.length === 0}
                        >
                            {isSubmitting ? "Confirming..." : "Confirm Appointment"}
                        </button>
                    </div>
                </form>

            </div>
        </div>
    );
};

export default AppointmentBookingForm;