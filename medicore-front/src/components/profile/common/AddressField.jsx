import Field from "./Field.jsx";
import IconField from "./IconField.jsx";

const AddressField = ({address, isEditing, onChange}) => {

    const formatAddress = (address) => {
        if (!address) return "";

        const {country, city, street, number} = address;
        return `${street} ${number}, ${city}, ${country}`;
    };

    if (!isEditing) {
        return (
            <IconField label="Address" value={formatAddress(address)} iconClass="fas fa-map-marker-alt"/>
        );
    }

    return (
        <>
            <Field
                label="Country"
                value={address.country}
                field="country"
                onChange={(f, v) => onChange(f, v)}
                isEditing={true}
            />

            <Field
                label="City"
                value={address.city}
                field="city"
                onChange={(f, v) => onChange(f, v)}
                isEditing={true}
            />

            <Field
                label="Street"
                value={address.street}
                field="street"
                onChange={(f, v) => onChange(f, v)}
                isEditing={true}
            />

            <Field
                label="Number"
                value={address.number}
                field="number"
                onChange={(f, v) => onChange(f, v)}
                isEditing={true}
            />
        </>
    );
};

export default AddressField;