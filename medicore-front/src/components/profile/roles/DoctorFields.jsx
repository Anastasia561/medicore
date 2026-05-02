import Field from "../comon/Field.jsx";
import IconField from "../comon/IconField.jsx";

const DoctorFields = ({user}) => (
    <>
        <Field label="First Name" value={user.firstName}/>
        <Field label="Last Name" value={user.lastName}/>
        <IconField label="Email" value={user.email} iconClass="bi bi-envelope text-primary"/>
        <IconField label="Employment Date" value={user.employmentDate} iconClass="bi bi-calendar-event text-info"/>
        <IconField label="Specialization" value={user.specialization} iconClass="fas fa-stethoscope text-primary"/>
    </>
);

export default DoctorFields;