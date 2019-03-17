import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final String SRC = "student/easy_0.ttp";
        final int pop_size = 100;
        final int gen = 100;
        final double Px = 0.9;
        final double Pm = 0.05;
        final int Tour = 5;

        TFP tfp = new TFP(SRC, pop_size, gen, Px, Pm, Tour);

        tfp.setup();
        tfp.printPickedItems();
        tfp.initialise();
        tfp.start();
    }
}
