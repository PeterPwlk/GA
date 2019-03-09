import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        final String SRC = "student/trivial_0.ttp";

        TFP tfp = new TFP(SRC);

        tfp.setup();
        tfp.printPickedItems();
        tfp.start();
    }
}
