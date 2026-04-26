import {Routes, Route} from 'react-router-dom'
import Layout from "./components/Layout.jsx";
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";
import Unauthorized from "./pages/Unauthorized.jsx";
import Missing from "./pages/Missing.jsx";
import Admin from "./pages/Admin.jsx";
import RequireAuth from "./components/RequireAuth.jsx";
import Doctors from "./pages/Doctors.jsx";
import PersistLogin from "./components/PersistLogin.jsx";

function App() {

    return (
        <Routes>
            <Route path="/" element={<Layout/>}>
                <Route path="login" element={<Login/>}/>
                <Route path="unauthorized" element={<Unauthorized/>}/>

                <Route path="/" element={<Home/>}/>

                <Route element={<PersistLogin/>}>
                    <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN"]}/>}>
                        <Route path="admin" element={<Admin/>}/>
                        <Route path="doctors" element={<Doctors/>}/>
                    </Route>
                </Route>

                <Route path="*" element={<Missing/>}/>
            </Route>
        </Routes>
    )
}

export default App;
