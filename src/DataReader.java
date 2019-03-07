
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataReader {
    private static final int MetaDataLength = 10;
    private String SRC;
    private List<String> rawData;

    public DataReader(String fileSrc){
        SRC = fileSrc;
    }

    public void readFile() throws IOException {
        rawData = Files.readAllLines(Paths.get(SRC));
    }

    public int getNumberOfCities(){
        int cityInfoLine = 2;
        return Integer.parseInt(rawData.get(cityInfoLine).replaceAll("\\D+",""));
    }

    public int getNumberOfItems(){
        int itemsInfoLine = 3;
        return Integer.parseInt(rawData.get(itemsInfoLine).replaceAll("\\D+",""));
    }

    public List<City> getCities(){
        String splitPattern = "\\s";
        int numberOfRows = 3;
        List<City> cities = new ArrayList<>();
        List<Item> items = getItems();
        for(int i = MetaDataLength; i<MetaDataLength+getNumberOfCities(); i++){
            String[] cityData = rawData.get(i).split(splitPattern,numberOfRows);
            City city = new City(
                    Integer.parseInt(cityData[0]),
                    Float.parseFloat(cityData[1]),
                    Float.parseFloat(cityData[2])
            );
            List<Item> avaliableItems = items.stream()
                    .filter((Item item) -> item.getCityIndex() == city.getIndex())
                    .collect(Collectors.toList());
            city.setAvaliableItems(avaliableItems);
            cities.add(city);
        }
        return cities;
    }

    public List<Item> getItems(){
        String splitPattern = "\\s";
        int numberOfRows = 4;
        List<Item> items = new ArrayList<>();
        for(int i = getNumberOfCities()+MetaDataLength+1; i< rawData.size(); i++){
            String[] itemData = rawData.get(i).split(splitPattern,numberOfRows);
            items.add(new Item(
                    Integer.parseInt(itemData[1]),
                    Integer.parseInt(itemData[2]),
                    Integer.parseInt(itemData[3]))
            );
        }
        return items;
    }

}
