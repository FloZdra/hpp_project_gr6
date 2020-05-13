package dto;

public class Chain implements Comparable<Chain> {

    private Person root;
    private Person end;
    private int weight;

    // TO DO

    public Chain(Person root, Person end) {
        this.root = root;
        this.end = end;
        this.weight = end.getWeight();
    }


    // Generated functions

    @Override
    public String toString() {
        return "Country, " + root.getId() + ", " + this.weight + "; ";
//        return "Chain{" +
//                "root=" + root.getId() +
//                ", end=" + end.getId() +
//                ", weight=" + weight +
//                '}';
    }

    @Override
    public int compareTo(Chain other) {
        if (this.weight != other.weight)
            return Integer.compare(this.weight, other.weight);
        else
            return Integer.compare(other.end.getDiagnosed_ts(), this.end.getDiagnosed_ts());
    }

    public Person getRoot() {
        return root;
    }

    public void setRoot(Person root) {
        this.root = root;
    }

    public Person getEnd() {
        return end;
    }

    public void setEnd(Person end) {
        this.end = end;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
