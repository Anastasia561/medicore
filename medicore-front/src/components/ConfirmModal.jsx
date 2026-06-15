import "./styles/ConfirmModal.css"

const ConfirmModal = ({title, message, confirmText, cancelText, onCancel, onConfirm}) => {
    return (
        <>
            <div
                className="modal-overlay"
                style={{zIndex: 1050}}
                onClick={onCancel}
            />
            <div
                className="confirm-modal"
                style={{
                    zIndex: 1060,
                    width: '90%',
                    maxWidth: '400px'
                }}
            >
                <h4 className="h5 text-dark fw-bold mb-2">{title}</h4>
                <p className="text-secondary small mb-4">{message}</p>

                <div className="d-flex justify-content-end gap-2">
                    <button
                        type="button"
                        className="btn btn-light btn-sm px-3 fw-medium"
                        onClick={onCancel}
                    >
                        {cancelText || "Cancel"}
                    </button>
                    <button
                        type="button"
                        className="btn btn-danger btn-sm px-3 fw-medium"
                        onClick={onConfirm}
                    >
                        {confirmText || "Confirm"}
                    </button>
                </div>
            </div>
        </>
    );
};

export default ConfirmModal;