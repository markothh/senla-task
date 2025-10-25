public class CompleteOrderStatus implements IOrderStatus {
    @Override
    public IOrderStatus resetToNew(Order order) {
        throw new IllegalStateException("Невозможно изменить статус на \"Новый\" у выполненного заказа.");
    }

    @Override
    public IOrderStatus complete(Order order) {
        return this;
    }

    @Override
    public IOrderStatus cancel(Order order) {
        throw new IllegalStateException("Нельзя отменить завершенный заказ.");
    }
}
