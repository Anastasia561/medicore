import {Route} from 'react-router-dom';
import Layout from '../layouts/Layout';
import Login from '../features/auth/Login.jsx';
import PublicHome from '../pages/PublicHome.jsx';
import PatientRegisterForm from '../features/register/patient/PatientRegisterForm.jsx';
import VerifyEmailPage from '../features/register/patient/VerifyEmailPage.jsx';
import Unauthorized from '../pages/Unauthorized';
import RequestResetForm from '../features/password/RequestResetForm.jsx';
import ResetPasswordForm from '../features/password/ResetPasswordForm.jsx';

export const PublicRoutes = (
    <Route path="/" element={<Layout/>}>
        <Route index element={<PublicHome/>}/>
        <Route path="login" element={<Login/>}/>
        <Route path="register" element={<PatientRegisterForm/>}/>
        <Route path="verify-email" element={<VerifyEmailPage/>}/>
        <Route path="reset-password/request" element={<RequestResetForm/>}/>
        <Route path="reset-password" element={<ResetPasswordForm/>}/>
        <Route path="unauthorized" element={<Unauthorized/>}/>
    </Route>
);
