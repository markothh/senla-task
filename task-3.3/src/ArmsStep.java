public class ArmsStep implements ILineStep {
    private Arms arms;

    @Override
    public IProductPart buildProductPart() {
        arms = new Arms("Дужки 001", "черный", "пластик", 12.3);
        return arms;
    }
}
