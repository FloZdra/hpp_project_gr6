package utils;

import org.junit.jupiter.api.*;

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
        String line = fileReader.readLine();
        assertNotNull(line);
    }

    @Test
    @Order(3)
    @DisplayName("Verify that file is closed")
    public void testCloseFile() {
        fileReader.closeFile();
        String expectedMessage = "Stream closed";
        Exception exception = assertThrows(IOException.class, () -> fileReader.getInputStream().available());
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}