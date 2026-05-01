import {Outlet} from "react-router-dom";
import {useState, useEffect} from "react";
import useRefreshToken from "../hooks/useRefreshToken";
import useAuth from "../hooks/useAuth";

const PersistLogin = () => {
    const [isLoading, setIsLoading] = useState(true);
    const refresh = useRefreshToken();
    const {auth} = useAuth();

    useEffect(() => {
        let isMounted = true;

        const verify = async () => {
            try {
                await refresh();
            } catch (err) {
                console.error(err);
            } finally {
                if (isMounted) setIsLoading(false);
            }
        };

        if (!auth?.accessToken) {
            verify();
        } else {
            setIsLoading(false);
        }

        return () => {
            isMounted = false;
        };
    }, []);

    return isLoading ? <p>Loading...</p> : <Outlet/>;
};

export default PersistLogin;