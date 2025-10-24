public class Task {
    public static void main() {
        Warehouse warehouse = new Warehouse(100);
        WarehouseRandomFiller.fillWarehouse(warehouse);

        warehouse.displayProductList();
        System.out.printf("Вес товаров на складе: %.2f", warehouse.getCurrentWeight());
    }
}
