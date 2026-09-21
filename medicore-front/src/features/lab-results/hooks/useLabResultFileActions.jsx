import useAxiosPrivate from "../../../hooks/useAxiosPrivate.jsx";
import toast from "react-hot-toast";
import {useNavigate} from "react-router-dom";

export const useLabResultFileActions = () => {
    const axiosPrivate = useAxiosPrivate();
    const navigate = useNavigate();

    const fetchUrl = async (path) => {
        try {
            const res = await axiosPrivate.get(path);
            if (res.data?.error) {
                throw new Error(res.data.error);
            }
            return res.data.data;
        } catch (err) {
            if (!err?.response) {
                toast.error("Server is not responding");
                throw err;
            }

            const {status} = err.response;
            if (status === 401) {
                navigate("/login");
            } else if (status === 403) {
                navigate("/unauthorized");
            } else if (status === 404) {
                toast.error("Lab result not found");
            } else {
                toast.error("Something went wrong");
            }
            throw err;
        }
    };

    const openView = async (id) => {
        const url = await fetchUrl(`/tests/view/${id}`);
        window.open(url, "_blank", "noopener,noreferrer");
    };

    const download = async (id) => {
        const url = await fetchUrl(`/tests/download/${id}`);
        window.open(url, "_blank", "noopener,noreferrer");
    };

    return {openView, download};
};
