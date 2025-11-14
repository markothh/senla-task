package Model.Service;

import Model.Entity.User;

public class UserContext {
    private static UserContext INSTANSE;
    private User currentUser;

    private UserContext() {}

    public static UserContext getInstance() {
        if (INSTANSE == null) {
            INSTANSE = new UserContext();
        }
        return INSTANSE;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
