import axios from "./axios";

export const loginRequest = async ({email, password}) => {
    const response = await axios.post("/auth/login",
        JSON.stringify({email, password}), {
            headers: {'Content-Type': 'application/json'},
            withCredentials: true
        });

    return response.data;
};