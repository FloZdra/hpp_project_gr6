package dto;

import java.util.ArrayList;
import java.util.List;

public class Tree {

    private Person root;
    private List<Chain> chains;

    public Tree(Person root) {
        this.root = root;
        chains = new ArrayList<>();
    }

    public void addPerson(Person new_person) {
        if (new_person.getContaminated_by() == root.getId()) {
            root.addInfected(new_person);
        } else {
            for (Person p : root.getInfect()) {
                addPerson(new_person, p);
            }
        }
    }

    public void addPerson(Person new_person, Person current_person) {
        if (new_person.getContaminated_by() == current_person.getId()) {
            current_person.addInfected(new_person);
        } else {
            for (Person p : current_person.getInfect()) {
                addPerson(new_person, p);
            }
        }
    }

    public void update(int actual_ts) {
        this.root.update(actual_ts, 0, root, chains);
    }

}
