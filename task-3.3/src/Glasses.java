public class Glasses implements IProduct {
    private IProductPart firstPart;
    private IProductPart secondPart;
    private IProductPart thirdPart;

    public Glasses() {
        System.out.println("Создана заготовка очков");
    }

    @Override
    public void installFirstPart(IProductPart part) {
        System.out.printf("%nУстановлена первая часть очков: %s", part.getName());
        this.firstPart = part;
    }

    @Override
    public void installSecondPart(IProductPart part) {
        System.out.printf("%nУстановлена вторая часть очков: %s", part.getName());
        this.secondPart = part;
    }

    @Override
    public void installThirdPart(IProductPart part) {
        System.out.printf("%nУстановлена третья часть очков: %s", part.getName());
        this.thirdPart = part;
    }
}
