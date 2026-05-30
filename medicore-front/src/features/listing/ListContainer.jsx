import '../styles/ListContainer.css';

const ListContainer = ({title, children, headerActions}) => {
    return (
        <div className="list_container my-5">
            <div className="white-box-card">
                <div className="d-flex justify-content-between align-items-center mb-4">
                    <h2 className="fw-bold">{title}</h2>
                    <div>{headerActions}</div>
                </div>
                {children}
            </div>
        </div>
    );
};

export default ListContainer;