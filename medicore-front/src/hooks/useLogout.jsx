import axios from "../api/axios.js";
import useAuth from "./useAuth";

const useLogout = () => {
    const {setAuth} = useAuth();

    return async () => {
        try {
            await axios.post("/auth/logout", {}, {
                withCredentials: true
            });
            setAuth({});
        } catch (err) {
            console.error(err)
        }
    };
}

export default useLogout;