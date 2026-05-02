const Field = ({label, value}) => (
    <div className="field">
        <label>{label}</label>
        <input value={value} disabled/>
    </div>
);

export default Field;