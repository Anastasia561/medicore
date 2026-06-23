import "./Profile.css";
import DoctorFields from "./components/DoctorFields.jsx";
import GeneralInfoFields from "./components/GeneralInfoFields.jsx";
import useAuth from "../../hooks/useAuth.jsx";
import {useProfile} from "./hooks/useProfile.jsx";

const Profile = () => {
    const {auth} = useAuth();
    const {data: user, isLoading, isError} = useProfile();
    const roleMap = {
        ROLE_DOCTOR: DoctorFields,
        ROLE_PATIENT: GeneralInfoFields,
        ROLE_ADMIN: GeneralInfoFields,
    };

    if (isLoading) {
        return (
            <div className="container p-4 text-center mt-5">
                <div className="spinner-border text-primary" role="status"/>
                <div className="mt-3 text-muted">
                    Loading profile...
                </div>
            </div>
        );
    }
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