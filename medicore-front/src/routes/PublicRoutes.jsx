import {Route} from 'react-router-dom';
import Layout from '../layouts/Layout';
import Login from '../features/auth/Login.jsx';
import PublicHome from '../pages/PublicHome.jsx';
import Register from '../pages/Register.jsx';
import Unauthorized from '../pages/Unauthorized';

export const PublicRoutes = (
    <Route path="/" element={<Layout/>}>
        <Route index element={<PublicHome/>}/>
        <Route path="login" element={<Login/>}/>
        <Route path="register" element={<Register/>}/>
        <Route path="unauthorized" element={<Unauthorized/>}/>
    </Route>
);