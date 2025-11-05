public class Electronics extends Product{
    private String brand;
    private int warrantyMonths;

    public Electronics(String name, double weight, String brand, int warrantyMonths) {
        super(name, weight);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public void displayInfo() {
        System.out.printf("Электроника: %s, Бренд: %s, Вес: %.2f кг, Гарантия: %d мес.%n",
                name, brand, weight, warrantyMonths);
    }
}
