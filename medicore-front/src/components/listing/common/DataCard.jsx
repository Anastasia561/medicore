const DataCard = ({name, details, renderActions}) => {
    return (
        <div className="col">
            <div className="card shadow-sm">
                <div className="card-body">
                    <div className="row">
                        <div className="col-md-8">
                            <h3 className="card-title">{name}</h3>
                            {details.map((detail, index) => (
                                <p
                                    key={index}
                                    className={`card-text ${index === details.length - 1 ? 'mb-0' : 'mb-1'}`}
                                >
                                    <strong>{detail.label}:</strong> {detail.value}
                                </p>
                            ))}
                        </div>

                        <div className="col-md-4 d-flex flex-column align-items-end justify-content-center gap-2">
                            {renderActions && renderActions()}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default DataCard;