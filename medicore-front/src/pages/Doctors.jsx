import {useState, useEffect} from 'react';
import useAxiosPrivate from "../hooks/auth/useAxiosPrivate.jsx";
import {useNavigate, useLocation} from 'react-router-dom';

const Doctors = () => {
    const [doctors, setDoctors] = useState();
    const axiosPrivate = useAxiosPrivate();
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        let isMounted = true;
        const controller = new AbortController();

        const getDoctors = async () => {
            try {
                const response = await axiosPrivate.get('/doctors', {
                    signal: controller.signal
                });
                console.log(response.data.data.content);
                isMounted && setDoctors(response.data.data.content);
            } catch (e) {
                console.error(e);

                if (e.name === 'CanceledError' || e.code === 'ERR_CANCELED') return;

                if (e.response?.status === 401 || e.response?.status === 403) {
                    navigate('/', {state: {from: location}, replace: true});
                } else {
                    console.error("Unexpected error:", e);
                }
            }
        }

        getDoctors();

        return () => {
            isMounted = false;
            controller.abort();
        }
    }, [])

    return (
        <>
            <h1>Doctor list</h1>
            {doctors?.length
                ? (
                    <ul>
                        {doctors.map((doctor, index) => (
                            <li key={index}>
                                <p>{doctor.firstName}</p>
                                <p>{doctor.lastName}</p>
                                <p>{doctor.email}</p>
                            </li>
                        ))}
                    </ul>
                ) : <p>No doctors</p>
            }
        </>
    )
}

export default Doctors;