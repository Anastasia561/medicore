import {Routes, Route} from 'react-router-dom'
import Layout from "./layouts/Layout.jsx";
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";
import Unauthorized from "./pages/Unauthorized.jsx";
import Missing from "./pages/Missing.jsx";
import RequireAuth from "./features/auth/RequireAuth.jsx";
import PersistLogin from "./features/auth/PersistLogin.jsx";
import ProtectedLayout from "./layouts/ProtectedLayout.jsx";
import Profile from "./features/profile/Profile.jsx";
import PatientList from "./features/listing/components/PatientList.jsx";
import DoctorList from "./features/listing/components/DoctorList.jsx";
import DoctorSchedule from "./features/schedule/DoctorSchedule.jsx";
import Providers from "./context/Providers.jsx";

function App() {

    return (
        <Providers>
            <Routes>
                <Route path="/" element={<Layout/>}>
                    <Route index element={<Login/>}/>
                    <Route path="unauthorized" element={<Unauthorized/>}/>
                </Route>

                <Route path="/" element={<PersistLogin/>}>
                    <Route element={<ProtectedLayout/>}>

                        <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN", "ROLE_PATIENT", "ROLE_DOCTOR"]}/>}>
                            <Route path="home" element={<Home/>}/>
                            <Route path="profile" element={<Profile/>}/>
                            <Route path="/doctors/:doctorId/schedule" element={<DoctorSchedule/>}/>
                        </Route>

                        <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN", "ROLE_PATIENT"]}/>}>
                            <Route path="doctors" element={<DoctorList/>}/>
                        </Route>

                        <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN", "ROLE_DOCTOR"]}/>}>
                            <Route path="patients" element={<PatientList/>}/>
                        </Route>

                    </Route>
                </Route>

                <Route path="*" element={<Missing/>}/>
            </Routes>
        </Providers>
    )
}

export default App;
