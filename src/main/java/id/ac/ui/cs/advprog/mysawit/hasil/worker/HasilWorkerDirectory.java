package id.ac.ui.cs.advprog.mysawit.hasil.worker;

import java.util.Set;

public interface HasilWorkerDirectory {
    Set<String> findSupervisedWorkerIds(String mandorUsername);

    boolean isWorkerSupervisedBy(String workerId, String mandorUsername);

    String resolveWorkerName(String workerId);
}
