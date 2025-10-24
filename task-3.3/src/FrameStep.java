public class FrameStep implements ILineStep{
    private Frame frame;

    @Override
    public IProductPart buildProductPart() {
        frame = new Frame("Корпус 001", "черный", "пластик", "прямоугольная", 3.2,5.5);
        return frame;
    }
}
