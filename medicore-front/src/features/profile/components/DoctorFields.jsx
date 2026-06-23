import IconField from "./IconField.jsx";
import {formatDateStandard} from "../../../utils/dateUtils.js";
import GeneralInfoFields from "./GeneralInfoFields.jsx";

const DoctorFields = ({user}) => (
    <>
        <GeneralInfoFields user={user}/>
        <IconField label="Employment Date" value={formatDateStandard(user.employmentDate)}
                   iconClass="bi bi-calendar-event text-info"/>
        <IconField label="Specialization" value={user.specialization} iconClass="fas fa-stethoscope text-primary"/>
    </>
);

export default DoctorFields;