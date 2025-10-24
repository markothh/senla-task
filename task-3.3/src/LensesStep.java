public class LensesStep implements ILineStep{
    private Lenses lenses;

    @Override
    public IProductPart buildProductPart() {
        lenses = new Lenses("Линзы 001", -2.0, -1.7);
        return lenses;
    }
}
