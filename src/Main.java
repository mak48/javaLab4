import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // input settings
        System.out.print("Input number of floors: ");
        var floorsCount = scanner.nextInt();
        System.out.print("Input number of requests: ");
        var requestsCount = scanner.nextInt();
        System.out.print("Input requests interval (ms): ");
        var requestsInterval = scanner.nextInt();

        // initialize
        ElevatorsManager manager = new ElevatorsManager(floorsCount);
        Thread requests = new Thread(new RequestThread(manager, requestsCount, requestsInterval));
        Thread elevators = new Thread(new ElevatorsThread(manager));
        requests.start();
        elevators.start();
        try {
            requests.join();
            elevators.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.print("\uD83C\uDF89 All passengers have been delivered");
    }
}