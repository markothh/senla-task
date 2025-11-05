public interface IOrderStatus {
    public IOrderStatus resetToNew(Order order);
    public IOrderStatus complete(Order order);
    public IOrderStatus cancel(Order order);
}
