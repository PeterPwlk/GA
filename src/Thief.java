
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Thief {
    private List<Integer> track;
    private int backpackWeight;
    private double speed;

    public Thief(List<Integer> cities){
        track = new ArrayList<>(cities);
        backpackWeight = 0;
        speed = 0;
    }

    public void generateTrack(){
        Collections.shuffle(track);
        //backtrack to first city
        track.add(track.get(0));
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

    //TODO get track length from distance matrix
    public double getTrackLength(double minSpeed, double maxSpeed,int backpackCapacity, double[][] distanceMatrix, List<Item> pickedItems){
        double trackLength;
        double trackTime = 0.0;
        for(int i = 0; i < track.size()-1; i++){
            int cityIndex = track.get(i) + 1;
            List <Item> itemsPickedInCity = pickedItems.stream()
                    .filter((Item item) -> item.getCityIndex() == cityIndex)
                    .collect(Collectors.toList());
            //itemsPickedInCity.forEach(System.out::println);
            itemsPickedInCity.forEach(this::pickUpItem);
            setSpeed(minSpeed, maxSpeed, backpackCapacity);
            trackLength = distanceMatrix[track.get(i)][track.get(i + 1)];
            trackTime += trackLength/speed;
        }
        return trackTime;
    }

    public double getScore(double minSpeed, double maxSpeed,int backpackCapacity, double[][] distanceMatrix, List<Item> pickedItems){
        double trackTime = getTrackLength(minSpeed, maxSpeed, backpackCapacity, distanceMatrix, pickedItems);
        int itemsProfit = pickedItems.stream().mapToInt(Item::getProfit).sum();
        //System.out.println(itemsProfit + " " + trackTime);
        return itemsProfit - trackTime;
    }
}
