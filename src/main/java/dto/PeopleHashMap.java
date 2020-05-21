package dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PeopleHashMap {

    private static final HashMap<Integer, Person> hashMap = new HashMap<Integer, Person>();

    public static void addPersonToMap(Person p) {
        hashMap.put(p.getId(), p);
    }

    public static void removePersonFromMap(Person p) {
        hashMap.remove(p.getId(), p);
    }

    public static Person getPersonWithId(int id) {
        return hashMap.get(id);
    }

}