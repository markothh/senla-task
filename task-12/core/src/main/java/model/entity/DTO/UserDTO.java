package model.entity.DTO;

import model.entity.User;

public class UserDTO {
    private final int id;
    private final String name;

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "\nUserProfile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
