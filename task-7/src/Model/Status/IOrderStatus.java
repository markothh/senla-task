package Model.Status;

import Model.Entity.Order;

import java.io.Serializable;

public interface IOrderStatus extends Serializable {
    public IOrderStatus resetToNew(Order order);
    public IOrderStatus complete(Order order);
    public IOrderStatus cancel(Order order);
}
