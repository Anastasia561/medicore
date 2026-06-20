import {Route} from 'react-router-dom';
import PersistLogin from '../features/auth/components/PersistLogin.jsx';
import ProtectedLayout from '../layouts/ProtectedLayout';
import RequireAuth from '../features/auth/components/RequireAuth.jsx';

import Home from '../pages/Home';
import Profile from '../features/profile/Profile';
import DoctorList from '../features/listing/components/DoctorList';
import AppointmentListing from '../features/appointments/AppointmentListing.jsx'
import PatientList from '../features/listing/components/PatientList';
import DoctorSchedule from "../features/schedule/DoctorSchedule";
import MedicalRecord from "../features/record/components/MedicalRecord.jsx";
import RecordListing from "../features/record/RecordListing.jsx";
import MedicalRecordForm from "../features/record/components/MedicalRecordForm.jsx";

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
                <Route path="records/:id" element={<MedicalRecord/>}/>
                <Route path="records" element={<RecordListing/>}/>
            </Route>

            <Route element={<RequireAuth allowedRoles={["ROLE_DOCTOR"]}/>}>
                <Route path="schedule" element={<DoctorSchedule/>}/>
                <Route path="appointments/complete/:appId" element={<MedicalRecordForm/>}/>
            </Route>

        </Route>
    </Route>
);