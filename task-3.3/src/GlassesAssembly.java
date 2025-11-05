public class GlassesAssembly {
    public static void main() {
        Glasses glasses = new Glasses();
        AssemblyLine assemblyLine = new AssemblyLine(new ArmsStep(), new FrameStep(), new LensesStep());

        assemblyLine.assembleProduct(glasses);
        System.out.println("Очки готовы!");
    }
}
