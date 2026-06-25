import Field from "./Field.jsx";
import IconField from "./IconField.jsx";
import AddressField from "./AddressField.jsx";
import {formatDateStandard} from "../../../utils/dateUtils.js";
import SelectField from "./SelectField.jsx";

const genderOptions = [
    {value: "MALE", label: "Male"},
    {value: "FEMALE", label: "Female"},
    {value: "OTHER", label: "Other"}
];

const GeneralInfoFields = ({user, isEditing, register, errors}) => (
    <>
        <Field
            label="First Name"
            isEditing={isEditing}
            register={register("firstName")}
            error={errors.firstName}/>

        <Field
            label="Last Name"
            isEditing={isEditing}
            register={register("lastName")}
            error={errors.lastName}/>

        <SelectField
            label="Gender"
            isEditing={isEditing}
            register={register("gender")}
            error={errors.gender}
            options={genderOptions}
        />
        <IconField
            label="Birthdate"
            isEditing={false}
            className={isEditing ? "permanently-disabled" : ""}
            value={formatDateStandard(user?.birthDate)}
            iconClass="fas fa-calendar-alt text-primary"
        />

        <IconField
            label="Email"
            isEditing={false}
            className={isEditing ? "permanently-disabled" : ""}
            value={user?.email}
            iconClass="bi bi-envelope text-primary"
        />

        <IconField
            label="Phone number"
            isEditing={isEditing}
            register={register("phoneNumber")}
            value={user?.phoneNumber}
            error={errors.phoneNumber}
            iconClass="fas fa-phone text-success"
        />

        <AddressField address={user?.address} isEditing={isEditing} register={register} errors={errors}/>
    </>
);

export default GeneralInfoFields;