import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TFP {
    private String SRC;
    private DataReader dataReader;
    private List<Item> pickedItems;

    public TFP(String src){
        SRC = src;
        dataReader = new DataReader(src);
        pickedItems = new ArrayList<>();
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

    public void start(){
        List<Integer> indexes = dataReader.getCitiesIndexes();
        List<Thief> thieves = new ArrayList<>();
        Thief thief1 = new Thief(indexes);
        Thief thief2 = new Thief(indexes);
        thieves.add(thief1);
        thieves.add(thief2);
        for (Thief thief: thieves) {
            thief.generateTrack();
            double score = thief.getScore(0.1,
                    1.0,
                    dataReader.getBackpackCapacity(),
                    dataReader.getDistanceMatrix(),
                    pickedItems);
            //System.out.println(thief.getTrack().toString());
            System.out.println(score);
        }
    }

}
