import {Routes, Route} from 'react-router-dom'
import Layout from "./components/layout/Layout.jsx";
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";
import Unauthorized from "./pages/Unauthorized.jsx";
import Missing from "./pages/Missing.jsx";
import Admin from "./pages/Admin.jsx";
import RequireAuth from "./components/RequireAuth.jsx";
import Doctors from "./pages/Doctors.jsx";
import PersistLogin from "./components/PersistLogin.jsx";
import ProtectedLayout from "./components/layout/ProtectedLayout.jsx";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";

function App() {

    const client = new QueryClient();

    return (
        <QueryClientProvider client={client}>
            <Routes>
                <Route path="/" element={<Layout/>}>
                    <Route index element={<Login/>}/>
                    <Route path="unauthorized" element={<Unauthorized/>}/>
                </Route>

                <Route path="/" element={<PersistLogin/>}>
                    <Route element={<ProtectedLayout/>}>

                        <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN", "ROLE_PATIENT", "ROLE_DOCTOR"]}/>}>
                            <Route path="home" element={<Home/>}/>
                        </Route>

                        <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN", "ROLE_DOCTOR"]}/>}>
                            <Route path="admin" element={<Admin/>}/>
                            <Route path="doctors" element={<Doctors/>}/>
                        </Route>

                    </Route>
                </Route>

                <Route path="*" element={<Missing/>}/>
            </Routes>
        </QueryClientProvider>
    )
}

export default App;
