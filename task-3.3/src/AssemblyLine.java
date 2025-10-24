public class AssemblyLine implements IAssemblyLine {
    private final ILineStep step1;
    private final ILineStep step2;
    private final ILineStep step3;

    public AssemblyLine(ILineStep step1, ILineStep step2, ILineStep step3) {
        if (step1.getClass().equals(step2.getClass()) ||
            step1.getClass().equals(step3.getClass()) ||
            step2.getClass().equals(step3.getClass())) {
            throw new IllegalArgumentException("Части очков должны быть разных типов!");
        }

        this.step1 = step1;
        this.step2 = step2;
        this.step3 = step3;

        System.out.println("Создана сборочная линия");
    }

    @Override
    public IProduct assembleProduct(IProduct product) {
        IProductPart part;
        System.out.println("Начата сборка");

        part = step1.buildProductPart();
        product.installFirstPart(part);

        part = step2.buildProductPart();
        product.installSecondPart(part);

        part = step3.buildProductPart();
        product.installThirdPart(part);

        System.out.println("\n\nСборка закончена");
        return product;
    }
}
