import IconField from "./IconField.jsx";
import {formatDateStandard} from "../../../utils/dateUtils.js";
import GeneralInfoFields from "./GeneralInfoFields.jsx";

const DoctorFields = ({user, isEditing, register, errors}) => (
    <>
        <GeneralInfoFields
            user={user}
            isEditing={isEditing}
            register={register}
            errors={errors}/>

        <IconField
            label="Employment Date"
            value={formatDateStandard(user.employmentDate)}
            isEditing={false}
            iconClass="bi bi-calendar-event text-info"
            className={isEditing ? "permanently-disabled" : ""}/>

        <IconField
            label="Specialization"
            value={user.specialization}
            isEditing={false}
            iconClass="fas fa-stethoscope text-primary"
            className={isEditing ? "permanently-disabled" : ""}/>
    </>
);

export default DoctorFields;