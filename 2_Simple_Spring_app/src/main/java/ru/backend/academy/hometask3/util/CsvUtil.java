package ru.backend.academy.hometask3.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.backend.academy.hometask3.exception.CsvDatabaseException;
import ru.backend.academy.hometask3.exception.ProductNotFoundException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CsvUtil {

    private final File dataFile;

    public List<String[]> readAllLines() {
        List<String[]> lines;
        try (CSVReader csvReader = new CSVReader(new FileReader(dataFile))) {
            lines = csvReader.readAll();
            return lines;
        } catch (IOException e) {
            log.error("CSV file reading error ", e);
            throw new CsvDatabaseException();
        }
    }

    public void writeAllLines(List<String[]>  lines, boolean append) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(dataFile, append))) {
            csvWriter.writeAll(lines);
            csvWriter.flush();
        } catch (IOException e) {
            log.error("CSV file writing error", e);
            throw new CsvDatabaseException();
        }
    }

    public boolean lineExists(String id) {
        try (CSVReader csvReader = new CSVReader(new FileReader(dataFile))) {
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length > 0 && nextLine[0].equals(id)) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            log.error("CSV file reading error", e);
            throw new CsvDatabaseException();
        }
    }

    public List<String[]> deleteLine(String id) {
        List<String[]> lines = readAllLines();
        boolean found = false;
        String[] lineToRemove = null;
        for (String[] line : lines) {
            if (id.equals(line[0])) {
                found = true;
                lineToRemove = line;
                break;
            }
        }
        lines.remove(lineToRemove);
        if (!found) {
            log.info("Product with this item number is not found {}", id);
            throw new ProductNotFoundException(id);
        }
        return lines;
    }

}
