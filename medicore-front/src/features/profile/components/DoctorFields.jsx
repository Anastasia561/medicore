import Field from "./Field.jsx";
import IconField from "./IconField.jsx";
import {formatDateStandard} from "../../../utils/dateUtils.js";

const DoctorFields = ({user}) => (
    <>
        <Field label="First Name" value={user.firstName}/>
        <Field label="Last Name" value={user.lastName}/>
        <IconField label="Email" value={user.email} iconClass="bi bi-envelope text-primary"/>
        <IconField label="Employment Date" value={formatDateStandard(user.employmentDate)} iconClass="bi bi-calendar-event text-info"/>
        <IconField label="Specialization" value={user.specialization} iconClass="fas fa-stethoscope text-primary"/>
    </>
);

export default DoctorFields;