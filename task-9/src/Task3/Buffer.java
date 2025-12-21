package Task3;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {
    private final int capacity;
    private final Queue<Integer> queue = new LinkedList<>();

    public Buffer(int capacity) {
        this.capacity = capacity;
    }

    public boolean isFull() {
        return queue.size() == capacity;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public void put(int value) {
        queue.add(value);
    }

    public int get() {
        return queue.remove();
    }
}
