import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        final String SRC = "student/trivial_0.ttp";

        DataReader reader = new DataReader(SRC);

        reader.readFile();
        System.out.println();
        List<City> cities = reader.getCities();
        for(City city : cities){
            System.out.println(city.getAvaliableItems().size());
        }
        System.out.println(reader.getItems().size());

        Thief thief = new Thief(cities);
        Thief thief1 = new Thief(cities);

        thief.generateTrack();
        thief1.generateTrack();

        System.out.println(thief.getTrackLenght(1.0));
        System.out.println(thief1.getTrackLenght(1.0));

    }
}
