public class Config {
    String src;
    int pop_size;
    int gen;
    int numberOfRuns;
    double Px;
    double Pm;
    int Tour;
    boolean pickElite;
    Main.selectType selectType;
    Main.crossType crossType;
    DataReader dataReader;

    public Config(String src, int pop_size, int gen, int numberOfRuns, double px, double pm, int tour, boolean pickElite, Main.selectType selectType, Main.crossType crossType, DataReader dataReader) {
        this.src = src;
        this.pop_size = pop_size;
        this.gen = gen;
        this.numberOfRuns = numberOfRuns;
        Px = px;
        Pm = pm;
        Tour = tour;
        this.pickElite = pickElite;
        this.selectType = selectType;
        this.crossType = crossType;
        this.dataReader = dataReader;
    }
}
