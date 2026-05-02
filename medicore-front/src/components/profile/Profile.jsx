import "./Profile.css";
import DoctorFields from "./roles/DoctorFields.jsx";
import PatientFields from "./roles/PatientFields.jsx";
import AdminFields from "./roles/AdminFields.jsx";
import useAuth from "../../hooks/auth/useAuth.jsx";
import {useProfile} from "./hooks/useProfile.jsx";

const Profile = () => {
    const {auth} = useAuth();
    const {data: user, isLoading, isError} = useProfile();
    const roleMap = {
        ROLE_DOCTOR: DoctorFields,
        ROLE_PATIENT: PatientFields,
        ROLE_ADMIN: AdminFields,
    };

    if (isLoading) return <p>Loading...</p>;
    if (isError || !user) return <p>Failed to load profile</p>;

    const renderFields = () => {
        const Component = roleMap[auth?.role];
        return Component ? <Component user={user}/> : <p>No data</p>;
    };

    return (
        <div className="profile-container">
            <div className="profile-wrapper">
                <h2>My Profile</h2>
                <p className="subtitle">View your personal details</p>

                <div className="profile-card">
                    <div className="profile-grid">
                        {renderFields()}
                    </div>

                    <button className="edit-btn">Edit</button>
                </div>
            </div>
        </div>
    );
};

export default Profile;