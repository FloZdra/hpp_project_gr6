package utils;

import dto.Person;

/**
 * Transform a string to a Person
 * help from : https://www.baeldung.com/java-read-lines-large-file
 */
public class Parser {
    private static final String COMMA_DELIMITER = ",";

    public Person parseLine(String country, String line) {
        String[] values = line.split(COMMA_DELIMITER);
        String person_id = values[0];
        String timestamp = values[4]
                .split("\\.")[0] // Get only the entire part of string
                .substring(1); // Remove first blank space
        String contaminated_by = values[5].substring(1); // Remove first blank space

        return new Person(
                country,
                Integer.parseInt(person_id),
                Integer.parseInt(timestamp),
                contaminated_by.equals("unknown") ? -1 : Integer.parseInt(contaminated_by)
        );
    }
}
