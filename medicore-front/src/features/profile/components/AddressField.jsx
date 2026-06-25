import Field from "./Field.jsx";
import IconField from "./IconField.jsx";

const AddressField = ({address, isEditing, register, errors}) => {

    const formatAddress = (addr) => {
        if (!addr) return "No address provided";
        const {country, city, street, number} = addr;
        return `${street} ${number}, ${city}, ${country}`;
    };

    if (!isEditing) {
        return (
            <div className="full-width-field">
                <IconField
                    label="Address"
                    value={formatAddress(address)}
                    iconClass="fas fa-map-marker-alt"
                    isEditing={isEditing}
                />
            </div>
        );
    }

    return (
        <>
            <Field
                label="Country"
                isEditing={true}
                register={register("address.country")}
                error={errors?.address?.country}/>

            <Field
                label="City"
                isEditing={true}
                register={register("address.city")}
                error={errors?.address?.city}/>

            <Field
                label="Street"
                isEditing={true}
                register={register("address.street")}
                error={errors?.address?.street}/>

            <Field
                label="Number"
                isEditing={true}
                register={register("address.number")}
                error={errors?.address?.number}/>
        </>
    );
};

export default AddressField;