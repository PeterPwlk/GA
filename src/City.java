import java.util.ArrayList;
import java.util.List;

public class City {
    private int index;
    private float X;
    private float Y;
    private List<Item> avaliableItems;

    public City(int index, float x, float y){
        this.index = index;
        X = x;
        Y = y;
        avaliableItems = new ArrayList<>();
    }

    public void setAvaliableItems(List<Item> avaliableItems) {
        this.avaliableItems = avaliableItems;
    }

    public float getX() {
        return X;
    }

    public float getY() {
        return Y;
    }

    public int getIndex() {
        return index;
    }

    public List<Item> getAvaliableItems() {
        return avaliableItems;
    }

    public double getDistance(City destination){
        return Math.sqrt(Math.pow(destination.X - this.X, 2) + Math.pow(destination.Y - this.Y,2));
    }
}
