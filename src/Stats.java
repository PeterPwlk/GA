import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stats {
//    private int numberOfRuns;
//    private int gen;
//    private int pop_size;
//    private double Px;
//    private double Pm;
//    private int tour;
//    private boolean pickElite;
//
//    private Main.crossType CROSSOVER_TYPE;
//    private Main.selectType SELECT_TYPE;

    private Config config;

    private List<TFP> tfpList;
    private double[] avgBest;
    private double[] avgWorst;
    private double[] avgAverage;
    private double avgBestScore;
    private List<Thief> avgThiefAlpha;

    public Stats(Config config) {
        this.config = config;
//        this.gen = config.gen;
//        this.pop_size = config.pop_size;
//        this.numberOfRuns = config.numberOfRuns;
//        Px = config.Px;
//        Pm = config.pm;
//        this.tour = tour;
//        this.pickElite = pickElite;

//        this.CROSSOVER_TYPE = CROSSOVER_TYPE;
//        this.SELECT_TYPE = SELECT_TYPE;
        tfpList = new ArrayList<>();
        avgBest = new double[config.gen];
        avgWorst = new double[config.gen];
        avgAverage = new double[config.gen];
        Arrays.fill(avgBest,0.0);
        Arrays.fill(avgWorst,0.0);
        Arrays.fill(avgAverage,0.0);
        avgThiefAlpha = new ArrayList<>();
    }

    private String createTFPName(int index){
        String crossType = (config.crossType == Main.crossType.OX)?"OX":"EX";
        String selectType = (config.selectType == Main.selectType.tournament)?"TOUR":"ROUL";
        return index + crossType + "_" + selectType;
    }
    private String createStatsName(){
        String crossType = (config.crossType == Main.crossType.OX)?"OX":"EX";
        String selectType = (config.selectType == Main.selectType.tournament)?"TOUR_"+config.Tour:"ROUL";
        String pickEliteString = (config.pickElite)?"pickElite_true":"pickElite_false";
        return config.src + "_" + crossType + "_" + selectType + "_PM" + config.Pm + "_PX" + config.Px + "_GEN" + config.gen + "_POP" + config.pop_size + pickEliteString + ".csv";
    }

    public void createTFPs(DataReader reader){
        for(int i = 0; i < config.numberOfRuns; i++){
            tfpList.add(new TFP(reader ,createTFPName(i) ,config.pop_size, config.gen, config.Px, config.Pm, config.Tour, config.crossType, config.selectType, config.pickElite, this));
        }
    }

    public synchronized void putBest(int index,double best){
        avgBest[index] += best;
    }
    public synchronized void putWorst(int index,double worst){
        avgWorst[index] += worst;
    }
    public synchronized void putAvg(int index,double avg){
        avgAverage[index] += avg;
    }
    public synchronized void putThiefAlpha(Thief thiefAlpha){
        avgThiefAlpha.add(thiefAlpha);
    }

    public void initialiseTFPs(){
        tfpList.forEach(tfp -> {
            tfp.setup();
            tfp.initialise();
        });
    }

    public void calculateAvgStats(){
        for(int i = 0; i < config.gen; i++){
            avgBest[i] = avgBest[i]/config.numberOfRuns;
            avgWorst[i] = avgWorst[i]/config.numberOfRuns;
            avgAverage[i] = avgAverage[i]/config.numberOfRuns;
        }
        avgBestScore = avgThiefAlpha.stream().mapToDouble(Thief::getScore)
                                .reduce(Double::sum).orElse(0.0)/config.numberOfRuns;
    }

    public void printAvgStats(){
        try {
            String statsFolder = "stats\\";
            BufferedWriter writer = new BufferedWriter(new FileWriter(statsFolder + createStatsName()));
            writer.write("alpha thief avg score: " + avgBestScore);
            avgThiefAlpha.sort(Thief::compareTo);
            Thief bestThiefAlpha = avgThiefAlpha.get(0);
            writer.newLine();
            writer.write("best thief alpha score: " + bestThiefAlpha.getScore() + " track: " + bestThiefAlpha.getTrack().toString());
            writer.newLine();
            String headings = "Gen," + "Best," + "Worst," + "Avg";
            writer.write(headings);
            writer.newLine();
            for(int i = 0; i < config.gen; i++){
                String stats;
                stats = i + "," + avgBest[i] + "," + avgWorst[i] + "," + avgAverage[i];
                writer.write(stats);
                writer.newLine();
            }
            writer.close();
            System.out.println(avgBestScore);
            System.out.println(bestThiefAlpha.getScore());
        } catch (IOException e){
            System.out.println("error creating file");
        }

    }

    public void start(){
        List<Thread> tfpThreads = new ArrayList<>();
        try {
            for(TFP tfp : tfpList){
                Thread tfpThread = new Thread(tfp);
                tfpThreads.add(tfpThread);
                tfpThread.start();
            }
            for(Thread tfpThread : tfpThreads){
                tfpThread.join();
            }
            System.out.println("finished " + createStatsName());
            calculateAvgStats();
            printAvgStats();
        } catch (InterruptedException e){
            System.out.println("error");
        }
    }
}
