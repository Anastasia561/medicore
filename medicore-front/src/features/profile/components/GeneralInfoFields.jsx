import Field from "./Field.jsx";
import IconField from "./IconField.jsx";
import {formatDateStandard} from "../../../utils/dateUtils.js";
import AddressField from "./AddressField.jsx";

const GeneralInfoFields = ({user}) => (
    <>
        <Field label="First Name" value={user.firstName}/>
        <Field label="Last Name" value={user.lastName}/>
        <Field label="Gender" value={user.gender}/>
        <IconField label="Birthdate" value={formatDateStandard(user.birthDate)}
                   iconClass="fas fa-calendar-alt text-primary"/>
        <IconField label="Email" value={user.email} iconClass="bi bi-envelope text-primary"/>
        <IconField label="Phone number" value={user.phoneNumber} iconClass="fas fa-phone text-success"/>
        <AddressField
            address={user.address} isEditing={false}
        />
    </>
);

export default GeneralInfoFields;