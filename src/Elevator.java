import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Elevator {
    public int number;
    public int capacity;
    public int currentFloor;
    public ArrayDeque<Integer> targetFloors;
    public int status; // 1 - up, -1 - down, 0 - waiting;
    public List<Integer> passengers;
    public int passengerStatus; // 1-up, 0-none, -1-down;`

    Elevator(int number) {
        this.number = number;
        capacity = 10;
        currentFloor = 0;
        targetFloors = new ArrayDeque<>();
        status = 0;
        passengers = new ArrayList<>();
        passengerStatus = 0;
    }
}