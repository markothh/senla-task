public class Clothing extends Product {
    private String size;
    private String material;

    public Clothing(String name, double weight, String size, String material) {
        super(name, weight);
        this.size = size;
        this.material = material;
    }

    @Override
    public void displayInfo() {
        System.out.printf("Одежда: %s, Размер: %s, Материал: %s, Вес: %.2f кг%n",
                name, size, material, weight);
    }
}