import './ListContainer.css';

const ListContainer = ({title, children, headerActions}) => {
    return (
        <div className="list_container my-5">
            <div className="white-box-card">
                <div className="d-flex flex-column flex-sm-row justify-content-between align-items-sm-center gap-3 mb-4">
                    <h2 className="mb-0 h3">{title}</h2>
                    <div className="d-flex flex-wrap gap-2">{headerActions}</div>
                </div>
                {children}
            </div>
        </div>
    );
};

export default ListContainer;