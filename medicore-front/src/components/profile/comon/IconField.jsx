const IconField = ({label, value, iconClass}) => (
    <div className="field">
        <label>{label}</label>
        <div className="input-group">
      <span className="input-group-text bg-white">
        <i className={iconClass}></i>
      </span>
            <input className="form-control bg-white" value={value} disabled/>
        </div>
    </div>
);

export default IconField;