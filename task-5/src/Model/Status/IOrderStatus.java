package Model.Status;

import Model.Entity.Order;

public interface IOrderStatus {
    public IOrderStatus resetToNew(Order order);
    public IOrderStatus complete(Order order);
    public IOrderStatus cancel(Order order);
}
