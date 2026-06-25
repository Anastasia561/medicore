import {useState, useEffect} from "react";
import {useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import {profileSchema} from "./validation/ProfileSchema.js";
import {useProfile} from "./hooks/useProfile.jsx";
import {useUpdateProfile} from "./hooks/useUpdateProfile.jsx";
import useAuth from "../../hooks/useAuth.jsx";
import DoctorFields from "./components/DoctorFields.jsx";
import GeneralInfoFields from "./components/GeneralInfoFields.jsx";
import "./Profile.css";

const Profile = () => {
    const {auth} = useAuth();
    const {data: user, isLoading, isError} = useProfile();
    const [isEditing, setIsEditing] = useState(false);
    const [generalError, setGeneralError] = useState('');

    const {
        register, handleSubmit, setError,
        reset, formState: {errors},
    } = useForm({
        resolver: yupResolver(profileSchema),
        mode: "onTouched"
    });

    const {
        mutateAsync: updateProfile,
        isPending: isSaving
    } = useUpdateProfile({setGeneralError, setError});


    useEffect(() => {
        if (user) {
            reset(user);
        }
    }, [user, reset]);

    const roleMap = {
        ROLE_DOCTOR: DoctorFields,
        ROLE_PATIENT: GeneralInfoFields,
        ROLE_ADMIN: GeneralInfoFields,
    };

    if (isLoading) return (
        <div className="text-center mt-5">
            <div className="spinner-border text-primary"/>
        </div>
    );
    if (isError || !user) return <p>Failed to load profile</p>;

    const onFormSubmit = async (validData) => {
        try {
            await updateProfile(validData);
            setIsEditing(false);
        } catch (error) {
            console.error(error);
        }
    };

    const handleCancel = () => {
        setIsEditing(false);
        reset(user);
        setGeneralError("")
    };

    const renderFields = () => {
        const Component = roleMap[auth?.role];
        return Component ? (
            <Component
                user={user}
                register={register}
                errors={errors}
                isEditing={isEditing}
            />
        ) : <p>No data</p>;
    };

    return (
        <div className="profile-container">
            <form onSubmit={handleSubmit(onFormSubmit)} className="profile-wrapper">
                <h2>My Profile</h2>
                <p className="subtitle">View and update your personal details</p>

                <div className="profile-card">

                    {generalError && (
                        <div className="alert alert-danger py-2 text-center" role="alert">
                            {generalError}
                        </div>
                    )}

                    <div className="profile-grid">
                        {renderFields()}
                    </div>

                    <div className="d-flex gap-2 mt-4 justify-content-center">
                        {isEditing ? (
                            <>
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={handleCancel}
                                    disabled={isSaving}
                                >
                                    Cancel
                                </button>
                                <button
                                    type="submit"
                                    className="btn btn-success d-flex align-items-center gap-2"
                                    disabled={isSaving}
                                >
                                    {isSaving ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm" role="status"
                                                  aria-hidden="true"></span>
                                            Saving...
                                        </>
                                    ) : (
                                        "Save Changes"
                                    )}
                                </button>
                            </>
                        ) : (
                            <button type="button" className="edit-btn" onClick={() => setIsEditing(true)}>
                                Edit Profile
                            </button>
                        )}
                    </div>
                </div>
            </form>
        </div>
    );
};

export default Profile;