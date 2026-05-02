import axios from "../../api/axios.js";
import useAuth from "./useAuth.jsx";

const useLogout = () => {
    const {setAuth} = useAuth();

    return async () => {
        try {
            await axios.post("/auth/logout", {}, {
                withCredentials: true
            });

        } catch (err) {
            console.error(err);
        } finally {
            setAuth(null);
        }
    };
};

export default useLogout;