import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TFP {
    private String SRC;
    private int gen;
    private int pop_size;
    private double Px;
    private double Pm;
    private int tour;
    private DataReader dataReader;
    private List<Item> pickedItems;
    private List<Thief> population;

    public TFP(String src, int pop_size, int gen, double Px, double Pm, int tour){
        SRC = src;
        this.pop_size = pop_size;
        this.gen = gen;
        this.Px = Px;
        this.Pm = Pm;
        this.tour = tour;
        dataReader = new DataReader(src);
        pickedItems = new ArrayList<>();
        population = new ArrayList<>();
    }

    public void setup(){
        try {
            dataReader.loadData();
            selectItems();
        } catch (IOException e) {
            System.out.println("Error during setup");
        }
    }

    public void selectItems(){
        int capacity = dataReader.getBackpackCapacity();
        int backpack = 0;
        List<Item> items = new ArrayList<>(dataReader.getItems());
        List<Item> pickedItems = new ArrayList<>();
        items.sort(Item::compareTo);
        int index = 0;
        while (capacity >= backpack + items.get(index).getWeight()){
            pickedItems.add(items.get(index));
            backpack += items.get(index).getWeight();
            index += 1;
        }

        this.pickedItems = pickedItems;
    }

    public void printPickedItems(){
        int totalProfit = pickedItems.stream().mapToInt(Item::getProfit).sum();
        int totalWeight = pickedItems.stream().mapToInt(Item::getWeight).sum();
        int capacity = dataReader.getBackpackCapacity();
        pickedItems.forEach(System.out::println);
        String stats = "cap: " + capacity + " totalWeight: " + totalWeight + " totalProfit: " + totalProfit;
        System.out.println(stats);
    }

    public void initialise(){
        List<Integer> citiesIndexes = dataReader.getCitiesIndexes();
        for(int i = 0; i < pop_size; i++){
            population.add(new Thief(citiesIndexes));
            population.get(i).generateTrack();
        }
    }

    private String evaluate(int genNumber){
        population.forEach(thief -> thief.calculateScore(
                    0.1,
                    1.0,
                    dataReader.getBackpackCapacity(),
                    dataReader.getDistanceMatrix(),
                    pickedItems)
        );
        double best_score = Collections.min(population, Thief::compareTo).getScore();
        double worst_score = Collections.max(population, Thief::compareTo).getScore();
        double avg_score = population.stream().mapToDouble(Thief::getScore).average().orElse(0.0);
        return genNumber + "," + best_score + "," + worst_score + "," + avg_score;
    }

    private void mutate(){
        for(Thief thief : population){
            if(Double.compare(Math.random(), Pm) <= 0){
//                System.out.println("mutation!");
//                System.out.println(thief.getTrack());
                thief.mutateSwap();
//                System.out.println(thief.getTrack());
            }
        }
    }

    private void reproduce(){
        List<Thief> newPopulation = new ArrayList<>();
        while (newPopulation.size() <= population.size()){
            Thief parent1 = tournament();
            Thief parent2 = tournament();
            if(Double.compare(Math.random(), Px) <= 0){
                //System.out.println("new thief");
                //System.out.println(parent1.getTrack());
                //System.out.println(parent1.getTrack());
                Thief child = Thief.crossOver(parent1,parent2);
                //System.out.println(child.getTrack());
                newPopulation.add(child);
            } else {
                //System.out.println("parents");
                newPopulation.add(new Thief(parent1.getTrack()));
                newPopulation.add(new Thief(parent2.getTrack()));
            }
        }
        //newPopulation.forEach(thief -> System.out.println(thief.getTrack()));
        population = newPopulation;
    }

    private Thief tournament(){
        Random random = new Random();
        List<Thief> participants = new ArrayList<>();
        for (int i = 0; i < tour; i++) {
            participants.add(population.get(random.nextInt(pop_size-1)));
        }
        //participants.forEach(thief -> System.out.println(thief.getScore()));
        //System.out.println(Collections.min(participants, Thief::compareTo).getScore());
        return Collections.min(participants, Thief::compareTo);
    }

    public void start(){
//        for (Thief thief: population) {
//            double score = thief.getScore(0.1,
//                    1.0,
//                    dataReader.getBackpackCapacity(),
//                    dataReader.getDistanceMatrix(),
//                    pickedItems);
//            //System.out.println(thief.getTrack().toString());
//            System.out.println(score);
//        }

        //Thief thief = population.get(0);
        //mutate test
//        System.out.println(thief.getTrack().toString());
//        thief.mutateSwap();
//        System.out.println(thief.getTrack().toString());

        //cross test
        //Thief.crossOver(population.get(0), population.get(0));

        //tournament test
        String fileName = "stats.csv";
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));

            String stats = evaluate(0);
            System.out.println(stats);
            writer.write(stats);
            writer.newLine();
            for(int i = 1; i<= gen; i++){

                reproduce();
                mutate();
                stats = evaluate(i);
                writer.write(stats);
                writer.newLine();
                if(i%10 == 0){
                    System.out.println(stats);
                }
            }
            writer.close();
        } catch (IOException e){
            System.out.println("error opening file");
        }


    }

}
