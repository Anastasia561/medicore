import axios from "./axios";

export const loginRequest = async ({email, password}) => {
    const response = await axios.post("/auth/login",
        {email, password},
        {withCredentials: true}
    );

    return response.data;
};