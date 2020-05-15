package dto;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Tree {

    private Person root;
    private List<Chain> chains;

    public Tree(Person root) {
        this.root = root;
        this.root.setWeight(10);
        chains = new ArrayList<>();
        chains.add(new Chain(root, root));
    }

    public Tree() {
    }

    @Override
    public String toString() {
        return "Tree{" +
                "chains=" + chains +
                '}';
    }

    public boolean addPerson(Person new_person, Person current_person) {
        if (current_person == null) current_person = this.root;

        if (new_person.getContaminated_by_id() == current_person.getId()) {
            if (current_person.getWeight() == 0)
                return false;
            current_person.addInfected(new_person);
            new_person.setContaminated_by(current_person);
            new_person.setWeight(current_person.getWeight() + 10);

            if (current_person.getInfect().size() == 1) {

                for (Chain c : chains) {
                    if (c.getEnd().equals(current_person)) {
                        c.setWeight(new_person.getWeight());
                    }
                }
            } else {
                chains.add(new Chain(root, new_person));
            }
            return true;
        } else {
            for (Person p : current_person.getInfect()) {
                if (addPerson(new_person, p)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void updateChains(int actual_ts) {
        chains.clear();
        this.root.update(actual_ts, 0, root, chains);

        ListIterator<Chain> iterator = chains.listIterator();
        while (iterator.hasNext()) {
            Chain c = iterator.next();
            if (c.getEnd().getWeight() == 0) {
                deleteChain(c.getEnd());
                iterator.remove();
            }
        }
    }

    // Recursive method (Bottom to top)
    public void deleteChain(Person p) {
        if (p.getInfect().isEmpty()) {
            if (!p.equals(root)) {
                p.getContaminated_by().getInfect().remove(p);
                deleteChain(p.getContaminated_by());
            } else
                this.root = null;
        }
    }

    public List<Chain> getChains() {
        return chains;
    }

    public void setChains(List<Chain> chains) {
        this.chains = chains;
    }

    // Generated method

    public Tree clone() {
        Tree clone = new Tree();
        clone.setRoot(root.clone());
        clone.chains = new ArrayList<>();
        for (Chain c : chains) {
            clone.chains.add(c.clone());
        }
        return clone;
    }

    public Person getRoot() {
        return root;
    }

    public void setRoot(Person root) {
        this.root = root;
    }
}
