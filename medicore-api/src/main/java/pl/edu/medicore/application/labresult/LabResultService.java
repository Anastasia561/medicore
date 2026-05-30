package pl.edu.medicore.application.labresult;

import pl.edu.medicore.common.encryption.HashId;

import java.util.List;

public interface LabResultService {
    void processLabResults(HashId testId);

    List<LabResult> getLabResultsByTestId(HashId testId);

    List<LabResult> getLabResultsByPatientId(HashId patientId);
}
