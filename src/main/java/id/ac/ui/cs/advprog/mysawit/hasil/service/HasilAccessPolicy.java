package id.ac.ui.cs.advprog.mysawit.hasil.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;


@Component
public class HasilAccessPolicy {
    private final HasilWorkerDirectory workerDirectory;

    public HasilAccessPolicy(HasilWorkerDirectory workerDirectory) {
        this.workerDirectory = workerDirectory;
    }

    public void ensureMandorSupervisesWorker(String mandorUsername, String workerId) {
        if (!workerDirectory.isWorkerSupervisedBy(workerId, mandorUsername)) {
            throw new AccessDeniedException("worker tidak berada di bawah mandor ini!");
        }
    }
}
