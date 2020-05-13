package utils;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileReaderTest {
    private static FileReader fileReader;

    @BeforeAll
    public static void setup() {
        fileReader = new FileReader();
    }

    @Test
    @Order(1)
    @DisplayName("Verify that file can open")
    public void testOpenFile() {
        fileReader.openFile(getClass().getResource("/data/20/France.csv"));
    }

    @Test
    @Order(2)
    @DisplayName("Verify that we can read something")
    public void testReadFile() {
        try {
            String line = fileReader.readLine();
            System.out.println(line);
            assertNotNull(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}