package dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Person {

    private int id;
    private int diagnosed_ts;
    private int contaminated_by;
    private int weight;
    private List<Person> infect;

    public Person(int id, int diagnosed_ts, int contaminated_by) {
        this.id = id;
        this.diagnosed_ts = diagnosed_ts;
        this.contaminated_by = contaminated_by;
        this.weight = 10;
        this.infect = new ArrayList<>();
    }

    // Recursive method
    public void update(int actual_ts, int chain_weight, Person root, List<Chain> chains) {
        if (weight > 0) {
            int ts_elapsed = actual_ts - diagnosed_ts;
            weight = chain_weight;
            if (ts_elapsed <= 604800) {
                weight += 10;
            } else if (ts_elapsed <= 1209600) {
                weight += 4;
            }
        }
        if (this.infect.isEmpty()) {
            chains.add(new Chain(root, this));
        } else {
            for (Person p : infect) {
                p.update(actual_ts, weight, root, chains);
            }
        }
    }

    public void addInfected(Person p) {
        this.infect.add(p);
    }

    // Generated functions

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
                ", contaminated_by=" + contaminated_by +
                ", weight=" + weight +
                ", infect=" + infect +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiagnosed_ts() {
        return diagnosed_ts;
    }

    public void setDiagnosed_ts(int diagnosed_ts) {
        this.diagnosed_ts = diagnosed_ts;
    }

    public int getContaminated_by() {
        return contaminated_by;
    }

    public void setContaminated_by(int contaminated_by) {
        this.contaminated_by = contaminated_by;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Person> getInfect() {
        return infect;
    }

    public void setInfect(List<Person> infect) {
        this.infect = infect;
    }
}
