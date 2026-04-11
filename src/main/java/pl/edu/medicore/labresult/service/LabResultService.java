package pl.edu.medicore.labresult.service;

import pl.edu.medicore.labresult.model.LabResult;

import java.util.List;

public interface LabResultService {
    void processLabResults(Long testId);

    List<LabResult> getLabResultsByTestId(Long testId);

    List<LabResult> getLabResultsByPatientId(Long patientId);
}
