import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DataReader {
    private static final int META_DATA_LENGTH = 10;
    private static final int CITY_INFO_LINE = 2;
    private static final int ITEMS_INFO_LINE = 3;
    private static final int BACKPACK_INFO_LINE = 4;
    private int numberOfCities;
    private int numberOfItems;
    private int backpackCapacity;
    private String SRC;
    private List<String> rawData;
    private List<City> cities;
    private List<Item> items;
    private double[][] distanceMatrix;

    public DataReader(String fileSrc){
        SRC = fileSrc;
        numberOfItems = 0;
        backpackCapacity = 0;
        numberOfCities = 0;
        cities = new ArrayList<>();
        items = new ArrayList<>();
    }

    private void readFile() throws IOException {
        rawData = Files.readAllLines(Paths.get(SRC));
    }

    public int getBackpackCapacity() {
        return backpackCapacity;
    }

    private int loadInteger(int lineNumber){
        return Integer.parseInt(rawData.get(lineNumber).replaceAll("\\D+",""));
    }

    private List<City> loadCities(){
        String splitPattern = "\\s";
        final int NUMBER_OF_ROWS = 3;
        List<City> cities = new ArrayList<>();
        for(int i = META_DATA_LENGTH; i< META_DATA_LENGTH + numberOfCities; i++){
            String[] cityData = rawData.get(i).split(splitPattern,NUMBER_OF_ROWS);
            City city = new City(
                    Integer.parseInt(cityData[0]),
                    Float.parseFloat(cityData[1]),
                    Float.parseFloat(cityData[2])
            );
//            List<Item> avaliableItems = items.stream()
//                    .filter((Item item) -> item.getCityIndex() == city.getIndex())
//                    .collect(Collectors.toList());
//            city.setAvaliableItems(avaliableItems);
            cities.add(city);
        }
        return cities;
    }

    private List<Item> loadItems(){
        String splitPattern = "\\s";
        final int NUMBER_OF_ROWS = 4;
        List<Item> items = new ArrayList<>();
        for(int i = numberOfCities + META_DATA_LENGTH +1; i< rawData.size(); i++){
            String[] itemData = rawData.get(i).split(splitPattern,NUMBER_OF_ROWS);
            items.add(new Item(
                    Integer.parseInt(itemData[1]),
                    Integer.parseInt(itemData[2]),
                    Integer.parseInt(itemData[3]))
            );
        }
        return items;
    }

    public void loadData() throws IOException{
        readFile();
        numberOfCities = loadInteger(CITY_INFO_LINE);
        numberOfItems = loadInteger(ITEMS_INFO_LINE);
        backpackCapacity = loadInteger(BACKPACK_INFO_LINE);
        items = loadItems();
        cities = loadCities();
        distanceMatrix = calculateDistanceMatrix();
    }

    public List<City> getCities() {
        return cities;
    }

    public List<Item> getItems() {
        return items;
    }

    public List<Integer> getCitiesIndexes(){
        List<Integer> indexes = new ArrayList<>();
        for(int i = 0; i < cities.size(); i++){
            indexes.add(i);
        }
        return indexes;
    }

    private double[][] calculateDistanceMatrix(){
        double[][] distanceMatrix = new double[cities.size()][cities.size()];

        for(int i=0; i < cities.size(); i++){
            for(int j = i; j < cities.size(); j++){
                double distance = cities.get(i).getDistance(cities.get(j));
                distanceMatrix[i][j] = distance;
                distanceMatrix[j][i] = distance;
            }
        }
        return distanceMatrix;
    }

    public double[][] getDistanceMatrix() {
        return distanceMatrix;
    }
}
