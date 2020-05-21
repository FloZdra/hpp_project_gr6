package dto;

public class Chain implements Comparable<Chain> {

    private Person root;
    private Person end;
    private int weight;

    public Chain(Person root, Person end) {
        this.root = root;
        this.end = end;
        this.weight = end.getWeight();
    }

    // Generated functions

    @Override
    public String toString() {
        return root.getCountry() + ", " + root.getId() + ", " + this.weight + "; ";
    }

    @Override
    public int compareTo(Chain other) {
        // If the weights are different, we compare in order to find the most heavy,
        // otherwise they are equals, we compare the diagnosed time to find the oldest (so we invert the parameters)
        if (this.weight != other.weight)
            return Integer.compare(this.weight, other.weight);
        else
            return Integer.compare(other.root.getDiagnosed_ts(), this.root.getDiagnosed_ts());
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
