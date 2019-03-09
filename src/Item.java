public class Item implements Comparable<Item>{
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

    public double getPWRatio(){
        return (double) profit/ weight;
    }

    @Override
    public int compareTo(Item other){
        //return Integer.compare(other.weight, weight);
        int comp = Double.compare(other.getPWRatio(), getPWRatio());
        return (comp != 0)? comp : Integer.compare(weight, other.weight);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        return sb
                .append("city index: ").append(cityIndex)
                .append(" P: ").append(profit)
                .append(" W: ").append(weight)
                .append(" PWRatio: ").append(getPWRatio())
                .toString();
    }
}
