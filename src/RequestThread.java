import java.time.Instant;
import java.util.Random;

public class RequestThread implements Runnable {
    private final ElevatorsManager manager;
    private final int requestsCount;
    private final int requestsInterval;

    RequestThread(ElevatorsManager manager, int requestsCount, int requestsInterval) {
        this.manager = manager;
        this.requestsCount = requestsCount;
        this.requestsInterval = requestsInterval;
    }

    @Override
    public void run() {
        Random random = new Random(Instant.now().getEpochSecond());
        // generate requests
        for (int i = 0; i < requestsCount; i++) {
            var start = random.nextInt(0, manager.floorsCount + 1);
            var end = random.nextInt(0, manager.floorsCount + 1);
            while (start == end) {
                end = random.nextInt(0, manager.floorsCount + 1);
            }
            var direction = start > end ? -1 : 1;
            this.manager.addWaiters(new Request(start, end, direction));
            try {
                Thread.sleep(requestsInterval);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ElevatorsManager.requestsOver = true;
    }
}