import {useContext} from "react";
import AuthProvider from "../context/AuthProvider.jsx";

const useAuth = () => {
    return useContext(AuthProvider);
}

export default useAuth;