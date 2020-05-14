import dto.Chain;
import dto.Person;
import dto.Tree;

import java.util.*;

public class Main {
    public static void main(String[] args) {

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

//        // UNIT TEST 4
//        List<Person> people = new ArrayList<>();
//        people.add(new Person(1, 1584489600, -1));
//        people.add(new Person(2, 1584576000, 1));
//        people.add(new Person(3, 1584662400, -1));
//        people.add(new Person(4, 1584748800, -1));
//        people.add(new Person(5, 1585267200, 3));
//        people.add(new Person(6, 1585440000, 4));
//        people.add(new Person(7, 1586390400, 5));
//        people.add(new Person(8, 1586908800, 6));

        // UNIT TEST 9
        List<Person> people = new ArrayList<>();
        people.add(new Person(1, 1583071200, -1));
        people.add(new Person(2, 1583676000, 1));
        people.add(new Person(3, 1583679600, 2));
        people.add(new Person(4, 1584280800, 3));
        people.add(new Person(5, 1584284400, 4));
        people.add(new Person(6, 1585501200, 4));


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

        System.out.println("\nFin du programme");
    }
}
