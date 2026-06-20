import {Route} from 'react-router-dom';
import Layout from '../layouts/Layout';
import Login from '../features/auth/Login.jsx';
import Unauthorized from '../pages/Unauthorized';

export const PublicRoutes = (
    <Route path="/" element={<Layout/>}>
        <Route index element={<Login/>}/>
        <Route path="unauthorized" element={<Unauthorized/>}/>
    </Route>
);