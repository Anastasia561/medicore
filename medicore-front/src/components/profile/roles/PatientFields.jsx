import Field from "../common/Field.jsx";
import IconField from "../common/IconField.jsx";
import AddressField from "../common/AddressField.jsx";

const PatientFields = ({user}) => (
    <>
        <Field label="First Name" value={user.firstName}/>
        <Field label="Last Name" value={user.lastName}/>
        <IconField label="Email" value={user.email} iconClass="bi bi-envelope text-primary"/>
        <IconField label="Phone number" value={user.phoneNumber} iconClass="fas fa-phone text-success"/>
        <IconField label="Birthdate" value={user.birthDate} iconClass="fas fa-calendar-alt text-primary"/>
        <AddressField
            address={user.address} isEditing={false}
        />
    </>
);

export default PatientFields;