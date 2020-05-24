package utils;

import dto.Person;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private static FileReader fileReader;
    private static final Parser parser = new Parser();
    private static final URL testFile = ParserTest.class.getResource("/parserTest/testFileReader.csv");

    @BeforeAll
    static void setUp() {
        fileReader = new FileReader();
        fileReader.openFile(testFile);
    }

    @Test
    @DisplayName("Read one person from testFile")
    void parseLineTest() {
        Person person = new Person("Test", 8, 1578494160, 4);
        String line = fileReader.readLine();
        Person read_person = parser.parseLine("Test", line);
        assertEquals(person, read_person);
    }
}