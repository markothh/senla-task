public class Frame implements IProductPart {
    private final String name;
    private final String color;
    private final String material;
    private final String shape;
    private final double height;
    private final double width;

    public Frame(String name, String color, String material, String shape, double height, double width) {
        this.name = name;
        this.color = color;
        this.material = material;
        this.shape = shape;
        this.height = height;
        this.width = width;

        System.out.println("\n===============================");
        System.out.printf("Создан корпус '%s': %n" +
                        "Цвет - %s%n" +
                        "Материал - %s%n" +
                        "Форма - %s%n" +
                        "Высота рамки для линзы - %.2f%n" +
                        "Ширина рамки для линзы - %.2f%n",
                this.name, this.color, this.material, this.shape, this.height, this.width);
    }

    @Override
    public String getName() {
        return name;
    }
}
