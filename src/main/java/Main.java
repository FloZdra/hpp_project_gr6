import dto.Person;
import dto.Tree;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {


        List<Person> people = new ArrayList<>();
        people.add(new Person(1, 1, 10));
        people.add(new Person(1, 1, 10));
        people.add(new Person(1, 1, 10));
        people.add(new Person(1, 1, 10));
        people.add(new Person(1, 1, 10));
        people.add(new Person(1, 1, 10));
        people.add(new Person(1, 1, 10));
        people.add(new Person(1, 1, 10));


        List<Tree> trees = new ArrayList<>();

        for (Person p : people) {
            if (p.getContaminated_by() == -1) {
                trees.add(new Tree(p));
            } else {
                for (Tree t : trees) {
                    t.addPerson(p);
                }
            }
        }

        for (Tree t : trees) {
            t.update(people.get(people.size() - 1).getDiagnosed_ts());
        }

        System.out.println("Test");
    }
}
