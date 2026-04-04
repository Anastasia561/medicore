package pl.edu.medicore.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.edu.medicore.labresult.service.LabResultService;
import pl.edu.medicore.infrastructure.messaging.event.FileUploadEvent;

@Async
@Component
@RequiredArgsConstructor
public class FileUploadListener {

    private final LabResultService labResultService;

    @EventListener
    public void handle(FileUploadEvent event) {
        labResultService.processLabResults(event.testId());
    }
}
