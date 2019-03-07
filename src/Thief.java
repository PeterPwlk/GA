
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Thief {
    private List<City> track;

    public Thief(List<City> cities){
        track = new ArrayList<>(cities);
    }

    public void generateTrack(){
        Collections.shuffle(track);
        //backtrack to first city
        track.add(track.get(0));

    }

    public List<City> getTrack() {
        return track;
    }

    public double getTrackLenght(double speed){
        double trackLenght = 0.0;
        for(int i = 0; i < track.size()-1; i++){
            trackLenght += track.get(i).getDistance(track.get(i+1));
        }
        return trackLenght/speed;
    }
}
