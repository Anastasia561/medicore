package pl.edu.medicore.infrastructure.messaging.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import pl.edu.medicore.infrastructure.messaging.event.LabResultsExtractedEvent;
import pl.edu.medicore.risk.service.contract.RiskResultService;

@Async
@Component
@RequiredArgsConstructor
public class LabResultsExtractedListener {
    private final RiskResultService riskResultService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(LabResultsExtractedEvent event) {
        riskResultService.calculateRiskForTest(event.testId());
    }
}
