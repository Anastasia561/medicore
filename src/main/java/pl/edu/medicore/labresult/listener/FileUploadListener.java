package pl.edu.medicore.labresult.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.edu.medicore.labresult.service.contract.LabResultService;
import pl.edu.medicore.test.event.FileUploadEvent;

@Component
@RequiredArgsConstructor
public class FileUploadListener {

    private final LabResultService labResultService;

    @EventListener
    public void handle(FileUploadEvent event) {
        labResultService.processLabResults(event.testId());
    }
}
