const Pagination = ({currentPage, totalPages, onPageChange}) => {
    const pages = Array.from({length: totalPages}, (_, i) => i + 1);

    if (totalPages <= 1) return null;

    return (
        <nav className="mt-4 d-flex justify-content-center">
            <ul className="pagination shadow-sm">
                <li className={`page-item ${currentPage === 1 ? 'disabled' : ''}`}>
                    <button className="page-link" onClick={() => onPageChange(currentPage - 1)}>
                        Previous
                    </button>
                </li>

                {pages.map(page => (
                    <li key={page} className={`page-item ${currentPage === page ? 'active' : ''}`}>
                        <button className="page-link" onClick={() => onPageChange(page)}>
                            {page}
                        </button>
                    </li>
                ))}

                <li className={`page-item ${currentPage === totalPages ? 'disabled' : ''}`}>
                    <button className="page-link" onClick={() => onPageChange(currentPage + 1)}>
                        Next
                    </button>
                </li>
            </ul>
        </nav>
    );
};

export default Pagination;