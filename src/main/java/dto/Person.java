package dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Person implements Comparable<Person>, Cloneable {
    private String country;
    private int id;
    private int diagnosed_ts;
    private int contaminated_by_id;
    private int weight;
    private Person contaminated_by;
    private List<Person> infect;
    private Tree tree_in;
    private boolean in_the_tree;

    public Person(String country, int id, int diagnosed_ts, int contaminated_by_id) {
        this.country = country;
        this.id = id;
        this.diagnosed_ts = diagnosed_ts;
        this.contaminated_by_id = contaminated_by_id;
        contaminated_by = null;
        this.infect = new ArrayList<>();
        in_the_tree = false;
    }

    // Recursive method (Top to bottom)
    public void update(int actual_ts, int chain_weight, Person root, List<Chain> chains, boolean have_to_be_added) {
        int ts_elapsed = actual_ts - diagnosed_ts;
        weight = chain_weight;
        if (ts_elapsed <= 604800) {
            weight += 10;
        } else if (ts_elapsed <= 1209600) {
            weight += 4;
        }

        if (weight == 0) {
            PeopleHashMap.removePersonFromMap(this);
        } else if (have_to_be_added) {
            this.getTree_in().getWhere_update().add(this);
            have_to_be_added = false;
        }

        if (this.infect.isEmpty()) {
            chains.add(new Chain(root, this));
        } else {
            for (Person p : infect) {
                p.update(actual_ts, weight, root, chains, have_to_be_added);
            }
        }
    }

    public void addInfected(Person p) {
        this.infect.add(p);
    }

    // Generated functions

    @Override
    public Person clone() {
        //return super.clone();
        return new Person(this.country, this.id, this.diagnosed_ts, this.contaminated_by_id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", diagnosed_ts=" + diagnosed_ts +
                ", contaminated_by_id=" + contaminated_by_id +
                ", weight=" + weight +
                ", infect=" + infect +
                '}';
    }

    @Override
    public int compareTo(Person other) {
        return Integer.compare(this.diagnosed_ts, other.diagnosed_ts);
    }

    public String getCountry() {
        return country;
    }

    public int getId() {
        return id;
    }

    public int getDiagnosed_ts() {
        return diagnosed_ts;
    }

    public int getContaminated_by_id() {
        return contaminated_by_id;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Person getContaminated_by() {
        return contaminated_by;
    }

    public void setContaminated_by(Person contaminated_by) {
        this.contaminated_by = contaminated_by;
    }

    public List<Person> getInfect() {
        return infect;
    }

    public Tree getTree_in() {
        return tree_in;
    }

    public void setTree_in(Tree tree_in) {
        this.tree_in = tree_in;
    }

    public void setIn_the_tree(boolean b) {
        this.in_the_tree = b;
    }

    public boolean isIn_the_tree() {
        return in_the_tree;
    }
}