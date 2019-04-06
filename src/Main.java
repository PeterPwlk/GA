import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public enum crossType {
        OX,EX
    }
    public enum selectType {
        roulette,tournament
    }
    public static void main(String[] args) {

        DataReader trivial0 = new DataReader("student/trivial_0.ttp");
        DataReader easy3 = new DataReader("student/easy_0.ttp");
        DataReader medium4 = new DataReader("student/medium_4.ttp");
        DataReader medium0 = new DataReader("student/medium_0.ttp");
        DataReader hard0 = new DataReader("student/hard_0.ttp");
        DataReader hard2 = new DataReader("student/hard_2.ttp");
        try {
            trivial0.loadData();
            easy3.loadData();
            medium4.loadData();
            medium0.loadData();
            hard0.loadData();
            hard2.loadData();
        } catch (IOException e){
            System.out.println("unable to read data");
        }

        //Runs on different files comparing Ox with Ex
        List<Config> configs = new ArrayList<>();
        configs.add(new Config("trivial_0", 100, 100, 10, 0.7, 0.05, 5, true, selectType.tournament, crossType.OX, trivial0));
        configs.add(new Config("easy_3", 100, 100, 10, 0.7, 0.05, 5, true, selectType.tournament, crossType.OX, easy3));
        configs.add(new Config("medium_4", 200, 200, 10, 0.7, 0.05, 10, true, selectType.tournament, crossType.OX, medium4));
        configs.add(new Config("medium_0", 200, 200, 10, 0.7, 0.05, 10, true, selectType.tournament, crossType.OX, medium0));
        configs.add(new Config("hard_0", 300, 300, 10, 0.7, 0.05, 20, true, selectType.tournament, crossType.OX, hard0));
        configs.add(new Config("hard_2", 300, 300, 10, 0.7, 0.05, 20, true, selectType.tournament, crossType.OX, hard2));
        configs.add(new Config("hard_0", 100, 100, 10, 0.7, 0.05, 5, true, selectType.tournament, crossType.EX, hard0));
        //Comparing crossover probability [0, 0.2, 0.4, 0,6, 0.8, 1.0]
        double[] PXs = new double[]{0.0, 0.2, 0.4, 0.6, 0.8, 1.0};
        for (double px : PXs) {
            configs.add(new Config("easy_3", 100, 100, 10, px, 0.05, 5, true, selectType.tournament, crossType.OX, easy3));
        }
        //Comparing mutation probability [0, 0.05, 0.1, 0.2, 0.5, 0.1]
        double[] PMs = new double[]{0.0, 0.05, 0.1, 0.2, 0.5, 1.0};
        for (double ps : PMs) {
            configs.add(new Config("easy_3", 100, 100, 10, 0.7, ps, 5, true, selectType.tournament, crossType.OX, easy3));
        }
        //Comparing population size [100, 200, 500, 1000, 5000]
        int[] popSizes = new int[]{100, 200, 500, 1000, 5000};
        for (int popSize : popSizes) {
            configs.add(new Config("hard_0", popSize, 100, 10, 0.7, 0.05, 5, true, selectType.tournament, crossType.OX, hard0));
        }
        //Comparing generations number
        int[] genNumbers = new int[]{100, 200, 500, 1000, 5000};
        for (int genNumber : genNumbers) {
            configs.add(new Config("hard_0", 100, genNumber, 10, 0.7, 0.05, 5, true, selectType.tournament, crossType.OX, hard0));
        }
        //Comparing generations number and population size on hard -- not done
        configs.add(new Config("hard_0", 1000, 1000, 10, 0.7, 0.05, 5, true, selectType.tournament, crossType.OX, hard0));
        //Comparing selection types tournament and roulette -- done
        configs.add(new Config("easy_3", 100, 100, 10, 0.7, 0.05, 5, true, selectType.tournament, crossType.OX, easy3));
        configs.add(new Config("east_3", 100, 100, 10, 0.7, 0.05, 5, true, selectType.roulette, crossType.OX, easy3));
        //Comparing tournament size
        int[] tourSizes = new int[]{5, 20, 50, 100};
        for (int tourSize : tourSizes) {
            configs.add(new Config("easy_3", 100, 100, 10, 0.7, 0.05, tourSize, true, selectType.tournament, crossType.OX, easy3));
        }
        //compering elite impact
        configs.add(new Config("easy_3", 100, 100, 10, 0.7, 0.05, 5, true, selectType.tournament, crossType.OX, easy3));
        configs.add(new Config("east_3", 100, 100, 10, 0.7, 0.05, 5, false, selectType.tournament, crossType.OX, easy3));
        for(Config config : configs){
            Stats stats = new Stats(config);
            stats.createTFPs(config.dataReader);
            stats.initialiseTFPs();
            stats.start();
        }
    }
}
