package dto;

import java.util.*;

public class Tree {

    private Person root;
    private List<Chain> chains;
    private List<Person> where_update;

    private List<Person> peopleToAdd;

    private Chain[] top_chains;
    private int top_chain_weight;
    private int potential_top_chain_weight;
    private int last_update;

    public Tree(Person root) {
        this.root = root;
        this.root.setWeight(10);
        this.root.setTree_in(this);
        this.root.setIn_the_tree(true);

        top_chains = new Chain[3];
        chains = new ArrayList<>();
        top_chains[0] = new Chain(root, root);
        chains.add(top_chains[0]);

        top_chain_weight = 10;
        potential_top_chain_weight = 10;

        where_update = new ArrayList<>();
        where_update.add(root);
        peopleToAdd = new ArrayList<>();
        last_update = root.getDiagnosed_ts();
    }

    @Override
    public String toString() {
        return "Tree{" +
                "chains=" + chains +
                '}';
    }

    public void addPersonToTree(Person new_person, Person contaminated_by) {

        contaminated_by.addInfected(new_person);
        new_person.setContaminated_by(contaminated_by);
        new_person.setWeight(contaminated_by.getWeight() + 10);
        new_person.setTree_in(this);
        new_person.setIn_the_tree(true);

        if (top_chain_weight < new_person.getWeight()) {
            top_chain_weight = new_person.getWeight();
            potential_top_chain_weight = new_person.getWeight();
        }

        if (contaminated_by.getInfect().size() == 1) {
            for (Chain c : chains) {
                if (c.getEnd().equals(contaminated_by)) {
                    c.setWeight(new_person.getWeight());
                    c.setEnd(new_person);
                    if (!c.equals(top_chains[0]) && !c.equals(top_chains[1]) && !c.equals(top_chains[2])) {
                        updateTopChains(c);
                    } else {
                        sortTopChains();
                    }
                }
            }
        } else {
            Chain c = new Chain(root, new_person);
            chains.add(c);
            updateTopChains(c);
        }
    }

    public void updateTree(int actual_ts) {

        chains.clear();
        top_chains = new Chain[3];

        // Update only where it is needed
        List<Person> where_update_now = new ArrayList<>(where_update);
        where_update.clear();
        for (Person p : where_update_now) {
            p.update(actual_ts, 0, root, chains, true);
        }

        top_chain_weight = 0;

        // Remove chains with weight = 0 and get top chain weight
        ListIterator<Chain> iterator = chains.listIterator();
        while (iterator.hasNext()) {
            Chain c = iterator.next();
            top_chain_weight = Integer.max(top_chain_weight, c.getWeight());
            if (c.getEnd().getWeight() == 0) {
                deleteChain(c.getEnd());
                iterator.remove();
            } else {
                updateTopChains(c);
            }
        }

        potential_top_chain_weight = top_chain_weight;
        last_update = actual_ts;
    }

    // Recursive method (Bottom to top)
    public void deleteChain(Person p) {
        if (p.getInfect().isEmpty()) {
            if (!p.equals(root)) {
                p.getContaminated_by().getInfect().remove(p);
                deleteChain(p.getContaminated_by());
            } else {
                this.root = null;
            }
        }
    }

    public void deleteTree() {
        for (Person p : where_update) {
            for (Person infect : p.getInfect()) {
                PeopleHashMap.removePersonFromMap(infect);
            }
            PeopleHashMap.removePersonFromMap(p);
        }
    }


    public void addPersonToWaiting(Person p) {
        p.setTree_in(this);
        p.setIn_the_tree(false);

        peopleToAdd.add(p);
        potential_top_chain_weight += 10;
        last_update = p.getDiagnosed_ts();
    }

    public int getWeightOfChainEndingWith(Person end) {
        for (Chain c : chains) {
            if (c.getEnd().equals(end)) {
                return c.getWeight();
            }
        }
        return 0;
    }

    public void updateTopChains(Chain c) {
        if (top_chains[0] == null || c.compareTo(top_chains[0]) > 0) {
            top_chains[2] = top_chains[1];
            top_chains[1] = top_chains[0];
            top_chains[0] = c;
        } else if (top_chains[1] == null || c.compareTo(top_chains[1]) > 0) {
            top_chains[2] = top_chains[1];
            top_chains[1] = c;
        } else if (top_chains[2] == null || c.compareTo(top_chains[2]) > 0) {
            top_chains[2] = c;
        }
    }


    private void sortTopChains() {
        if (top_chains[1] != null) {
            if (top_chains[1].compareTo(top_chains[0]) > 0) {
                Chain temp = top_chains[0];
                top_chains[0] = top_chains[1];
                top_chains[1] = temp;
            }
            if(top_chains[2] != null) {
                if (top_chains[2].compareTo(top_chains[1]) > 0) {
                    Chain temp = top_chains[1];
                    top_chains[1] = top_chains[2];
                    top_chains[2] = temp;
                }
            }
        }
    }

    // Generated method

    public List<Chain> getChains() {
        return chains;
    }

    public void setChains(List<Chain> chains) {
        this.chains = chains;
    }

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

    public List<Person> getPeopleToAdd() {
        return peopleToAdd;
    }

    public int getPotential_top_chain_weight() {
        return potential_top_chain_weight;
    }

    public int getLast_update() {
        return last_update;
    }

    public Chain[] getTop_chains() {
        return top_chains;
    }
}
