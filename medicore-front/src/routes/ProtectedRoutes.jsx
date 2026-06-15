import {Route} from 'react-router-dom';
import PersistLogin from '../features/auth/PersistLogin';
import ProtectedLayout from '../layouts/ProtectedLayout';
import RequireAuth from '../features/auth/RequireAuth';

import Home from '../pages/Home';
import Profile from '../features/profile/Profile';
import DoctorList from '../features/listing/components/DoctorList';
import AppointmentListing from '../features/appointments/components/AppointmentListing'
import PatientList from '../features/listing/components/PatientList';
import DoctorSchedule from "../features/schedule/DoctorSchedule";

export const ProtectedRoutes = (
    <Route path="/" element={<PersistLogin/>}>
        <Route element={<ProtectedLayout/>}>

            <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN", "ROLE_PATIENT", "ROLE_DOCTOR"]}/>}>
                <Route path="home" element={<Home/>}/>
                <Route path="profile" element={<Profile/>}/>
                <Route path="doctors/:doctorId/schedule" element={<DoctorSchedule/>}/>
                <Route path="appointments/:userId" element={<AppointmentListing/>}/>
            </Route>

            <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN", "ROLE_PATIENT"]}/>}>
                <Route path="doctors" element={<DoctorList/>}/>
            </Route>

            <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN", "ROLE_DOCTOR"]}/>}>
                <Route path="patients" element={<PatientList/>}/>
            </Route>

            <Route element={<RequireAuth allowedRoles={["ROLE_PATIENT", "ROLE_DOCTOR"]}/>}>
                <Route path="appointments" element={<AppointmentListing/>}/>
            </Route>

        </Route>
    </Route>
);