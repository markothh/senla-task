public class CancelledOrderStatus implements IOrderStatus {
    @Override
    public IOrderStatus resetToNew(Order order) {
        return new NewOrderStatus();
    }

    @Override
    public IOrderStatus complete(Order order) {
        throw new IllegalStateException("Невозможно выполнить отмененный заказ.");
    }

    @Override
    public IOrderStatus cancel(Order order) {
        return this;
    }

    @Override
    public String toString() {
        return "Отменен";
    }
}
