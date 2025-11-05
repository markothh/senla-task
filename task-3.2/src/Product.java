abstract class Product {
    protected String name;
    protected double weight;

    public Product(String name, double weight) {
        this.name = name;
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public String getName() {
        return name;
    }

    public abstract void displayInfo();
}