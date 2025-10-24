import java.util.ArrayList;
import java.util.List;

public class Warehouse {
    private List<Product> products;
    private double maxCapacity;
    private double currentWeight;

    public Warehouse(double maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.currentWeight = 0;
        this.products = new ArrayList<>();
    }

    public boolean addProduct(Product product) {
        if (currentWeight + product.getWeight() <= maxCapacity) {
            products.add(product);
            currentWeight += product.getWeight();

            return true;
        } else {
            return false;
        }
    }

    public void displayProductList() {
        System.out.println("\nТовары на складе:");
        for (Product p : products) {
            p.displayInfo();
        }
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }
}