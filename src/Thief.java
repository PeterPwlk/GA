
import java.util.*;
import java.util.stream.Collectors;

public class Thief implements Comparable<Thief>{
    private List<Integer> track;
    private int backpackWeight;
    private double score;
    private double speed;

    public Thief(List<Integer> cities){
        track = new ArrayList<>(cities);
        backpackWeight = 0;
        score = 0.0;
        speed = 0.0;
    }

    public void generateTrack(){
        Collections.shuffle(track);
        //backtrack to first city
        track.add(track.get(0));
    }

    public double getScore() {
        return score;
    }

    public List<Integer> getTrack() {
        return track;
    }

    public void pickUpItem(Item item){
        backpackWeight += item.getWeight();
    }

    public void setSpeed(double minSpeed, double maxSpeed, int backpackCapacity){
        speed = maxSpeed - (backpackWeight * ((maxSpeed - minSpeed) / backpackCapacity));
    }

    public double getTrackLength(double minSpeed, double maxSpeed,int backpackCapacity, double[][] distanceMatrix, List<Item> pickedItems){
        double trackLength;
        double trackTime = 0.0;
        for(int i = 0; i < track.size()-1; i++){
            int cityIndex = track.get(i) + 1;
            List <Item> itemsPickedInCity = pickedItems.stream()
                    .filter((Item item) -> item.getCityIndex() == cityIndex)
                    .collect(Collectors.toList());
            itemsPickedInCity.forEach(this::pickUpItem);
            setSpeed(minSpeed, maxSpeed, backpackCapacity);
            trackLength = distanceMatrix[track.get(i)][track.get(i + 1)];
            trackTime += trackLength/speed;
        }
        return trackTime;
    }

    @Override
    public int compareTo(Thief o) {
        return Double.compare(o.score, score);
    }

    public void calculateScore(double minSpeed, double maxSpeed, int backpackCapacity, double[][] distanceMatrix, List<Item> pickedItems){
        double trackTime = getTrackLength(minSpeed, maxSpeed, backpackCapacity, distanceMatrix, pickedItems);
        int itemsProfit = pickedItems.stream().mapToInt(Item::getProfit).sum();
        score = itemsProfit - trackTime;
    }

    public void mutateSwap(){
        Random random = new Random();
        int index1 = random.nextInt(track.size()-1);
        int index2 = random.nextInt(track.size()-1);
        Collections.swap(track, index1, index2);
        if(index1 == 0 || index2 == 0){
            track.remove(track.size()-1);
            track.add(track.get(0));
        }
    }
    //TODO implement Ex crossover
    private List<Integer> getEdges(int city){
        List<Integer> edges = new ArrayList<>();
        int indexOfCity = track.indexOf(city);
        if(indexOfCity == 0) {
            edges.add(track.get(indexOfCity + 1));
            edges.add(track.get(track.size()-2));
        }
        else if( indexOfCity == track.size()-1){
            edges.add(track.get(indexOfCity - 1));
            edges.add(track.get(1));
        }
        else {
            if(indexOfCity < 0 ){
                System.out.println("index error");
                return null;
            }
            edges.add(track.get(indexOfCity + 1));
            edges.add(track.get(indexOfCity - 1));
        }
        return edges;
    }

    public static int getNextCity(int selectedCity, HashMap<Integer, List<Integer>> edgeTable){
        int nextCity;
        List<Integer> selectedCityEdges = edgeTable.get(selectedCity);
        Random random = new Random();
        if(selectedCityEdges.size() == 0){
            nextCity = random.nextInt(edgeTable.size()-1);
        } else if(selectedCityEdges.size() == 1){
            nextCity = selectedCityEdges.get(0);
        } else {
            int shortestEdgeListSize = Collections.min(selectedCityEdges.stream().map(city -> edgeTable.get(city).size()).collect(Collectors.toList()));
            List<Integer> shortestEdgeListCities = new ArrayList<>();
            for (Integer city : selectedCityEdges) {
                List<Integer> edges = edgeTable.get(city);
                Set<Integer> uniqueCities = new HashSet<>(edges);
                if (uniqueCities.size() < edges.size()) {
                    return city;
                }
                if(edgeTable.get(city).size() == shortestEdgeListSize){
                    shortestEdgeListCities.add(city);
                }
            }
            int lastIndex = shortestEdgeListCities.size()-1;
            nextCity = (lastIndex==0)?shortestEdgeListCities.get(0):shortestEdgeListCities.get(random.nextInt(lastIndex));
        }
        return nextCity;
    }

    public static Thief crossOver(Thief parent1, Thief parent2){
        List<Integer> cityIndexes = new ArrayList<>(parent1.track.subList(0,parent1.track.size()-1));
        cityIndexes.sort(Integer::compareTo);
        HashMap<Integer, List<Integer>> edgeTable = new HashMap<>();
        //generate edge table
        for(Integer index : cityIndexes){
            List<Integer> parent1Edges = parent1.getEdges(index);
            List<Integer> parent2Edges = parent2.getEdges(index);
            List<Integer> edges = new ArrayList<>();
            edges.addAll(parent1Edges);
            edges.addAll(parent2Edges);
            edgeTable.put(index, edges);
        }
        Random random = new Random();
        List<Integer> childIndexes = new ArrayList<>();
        Integer firstCity = random.nextInt(cityIndexes.size()-1);
        edgeTable.forEach((key, value) -> value.removeIf(city -> city.equals(firstCity)));
        childIndexes.add(firstCity);
        while (childIndexes.size() <= edgeTable.size() - 1){
            Integer next = getNextCity(childIndexes.get(childIndexes.size()-1), edgeTable);
            if(!childIndexes.contains(next)){
                edgeTable.forEach((key, value) -> value.removeIf(city -> city.equals(next)));
                childIndexes.add(next);
            } else {
                Integer missing = getNextCity(next, edgeTable);
                if (!childIndexes.contains(missing)) {
                    edgeTable.forEach((key, value) -> value.removeIf(city -> city.equals(missing)));
                    childIndexes.add(missing);
                }
            }
        }
        //backtrack to first city
        childIndexes.add(childIndexes.get(0));
        return new Thief(childIndexes);
    }
}
