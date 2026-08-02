import {useState} from 'react';
import {useForm, FormProvider} from 'react-hook-form';
import {yupResolver} from '@hookform/resolvers/yup';
import {registerSchema} from './validation/PatientRegistrationSchema.js';
import {useRegisterPatient} from './hooks/useRegisterPatient';
import logo from '../../../assets/logo.png';

import {Step1PersonalInfo} from './components/Step1PersonalInfo';
import {Step2Password} from './components/Step2Password';
import {Step3ContactDetails} from './components/Step3ContactDetails';
import {Step4Address} from './components/Step4Address';
import {Step5MedicalInfo} from './components/Step5MedicalInfo';

const STEP_FIELDS = {
    1: ['firstName', 'lastName', 'birthDate'],
    2: ['password', 'repeatPassword'],
    3: ['phoneNumber', 'email'],
    4: ['address.country', 'address.city', 'address.street', 'address.number'],
    5: ['gender', 'weight', 'height', 'pregnancyStatus'],
};

const STEP_HEADING = [
    {id: 1, title: "Personal Details", subtitle: "Tell us a bit about yourself"},
    {id: 2, title: "Account Security", subtitle: "Set up your login credentials"},
    {id: 3, title: "Contact Information", subtitle: "How can we reach you?"},
    {id: 4, title: "Address Details", subtitle: "Where are you located?"},
    {
        id: 5,
        title: "Medical Details",
        subtitle: "Help us tailor your care. These fields are optional and can be updated anytime in your medical profile"
    },
];

const PatientRegisterForm = () => {
    const [step, setStep] = useState(1);
    const [generalError, setGeneralError] = useState('');

    const methods = useForm({
        resolver: yupResolver(registerSchema),
        mode: 'onTouched',
        defaultValues: {pregnancyStatus: 'NOT_APPLICABLE'},
    });

    const {trigger, handleSubmit, setError} = methods;

    const {mutate: registerPatient, isPending} = useRegisterPatient(setError, setGeneralError);

    const nextStep = async () => {
        const isValid = await trigger(STEP_FIELDS[step]);
        if (isValid) setStep((prev) => prev + 1);
    };

    const prevStep = () => setStep((prev) => prev - 1);

    const onSubmit = (data) => {
        registerPatient(data);
    };

    const currentStepMeta = STEP_HEADING[step - 1];

    return (
        <div className="container py-4 py-md-5">
            <div className="row justify-content-center">
                <div className="col-lg-8 col-xl-7">
                    <div className="text-center mb-4">
                        <img
                            src={logo}
                            alt="MediCore Logo"
                            width="100"
                            height="70"
                            className="mb-2"
                        />
                        <h2 className="fw-bold mb-1">MediCore</h2>
                    </div>

                    <div className="card shadow-lg border-0">
                        <div className="card-body p-4 p-md-5">
                            <div className="d-flex justify-content-between align-items-center mb-3">
                                <h3 className="h5 mb-0">{currentStepMeta.title}</h3>
                                <span className="badge text-bg-primary">Step {step} of 5</span>
                            </div>
                            <p className="text-muted small mb-3">{currentStepMeta.subtitle}</p>

                            <div className="progress mb-4" role="progressbar" aria-label="Registration progress">
                                <div className="progress-bar" style={{width: `${(step / 5) * 100}%`}}/>
                            </div>

                            {generalError && (
                                <div className="alert alert-danger py-2 text-center" role="alert">
                                    {generalError}
                                </div>
                            )}

                            <FormProvider {...methods}>
                                <form
                                    onSubmit={handleSubmit(onSubmit)}>
                                    {step === 1 && <Step1PersonalInfo/>}
                                    {step === 2 && <Step2Password/>}
                                    {step === 3 && <Step3ContactDetails/>}
                                    {step === 4 && <Step4Address/>}
                                    {step === 5 && <Step5MedicalInfo/>}

                                    <div className="d-flex justify-content-between mt-4">
                                        {step > 1 && (
                                            <button type="button" onClick={prevStep}
                                                    className="btn btn-outline-secondary">
                                                Previous
                                            </button>
                                        )}
                                        {step < 5 ? (
                                            <button type="button" onClick={nextStep}
                                                    className="btn btn-primary ms-auto">
                                                Next
                                            </button>
                                        ) : (
                                            <button type="submit" disabled={isPending}
                                                    className="btn btn-success ms-auto">
                                                {isPending ? 'Registering...' : 'Submit'}
                                            </button>
                                        )}
                                    </div>
                                </form>
                            </FormProvider>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PatientRegisterForm;
