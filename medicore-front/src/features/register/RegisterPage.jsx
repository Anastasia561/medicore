import {useSearchParams} from 'react-router-dom';
import PatientRegisterForm from './patient/PatientRegisterForm.jsx';
import DoctorRegisterForm from './doctor/DoctorRegisterForm.jsx';

const RegisterPage = () => {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token')?.replace(/\s/g, '+');

    if (searchParams.has('token')) {
        return <DoctorRegisterForm inviteToken={token || ''}/>;
    }

    return <PatientRegisterForm/>;
};

export default RegisterPage;
