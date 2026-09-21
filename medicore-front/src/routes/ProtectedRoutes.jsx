import {Route} from 'react-router-dom';
import PersistLogin from '../features/auth/components/PersistLogin.jsx';
import ProtectedLayout from '../layouts/ProtectedLayout';
import RequireAuth from '../features/auth/components/RequireAuth.jsx';

import Profile from '../features/profile/Profile';
import DoctorList from '../features/listing/components/DoctorList';
import AppointmentListing from '../features/appointments/AppointmentListing.jsx'
import PatientList from '../features/listing/components/PatientList';
import DoctorSchedule from "../features/schedule/DoctorSchedule";
import MedicalRecord from "../features/record/components/MedicalRecord.jsx";
import RecordListing from "../features/record/RecordListing.jsx";
import MedicalRecordForm from "../features/record/components/MedicalRecordForm.jsx";
import AppointmentBookingForm from "../features/appointments/AppointmentBookingForm.jsx";
import StatisticsDashboard from "../features/statistics/StatisticsDashboard.jsx";
import DoctorInviteForm from "../features/register/doctor/DoctorInviteForm.jsx";
import LabResults from "../features/lab-results/LabResults.jsx";

export const ProtectedRoutes = (
    <Route path="/" element={<PersistLogin/>}>
        <Route element={<ProtectedLayout/>}>

            <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN", "ROLE_PATIENT", "ROLE_DOCTOR"]}/>}>
                <Route path="profile" element={<Profile/>}/>
                <Route path="doctors/:doctorId/schedule" element={<DoctorSchedule/>}/>
                <Route path="appointments/:userId" element={<AppointmentListing/>}/>
            </Route>

            <Route element={<RequireAuth allowedRoles={["ROLE_ADMIN"]}/>}>
                <Route path="statistics" element={<StatisticsDashboard/>}/>
                <Route path="/doctors/register" element={<DoctorInviteForm/>}/>
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
                <Route path="patients/:patientId/lab-results" element={<LabResults/>}/>
            </Route>


            <Route element={<RequireAuth allowedRoles={["ROLE_PATIENT"]}/>}>
                <Route path="appointments/book/:doctorId" element={<AppointmentBookingForm/>}/>
                <Route path="lab-results" element={<LabResults/>}/>
            </Route>

        </Route>
    </Route>
);