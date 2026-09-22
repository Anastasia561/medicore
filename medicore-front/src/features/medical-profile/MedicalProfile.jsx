import {useEffect, useState} from "react";
import {useLocation, useParams} from "react-router-dom";
import {useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import ListContainer from "../listing/ListContainer.jsx";
import MedicalParamsForm from "./components/MedicalParamsForm.jsx";
import RiskResultsPanel from "./components/RiskResultsPanel.jsx";
import {useMedicalProfile} from "./hooks/useMedicalProfile.jsx";
import {useUpdateMedicalProfile} from "./hooks/useUpdateMedicalProfile.jsx";
import {useRiskResults} from "./hooks/useRiskResults.jsx";
import {medicalProfileSchema} from "./validation/medicalProfileSchema.js";

const MedicalProfile = () => {
    const {patientId} = useParams();
    const {state} = useLocation();
    const canEdit = !patientId;

    const [isEditing, setIsEditing] = useState(false);
    const [generalError, setGeneralError] = useState("");

    const {
        data: profile,
        isLoading: isProfileLoading,
        isError: isProfileError,
    } = useMedicalProfile(patientId);

    const riskPatientId = patientId || profile?.id;
    const {
        data: risks = [],
        isLoading: isRisksLoading,
        isError: isRisksError,
    } = useRiskResults(riskPatientId);

    const {
        register,
        handleSubmit,
        reset,
        setError,
        formState: {errors},
    } = useForm({
        resolver: yupResolver(medicalProfileSchema),
        mode: "onTouched",
        defaultValues: {
            weight: "",
            height: "",
            pregnancyStatus: "",
            gender: "",
        },
    });

    const {mutateAsync: updateProfile, isPending: isSaving} = useUpdateMedicalProfile({
        setGeneralError,
        setError,
    });

    useEffect(() => {
        if (profile) {
            reset({
                weight: profile.weight ?? "",
                height: profile.height ?? "",
                pregnancyStatus: profile.pregnancyStatus ?? "",
                gender: profile.gender ?? "",
            });
        }
    }, [profile, reset]);

    const title = patientId && state?.userName
        ? `Medical profile — ${state.userName}`
        : "Medical profile";

    const onSubmit = async (formData) => {
        try {
            await updateProfile({
                weight: formData.weight === "" ? null : formData.weight,
                height: formData.height === "" ? null : formData.height,
                pregnancyStatus: formData.pregnancyStatus,
            });
            setIsEditing(false);
        } catch (error) {
            console.error(error);
        }
    };

    const handleCancel = () => {
        setIsEditing(false);
        setGeneralError("");
        if (profile) {
            reset({
                weight: profile.weight ?? "",
                height: profile.height ?? "",
                pregnancyStatus: profile.pregnancyStatus ?? "",
                gender: profile.gender ?? "",
            });
        }
    };

    if (isProfileError) {
        return <div className="alert alert-danger m-4">Failed to load medical profile</div>;
    }

    return (
        <ListContainer title={title}>
            {isProfileLoading ? (
                <div className="p-5 text-center">
                    <div className="spinner-border text-primary" role="status"/>
                    <div className="mt-3 text-muted">Loading medical profile...</div>
                </div>
            ) : (
                <div className="row g-4">
                    <div className="col-lg-5">
                        <form onSubmit={handleSubmit(onSubmit)}>
                            <MedicalParamsForm
                                register={register}
                                errors={errors}
                                gender={profile?.gender}
                                canEdit={canEdit}
                                isEditing={isEditing}
                                isSaving={isSaving}
                                generalError={generalError}
                                onEdit={() => setIsEditing(true)}
                                onCancel={handleCancel}
                            />
                        </form>
                    </div>
                    <div className="col-lg-7">
                        <RiskResultsPanel
                            risks={risks}
                            isLoading={isRisksLoading}
                            isError={isRisksError}
                        />
                    </div>
                </div>
            )}
        </ListContainer>
    );
};

export default MedicalProfile;
