import java.util.*;
import java.util.stream.Collectors;

public class TFP implements Runnable{
    private String SRC;
    private String name;
    private int gen;
    private int pop_size;
    private double Px;
    private double Pm;
    private int tour;
    private boolean pickElite;
    private double minScore;
    private Main.crossType CROSSOVER_TYPE;
    private Main.selectType SELECT_TYPE;
    private Thief thiefAlpha;
    private DataReader dataReader;
    private List<Item> pickedItems;
    private List<Thief> population;
    private Stats group;

    public TFP(DataReader dataReader,String name, int pop_size, int gen, double Px, double Pm, int tour, Main.crossType crossType, Main.selectType selectType, boolean pickElite, Stats stats){
        //SRC = src;
        this.name = name;
        this.pop_size = pop_size;
        this.gen = gen;
        this.Px = Px;
        this.Pm = Pm;
        this.tour = tour;
        this.pickElite = pickElite;
        minScore = 0.0;
        this.dataReader = dataReader;
        pickedItems = new ArrayList<>();
        population = new ArrayList<>();
        CROSSOVER_TYPE = crossType;
        SELECT_TYPE = selectType;
        group = stats;
    }

    public void setup(){
//        try {
//            dataReader.loadData();
//            selectItems();
//        } catch (IOException e) {
//            System.out.println("Error during setup");
//        }
        selectItems();
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

    private void addStatsToGroup(int genNumber,double best, double worst, double avg){
        group.putBest(genNumber, best);
        group.putWorst(genNumber, worst);
        group.putAvg(genNumber, avg);
    }

    private String evaluate(int genNumber){
        population.forEach(thief ->
                thief.calculateScore
                (
                    0.1,
                    1.0,
                    dataReader.getBackpackCapacity(),
                    dataReader.getDistanceMatrix(),
                    pickedItems
                )
        );

        Thief bestThief = Collections.min(population, Thief::compareTo);
        Thief worstThief = Collections.max(population, Thief::compareTo);
        double best_score = bestThief.getScore();
        double worst_score = worstThief.getScore();
        double avg_score = population.stream().mapToDouble(Thief::getScore).average().orElse(0.0);
        addStatsToGroup(genNumber,best_score,worst_score,avg_score);
        if(thiefAlpha == null) thiefAlpha = bestThief;
        if(best_score > thiefAlpha.getScore()) thiefAlpha = bestThief;
        if(minScore == 0.0) minScore = Math.abs(worst_score) + 10;
        if(Math.abs(worst_score) > minScore) minScore = Math.abs(worst_score) + 10;
        return genNumber + "," + best_score + "," + worst_score + "," + avg_score;
    }

    private void mutate(){
        for(Thief thief : population){
            if(Double.compare(Math.random(), Pm) <= 0){
                thief.mutateSwap();
            }
        }
    }

    private List<Thief> pickElite(){
        List<Thief> pop = new ArrayList<>(population);
        pop.sort(Thief::compareTo);
        List<Thief> elite = pop.subList(0,4);
        return elite.stream().map(thief -> new Thief(thief.getTrack())).collect(Collectors.toList());
    }

    private void reproduce(){
        List<Thief> newPopulation = new ArrayList<>();
        if(pickElite)
            newPopulation.addAll(pickElite());
        while (newPopulation.size() <= population.size()){
            Thief parent1 = null;
            Thief parent2 = null;
            if(SELECT_TYPE == Main.selectType.tournament){
                parent1 = tournament();
                parent2 = tournament();
            }
            if(SELECT_TYPE == Main.selectType.roulette){
                parent1 = roulette();
                parent2 = roulette();
            }
            if(Double.compare(Math.random(), Px) <= 0 || newPopulation.size() == population.size()-1){
                Thief child = null;
                if(CROSSOVER_TYPE == Main.crossType.OX)
                    child = Thief.crossOverOx(parent1,parent2);
                if(CROSSOVER_TYPE == Main.crossType.EX) 
                    child = Thief.crossOverEx(parent1, parent2);
                newPopulation.add(child);
            } else {
                newPopulation.add(new Thief(parent1.getTrack()));
                newPopulation.add(new Thief(parent2.getTrack()));
            }
        }
        population = newPopulation;
    }

    private Thief tournament(){
        Random random = new Random();
        Set<Thief> participants = new HashSet<>();
        for (int i = 0; i < tour; i++) {
            participants.add(population.get(random.nextInt(pop_size-1)));
        }
        return Collections.min(participants, Thief::compareTo);
    }

    private Thief roulette(){
        List<Double> scores = population.stream().mapToDouble(thief -> thief.getScore() + minScore).boxed().collect(Collectors.toList());
        double totalScore = scores.stream().reduce((score,acc) -> acc += score).orElse(0.0);
        List<Double> weights = scores.stream().mapToDouble(score -> score/totalScore).boxed().collect(Collectors.toList());
        //double totalWeigth = weights.stream().reduce((weight,acc) -> acc += weight).orElse(0.0);
        Random random = new Random();
        double value = random.nextDouble(); //* totalWeigth;
        int index = 0;
        while (value > 0){
            value -= weights.get(index);
            index++;
        }
        if(index >= population.size()) index = population.size()-1;
        return population.get(index);
    }

    public void tfpStart(){

            String stats = evaluate(0);
//            System.out.println(stats);
            for(int i = 1; i< gen; i++){
                reproduce();
                mutate();
                stats = evaluate(i);
//                if(i%10 == 0)
//                    System.out.println(name + " " + stats);
            }
            group.putThiefAlpha(thiefAlpha);
            //System.out.println(thiefAlpha.getTrack() + " " + thiefAlpha.getScore());
    }

    @Override
    public void run() {
        tfpStart();
    }
}
