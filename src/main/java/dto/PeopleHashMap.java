package dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PeopleHashMap {

    private static final HashMap<Integer, Person> hashMap = new HashMap<Integer, Person>();

    public static List<Long> update_time = new ArrayList<>();
    public static List<Long> add_person_to_hashmap = new ArrayList<>();
    public static List<Long> add_person_to_tree = new ArrayList<>();
    public static List<Long> get_chains = new ArrayList<>();
    public static List<Long> writing = new ArrayList<>();

    public static int nb_update = 0, iteration = 0;


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