import Field from "../common/Field.jsx";
import IconField from "../common/IconField.jsx";

const AdminFields = ({user}) => (
    <>
        <Field label="First Name" value={user.firstName}/>
        <Field label="Last Name" value={user.lastName}/>
        <IconField label="Email" value={user.email} iconClass="bi bi-envelope text-primary"/>
    </>
);

export default AdminFields;