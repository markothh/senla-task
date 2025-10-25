import java.util.List;

public class BookShop {
    private final RequestManager requestManager = new RequestManager();
    private final OrderManager orderManager = new OrderManager(requestManager);
    private final Warehouse warehouse = new Warehouse();

    public void removeBookFromStock(String bookName) {
        warehouse.removeFromStock(bookName);
    }

    public int createOrder(int userId, List<String> bookNames) {
        return orderManager.createOrder(userId, warehouse.formIdListFromNames(bookNames)).getId();
    }

    public void cancelOrder(int orderId) {
        orderManager.cancelOrder(orderId);
    }

    public void setOrderStatus(int orderId, String status) {
        orderManager.setOrderStatus(orderId, status);
    }

    public void addBookToStock(String bookName) {
        warehouse.addToStock(bookName);
        requestManager.satisfyAllRequestsByBookId(warehouse.getBookByName(bookName).getId());
    }

    public int createBookRequest(String bookName) {
        if (warehouse.isBookAvailable(bookName)) {
            throw new IllegalArgumentException("Невозможно создать заказ на книгу, которая есть в наличии.");
        }

        return requestManager.createRequest(warehouse.getBookByName(bookName).getId()).getId();
    }
}
