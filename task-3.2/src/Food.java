public class Food extends Product {
    private String expirationDate;
    private boolean requiresRefrigeration;

    public Food(String name, double weight, String expirationDate, boolean requiresRefrigeration) {
        super(name, weight);
        this.expirationDate = expirationDate;
        this.requiresRefrigeration = requiresRefrigeration;
    }

    @Override
    public void displayInfo() {
        String refrigeration = requiresRefrigeration ? "требуется" : "не требуется";
        System.out.printf("Продукт: %s, Вес: %.2f кг, Срок годности: %s, Охлаждение: %s%n",
                name, weight, expirationDate, refrigeration);
    }
}
