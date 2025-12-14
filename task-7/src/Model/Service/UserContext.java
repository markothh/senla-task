package Model.Service;

import Model.Entity.User;

import java.io.Serial;
import java.io.Serializable;

public class UserContext implements Serializable {
    private static UserContext INSTANCE;
    private User currentUser;

    private UserContext() {}

    public static UserContext getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserContext();
        }
        return INSTANCE;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    @Serial
    private Object readResolve() {
        if (INSTANCE == null) {
            INSTANCE = this;
        }
        else {
            INSTANCE.currentUser = this.currentUser;
        }
        return INSTANCE;
    }
}
