import {useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import {useState} from "react";
import {useCreateSchedule} from "../hooks/useCreateSchedule.jsx";
import {useUpdateSchedule} from "../hooks/useUpdateSchedule.jsx";
import {scheduleSchema} from "../validation/ScheduleSchema.js";

const ConsultationForm = ({onSubmit, onCancel, doctorId, initialData}) => {

    const isUpdate = !!initialData;
    const [generalError, setGeneralError] = useState('');

    const {
        register, handleSubmit, setError
        , formState: {errors}
    } = useForm({
        resolver: yupResolver(scheduleSchema),
        defaultValues: initialData || {
            day: 'MONDAY',
            startTime: '08:00',
            endTime: '09:00'
        }
    });

    const {mutate: createSchedule} = useCreateSchedule(doctorId, {setGeneralError, setError});
    const {mutate: updateSchedule} = useUpdateSchedule(doctorId, {setGeneralError, setError});

    const submitHandler = (data) => {
        if (isUpdate) {
            updateSchedule({
                id: initialData.id,
                startTime: data.startTime,
                endTime: data.endTime
            }, {
                onSuccess: () => onSubmit()
            });
        } else {
            createSchedule(data, {
                onSuccess: () => onSubmit()
            });
        }
    };

    return (
        <form onSubmit={handleSubmit(submitHandler)} className="p-3">
            {generalError && (
                <div className="alert alert-danger py-2 text-center" role="alert">
                    {generalError}
                </div>
            )}

            <div className="mb-3">
                {isUpdate ? (
                    <div>
                        <input
                            type="text"
                            readOnly
                            className="form-control-plaintext fw-bold"
                            {...register("day")}
                        />
                    </div>
                ) : (
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
                )}
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
                <button type="submit" className="btn btn-primary">
                    {initialData?.id ? "Update Hours" : "Save Hours"}
                </button>
            </div>
        </form>
    );
};

export default ConsultationForm;