import java.util.Arrays;
import java.util.List;

public class WarehouseRandomFiller {
    private static List<Product> predefinedProducts = Arrays.asList(
            new Electronics("Ноутбук", 2.5, "Dell", 24),
            new Electronics("Смартфон", 0.3, "Samsung", 12),
            new Electronics("Телевизор", 15.0, "LG", 36),
            new Electronics("Планшет", 0.6, "Apple", 12),

            new Food("Яблоки", 25.0, "2024-12-31", false),
            new Food("Молоко", 1.5, "2024-03-15", true),
            new Food("Рис", 10.0, "2025-12-31", false),
            new Food("Мясо", 5.0, "2024-04-10", true),

            new Clothing("Футболка", 0.2, "M", "Хлопок"),
            new Clothing("Джинсы", 0.7, "L", "Деним"),
            new Clothing("Куртка", 1.2, "XL", "Нейлон"),
            new Clothing("Рубашка", 0.4, "L", "Хлопок")
    );
    private static Product chooseRandomProduct() {
        return predefinedProducts.get((new java.util.Random()).nextInt(12));
    }

    public static void fillWarehouse(Warehouse warehouse) {
        Product product;
        do {
            product = chooseRandomProduct();
        } while (warehouse.addProduct(product));
    }
}
