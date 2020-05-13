import dto.Chain;
import dto.Person;
import dto.Tree;

import java.util.*;

public class Main {
    public static void main(String[] args) {

//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1584540000, -1));
//        people.add(new Person(3, 1584558000, 1));
//        people.add(new Person(2, 1584712800, -1));
//        people.add(new Person(4, 1585324800, -1));
//        people.add(new Person(7, 1585357200, 3));

//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1583071200, -1));
//        people.add(new Person(2, 1583676000, 1));
//        people.add(new Person(3, 1583679600, 2));
//        people.add(new Person(4, 1584280800, 3));
//        people.add(new Person(5, 1584284400, 4));
//        people.add(new Person(6, 1584889201, 4));

//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1584493200, -1));
//        people.add(new Person(2, 1584579600, 1));
//        people.add(new Person(3, 1585962000, 1));

//        // UNIT TEST 2
//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1584540000, -1));
//        people.add(new Person(3, 1584558000, 1));
//        people.add(new Person(2, 1584712800, -1));
//        people.add(new Person(4, 1585324800, -1));
//        people.add(new Person(7, 1585357200, 1));

//        // UNIT TEST 3
//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1584540000, -1));
//        people.add(new Person(2, 1584540000, -1));

        // UNIT TEST 4
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, 1584493200, -1));
        people.add(new Person(3, 1584558000, 1));
        people.add(new Person(2, 1584712800, -1));
        people.add(new Person(6, 1584752400, -1));
        people.add(new Person(4, 1585324800, 3));
        people.add(new Person(8, 1585490400, 6));
        people.add(new Person(9, 1586394000, 8));
        people.add(new Person(10, 1586912400, 9));


        List<Tree> trees = new ArrayList<>();
        List<Chain> global_chains = new ArrayList<>();

        for (Person p : people) {
            // Clear all the chains
            global_chains.clear();

            // Recalculate every score of every chain
            ListIterator<Tree> iterator = trees.listIterator();
            while (iterator.hasNext()) {
                Tree t = iterator.next();
                t.updateChains(p.getDiagnosed_ts());
                if (t.getRoot() == null)
                    iterator.remove();
            }

            // If the person is contaminated by someone unknown
            if (p.getContaminated_by_id() == -1) {
                trees.add(new Tree(p));
            } else {
                // If we found the person who contaminated the new person
                boolean added = false;
                for (Tree t : trees) {
                    if (t.addPerson(p, null)) {
                        added = true;
                        break;
                    }
                }
                // If we didn't find him, we create a new tree where the person will be the root
                if (!added) {
                    trees.add(new Tree(p));
                }
            }


            for (Tree t : trees) {
                global_chains.addAll(t.getChains());
            }


            Collections.sort(global_chains, Collections.reverseOrder());
            for (Chain c : global_chains) System.out.print(c.toString());
            System.out.print('\n');
        }

        System.out.println("Pas olive");
    }
}
