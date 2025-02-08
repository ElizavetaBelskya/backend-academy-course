package ru.backend.academy.hometask4.util;


import org.junit.jupiter.api.*;
import ru.backend.academy.hometask4.exception.csv.CsvDatabaseException;
import ru.backend.academy.hometask4.exception.not_found.ProductNotFoundException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("CsvUtil tests")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CsvUtilTest {

    private CsvUtil csvUtil;
    private Path tempDir;

    List<String[]> linesToWrite;

    @BeforeEach
    void setUp() throws IOException {
        linesToWrite = Arrays.asList(
                new String[]{"1", "Product1", "100"},
                new String[]{"2", "Product2", "200"}
        );
        tempDir = Files.createTempDirectory("csvUtilTest");
        File dataFile = Files.createFile(tempDir.resolve("test.csv")).toFile();
        csvUtil = new CsvUtil(dataFile);
    }

    @Test
    public void contains_unique_column_value_when_value_exists_then_return_true() {
        csvUtil.writeAllLines(linesToWrite, false);
        String[] result = csvUtil.returnLineWithUniqueColumnValue(0, "1");
        assertNotNull(result);
        assertArrayEquals(new String[]{"1", "Product1", "100"}, result);
    }

    @Test
    public void contains_unique_column_value_when_value_not_exists_then_return_false() {
        csvUtil.writeAllLines(linesToWrite, false);
        String[] result = csvUtil.returnLineWithUniqueColumnValue(0, "3");
        assertEquals(0, result.length);
    }

    @Test
    public void contains_unique_column_value_when_file_cannot_be_read_then_throw_csv_database_exception() {
        File unreadableFile = new File(tempDir.toFile(), "unreadable.csv");
        unreadableFile.setReadable(false);
        CsvUtil csvUtilWithUnreadableFile = new CsvUtil(unreadableFile);
        assertThrows(CsvDatabaseException.class, () -> csvUtilWithUnreadableFile.returnLineWithUniqueColumnValue(0, "1"));
    }

    @Test
    public void read_all_lines_when_file_not_empty_then_return_all_lines() throws IOException {
        try (Writer writer = Files.newBufferedWriter(tempDir.resolve("test.csv"))) {
            writer.write("1,Product1,100\n2,Product2,200\n");
        }
        List<String[]> lines = csvUtil.readAllLines();
        assertNotNull(lines);
        assertEquals(2, lines.size());
        assertEquals(3, lines.get(0).length);
    }

    @Test
    public void read_all_lines_when_file_empty_then_return_empty_list() {
        List<String[]> lines = csvUtil.readAllLines();
        assertNotNull(lines);
        assertTrue(lines.isEmpty());
    }

    @Test
    public void write_all_lines_when_lines_provided_then_write_to_csv_file() {
        csvUtil.writeAllLines(linesToWrite, false);
        List<String[]> linesRead = csvUtil.readAllLines();
        assertNotNull(linesRead);
        assertEquals(linesToWrite.size(), linesRead.size());
        for (int i = 0; i < linesToWrite.size(); i++) {
            assertArrayEquals(linesToWrite.get(i), linesRead.get(i));
        }
    }

    @Test
    public void delete_line_when_line_exists_then_delete_line() {
        csvUtil.writeAllLines(linesToWrite, false);
        List<String[]> linesAfterDelete = csvUtil.deleteLine("1");
        assertNotNull(linesAfterDelete);
        assertEquals(1, linesAfterDelete.size());
        assertNotEquals("1", linesAfterDelete.get(0)[0]);
    }

    @Test
    public void delete_line_when_line_not_exists_then_throw_product_not_found_exception() {
        csvUtil.writeAllLines(linesToWrite, false);
        assertThrows(ProductNotFoundException.class, () -> csvUtil.deleteLine("3"));
    }



}