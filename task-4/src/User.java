import java.util.ArrayList;
import java.util.List;

public class User {
    private static int nextId = 1;

    private int id;
    private String name;

    public User(String name) {
        this.id = User.nextId++;

        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }
}
