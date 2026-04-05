package pl.edu.medicore.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.edu.medicore.infrastructure.messaging.event.LabResultsExtractedEvent;
import pl.edu.medicore.risk.service.RiskResultService;

@Async
@Component
@RequiredArgsConstructor
public class LabResultsExtractedListener {
    private final RiskResultService riskResultService;

    @EventListener
    public void handle(LabResultsExtractedEvent event) {
        riskResultService.calculateRisk(event.testId());
    }
}
