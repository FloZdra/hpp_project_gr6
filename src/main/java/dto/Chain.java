package dto;

public class Chain {

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
