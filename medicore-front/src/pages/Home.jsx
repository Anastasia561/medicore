import {Link} from "react-router-dom";
import useLogout from "../hooks/useLogout";

const Home = () => {
    const logout = useLogout();

    return (
        <div>
            <h1>Home page</h1>

            <nav>
                <h3>Public</h3>
                <Link to="/login">Login</Link>
                <br/>
                <h3>Private</h3>
                <Link to="/admin">Admin</Link> <br/>
                <Link to="/doctors">Doctors</Link>
                <br/>
                <button onClick={() => logout()}>Log out</button>
            </nav>
        </div>
    );
};

export default Home;