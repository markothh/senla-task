public class Arms implements IProductPart {
    private final String name;
    private final String color;
    private final String material;
    private final double length;

    public Arms(String name, String color, String material, double length) {
        this.name = name;
        this.color = color;
        this.material = material;
        this.length = length;

        System.out.println("\n===============================");
        System.out.printf("Созданы дужки '%s': %n" +
                            "Цвет - %s%n" +
                            "Материал - %s%n" +
                            "Длина - %.2f%n",
                            this.name, this.color, this.material, this.length);
    }

    @Override
    public String getName() {
        return name;
    }
}
