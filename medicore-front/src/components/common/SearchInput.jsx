import {useState, useEffect} from 'react';

const SearchInput = ({onSearch, placeholder = "Search...", delay = 500}) => {
    const [searchTerm, setSearchTerm] = useState("");
    const [isFirstRender, setIsFirstRender] = useState(true);

    useEffect(() => {
        if (isFirstRender) {
            setIsFirstRender(false);
            return;
        }
        const handler = setTimeout(() => {
            onSearch(searchTerm);
        }, delay);
        return () => clearTimeout(handler);
    }, [searchTerm]);

    return (
        <div className="input-group" style={{maxWidth: '300px'}}>
            <span className="input-group-text bg-white border-end-0">
                <i className="bi bi-search text-muted"></i>
            </span>
            <input
                type="text"
                className="form-control border-start-0"
                placeholder={placeholder}
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
            />
        </div>
    );
};

export default SearchInput;