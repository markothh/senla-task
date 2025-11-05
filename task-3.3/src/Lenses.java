public class Lenses implements IProductPart {
    private final String name;
    private final double leftDiopters;
    private final double rightDiopters;

    public Lenses(String name, double leftDiopters, double rightDiopters) {
        this.name = name;
        this.leftDiopters = leftDiopters;
        this.rightDiopters = rightDiopters;

        System.out.println("\n===============================");
        System.out.printf("Созданы линзы '%s': %n" +
                        "Диоптрии правой линзы - %.1f%n" +
                        "Диоптрии левой линзы - %.1f%n",
                this.name, this.rightDiopters, this.leftDiopters);
    }

    @Override
    public String getName() {
        return name;
    }
}
