import java.util.*;

public class ElevatorsManager {
    public static List<Request> requests = new ArrayList<>();
    public ArrayList<Map<Integer, ArrayDeque<Integer>>> floors;
    public Elevator first;
    public Elevator second;
    public int floorsCount;
    public static boolean requestsOver = false;

    ElevatorsManager(int floorsCount) {
        this.floorsCount = floorsCount;
        first = new Elevator(1);
        second = new Elevator(2);
        floors = new ArrayList<>(floorsCount + 1);
        for (int i = 0; i <= floorsCount; i++) {
            Map<Integer, ArrayDeque<Integer>> floorMap = new HashMap<>();
            floorMap.put(-1, new ArrayDeque<>());
            floorMap.put(1, new ArrayDeque<>());
            floors.add(floorMap);
        }
    }
    public synchronized void stepElevators() {
        checkFloor(first);
        checkFloor(second);
        changeElevatorStatus(first);
        changeElevatorStatus(second);
    }
    public synchronized void addWaiters(Request rq) {
        System.out.println("\uD83D\uDEB9 New request on " + rq.start + " floor, " + "direction " + (rq.direction == 1 ? "up" : "down"));
        floors.get(rq.start).get(rq.direction).add(rq.end);
        if (first.targetFloors.isEmpty() && second.targetFloors.isEmpty()) {
            if (Math.abs(first.currentFloor - rq.start) <= Math.abs(second.currentFloor - rq.start)) {
                first.targetFloors.add(rq.start);
                first.passengerStatus = rq.direction;
                System.out.println("☑️ Elevator (1) take request on floor " + rq.start + ", direction " +
                        (rq.direction == 1 ? "up" : "down"));
            } else {
                second.targetFloors.add(rq.start);
                second.passengerStatus = rq.direction;
                System.out.println("☑️ Elevator (2) take request on floor " + rq.start + ", direction " +
                        (rq.direction == 1 ? "up" : "down"));
            }
        } else if (first.targetFloors.isEmpty() || (first.passengerStatus == rq.direction &&
                ((rq.direction == 1 && first.currentFloor <= rq.start) || (rq.direction == -1 && first.currentFloor >= rq.start)))) {
            first.targetFloors.add(rq.start);
            first.passengerStatus = rq.direction;
            System.out.println("☑️ Elevator (1) take request on floor " + rq.start + ", direction " +
                    (rq.direction == 1 ? "up" : "down"));
        } else if (second.targetFloors.isEmpty() || (second.passengerStatus == rq.direction &&
                ((rq.direction == 1 && second.currentFloor <= rq.start) || (rq.direction == -1 && second.currentFloor >= rq.start)))) {
            second.targetFloors.add(rq.start);
            second.passengerStatus = rq.direction;
            System.out.println("☑️ Elevator (2) take request on floor " + rq.start + ", direction " +
                    (rq.direction == 1 ? "up" : "down"));
        } else {
            requests.add(rq);
            System.out.println("⏸️️ All elevators are busy, wait");
        }
    }
    private void checkFloor(Elevator elevator) {
        while (elevator.passengers.contains(elevator.currentFloor)) {
            var passengerCount = elevator.passengers.size();
            elevator.passengers.removeAll(List.of(elevator.currentFloor));
            elevator.targetFloors.removeAll(List.of(elevator.currentFloor));
            System.out.println("⬅️ " + (passengerCount - elevator.passengers.size()) +
                    " passenger left elevator (" + elevator.number + ") on floor " + elevator.currentFloor);
            if (elevator.passengers.size() == 0) {
                elevator.passengerStatus = 0;
            }
        }
        if (elevator.passengerStatus != 0) {
            ArrayDeque<Integer> floorQueue = floors.get(elevator.currentFloor).get(elevator.passengerStatus);
            while (!floorQueue.isEmpty() && elevator.passengers.size() < elevator.capacity) {
                int currentPassenger = floorQueue.poll();
                elevator.targetFloors.removeAll(List.of(elevator.currentFloor));
                popFromRequests(elevator, currentPassenger);
                elevator.targetFloors.add(currentPassenger);
                elevator.passengers.add(currentPassenger);
                elevator.status = elevator.passengerStatus;
                System.out.println("➡️ Passenger enter elevator (" + elevator.number + ") on floor " + elevator.currentFloor);
            }
        } else {
            ArrayDeque<Integer> floorQueueUp = floors.get(elevator.currentFloor).get(1);
            ArrayDeque<Integer> floorQueueDown = floors.get(elevator.currentFloor).get(-1);
            ArrayDeque<Integer> floorQueue;
            if (floorQueueUp.size() >= floorQueueDown.size()) {
                floorQueue = floorQueueUp;
                elevator.status = 1;
                elevator.passengerStatus = 1;
            } else {
                floorQueue = floorQueueDown;
                elevator.status = -1;
                elevator.passengerStatus = -1;
            }
            while (!floorQueue.isEmpty() && elevator.passengers.size() < elevator.capacity) {
                int currentPassenger = floorQueue.poll();
                elevator.targetFloors.removeAll(List.of(elevator.currentFloor));
                popFromRequests(elevator, currentPassenger);
                elevator.targetFloors.add(currentPassenger);
                elevator.passengers.add(currentPassenger);
                System.out.println("➡️ Passenger enter elevator (" + elevator.number + ") on floor " + elevator.currentFloor);
            }
        }
    }
    private void popFromRequests(Elevator elevator, int currentPassenger) {
        var i = 0;
        while (i < requests.size()) {
            var request = requests.get(i);
            if (request.start == elevator.currentFloor && request.end == currentPassenger) {
                requests.remove(i);
            }
            i++;
        }
    }
    private void changeElevatorStatus(Elevator elevator) {
        if (elevator.targetFloors.isEmpty()) {
            if (requests.isEmpty()) {
                elevator.status = 0;
                elevator.passengerStatus = 0;
            } else {
                Request rq = requests.get(0);
                requests.remove(0);
                elevator.targetFloors.add(rq.start);
                elevator.passengerStatus = rq.direction;
                elevator.status = elevator.currentFloor < rq.start ? 1 : -1;
                System.out.println("☑️ Elevator (" + elevator.number + ") take request on floor " + rq.start
                        + ", direction " +  (rq.direction == 1 ? "up" : "down"));
            }
        } else if (elevator.targetFloors.peek() == elevator.currentFloor) {
            elevator.targetFloors.removeAll(List.of(elevator.currentFloor));
        } else if (elevator.targetFloors.peek() < elevator.currentFloor) {
            elevator.status = -1;
        } else {
            elevator.status = 1;
        }

        if (elevator.status == 0) {
            System.out.println("\uD83D\uDCAC Elevator (" + elevator.number + ") is waiting on floor " + elevator.currentFloor);
        } else if (elevator.status == -1) {
            if (elevator.currentFloor > 0) {
                elevator.currentFloor--;
            } else {
                elevator.status = 0;
                elevator.passengerStatus = 0;
            }
            System.out.println("⬇️ Elevator (" + elevator.number + ") is going down. Current floor = " + elevator.currentFloor);
        } else if (elevator.status == 1){
            if (elevator.currentFloor < floorsCount) {
                elevator.currentFloor++;
            } else {
                elevator.status = 0;
                elevator.passengerStatus = 0;
            }
            System.out.println("⬆️ Elevator (" + elevator.number + ") is going up. Current floor = " + elevator.currentFloor);
        }
    }
}