import axios from "../../api/axios.js"
import useAuth from "./useAuth.jsx";
import {jwtDecode} from "jwt-decode";

const useRefreshToken = () => {
    const {setAuth} = useAuth();

    return async () => {
        const response = await axios.post('/auth/refresh', {}, {
            withCredentials: true
        });

        setAuth(prev => {
            const decoded = jwtDecode(response.data.accessToken);

            return {...prev, accessToken: response.data.accessToken, role: decoded.role};
        });
        return response.data.accessToken;
    }
}

export default useRefreshToken;