import * as yup from "yup";
import {useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import {useState} from "react";
import {useCreateSchedule} from "./hooks/useCreateSchedule.jsx";

const ConsultationForm = ({onSubmit, onCancel, doctorId}) => {

    const [generalError, setGeneralError] = useState('');

    const schema = yup.object().shape({
        day: yup.string().required("Please select a day"),
        startTime: yup.string().required("Start time is required"),
        endTime: yup.string()
            .required("End time is required")
            .test("is-after", "End time must be after start time", function (value) {
                const {startTime} = this.parent;
                return !startTime || !value || value > startTime;
            }),
    });

    const {
        register, handleSubmit, setError
        , formState: {errors}
    } = useForm({
        resolver: yupResolver(schema),
        defaultValues: {
            day: 'MONDAY',
            startTime: '08:00',
            endTime: '09:00'
        }
    });

    const {mutate} = useCreateSchedule(doctorId, {
        setGeneralError,
        setFormError: setError
    });

    const submitHandler = (data) => {
        mutate(data, {
            onSuccess: () => onSubmit()
        });
    };

    return (
        <form onSubmit={handleSubmit(submitHandler)} className="p-3">
            {generalError && (
                <div className="alert alert-danger py-2 text-center" role="alert">
                    {generalError}
                </div>
            )}

            <div className="mb-3">
                <label className="form-label">Day of the Week</label>
                <select
                    {...register("day")}
                    className={`form-select ${errors.day ? "is-invalid" : ""}`}
                >
                    <option value="MONDAY">Monday</option>
                    <option value="TUESDAY">Tuesday</option>
                    <option value="WEDNESDAY">Wednesday</option>
                    <option value="THURSDAY">Thursday</option>
                    <option value="FRIDAY">Friday</option>
                </select>
                {errors.day && <div className="invalid-feedback">{errors.day.message}</div>}
            </div>

            <div className="row">
                <div className="col-6 mb-3">
                    <label className="form-label">Start Time</label>
                    <input
                        type="time"
                        {...register("startTime")}
                        className={`form-control ${errors.startTime ? "is-invalid" : ""}`}
                    />
                    {errors.startTime && <div className="invalid-feedback">{errors.startTime.message}</div>}
                </div>

                <div className="col-6 mb-3">
                    <label className="form-label">End Time</label>
                    <input
                        type="time"
                        {...register("endTime")}
                        className={`form-control ${errors.endTime ? "is-invalid" : ""}`}
                    />
                    {errors.endTime && <div className="invalid-feedback">{errors.endTime.message}</div>}
                </div>
            </div>

            <div className="d-flex gap-2 justify-content-end mt-3">
                <button type="button" className="btn btn-secondary" onClick={onCancel}>Cancel</button>
                <button type="submit" className="btn btn-primary">Save Hours</button>
            </div>
        </form>
    );
};

export default ConsultationForm;