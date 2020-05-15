package dto;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Tree {

    private Person root;
    private List<Chain> chains;
    private List<Person> where_update;

    public Tree(Person root) {
        this.root = root;
        this.root.setWeight(10);
        this.root.setTree_in(this);
        chains = new ArrayList<>();
        chains.add(new Chain(root, root));
        where_update = new ArrayList<>();
        where_update.add(root);
    }

    @Override
    public String toString() {
        return "Tree{" +
                "chains=" + chains +
                '}';
    }

    public void addPersonWithHashMap(Person new_person, Person contaminated_by) {

        contaminated_by.addInfected(new_person);
        new_person.setContaminated_by(contaminated_by);
        new_person.setWeight(contaminated_by.getWeight() + 10);
        new_person.setTree_in(this);

        if (contaminated_by.getInfect().size() == 1) {
            for (Chain c : chains) {
                if (c.getEnd().equals(contaminated_by)) {
                    c.setWeight(new_person.getWeight());
                }
            }
        } else {
            chains.add(new Chain(root, new_person));
        }
    }

    public void updateChains(int actual_ts) {
        chains.clear();

//        // optimize
//        List<Person> where_update_now = new ArrayList<>(where_update);
//        for (Person p : where_update_now) {
//            p.update(actual_ts, 0, root, chains);
//        }


        this.root.update(actual_ts, 0, root, chains);

        ListIterator<Chain> iterator = chains.listIterator();
        while (iterator.hasNext()) {
            Chain c = iterator.next();
            if (c.getEnd().getWeight() == 0) {
                deleteChain(c.getEnd());
                iterator.remove();
                System.out.println("Remove chain");
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

    public Person getRoot() {
        return root;
    }

    public void setRoot(Person root) {
        this.root = root;
    }

    public List<Person> getWhere_update() {
        return where_update;
    }

    public void setWhere_update(List<Person> where_update) {
        this.where_update = where_update;
    }

}
