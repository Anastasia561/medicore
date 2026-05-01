package pl.edu.medicore.labresult.service;

import pl.edu.medicore.labresult.model.LabResult;

import java.util.List;
import java.util.UUID;

public interface LabResultService {
    void processLabResults(Long testId);

    List<LabResult> getLabResultsByTestId(Long testId);

    List<LabResult> getLabResultsByPatientId(UUID patientId);
}
