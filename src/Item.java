public class Item {
    private int profit;
    private int weight;
    private int cityIndex;

    public Item(int profit, int weight, int cityIndex){
        this.profit = profit;
        this.weight = weight;
        this.cityIndex = cityIndex;
    }

    public int getProfit() {
        return profit;
    }

    public int getWeight() {
        return weight;
    }

    public int getCityIndex() {
        return cityIndex;
    }
}
