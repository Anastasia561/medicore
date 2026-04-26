import axios from "../api/axios.js"
import useAuth from "./useAuth";
import {jwtDecode} from "jwt-decode";

const useRefreshToken = () => {
    const {setAuth} = useAuth();

    return async () => {
        const response = await axios.post('/auth/refresh', {}, {
            withCredentials: true
        });

        setAuth(prev => {
            console.log(JSON.stringify(prev));
            console.log(response.data.accessToken);
            const decoded = jwtDecode(response.data.accessToken);

            return {...prev, accessToken: response.data.accessToken, role: decoded.role};
        });
        return response.data.accessToken;
    }
}

export default useRefreshToken;