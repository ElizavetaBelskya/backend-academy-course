package ru.backend.academy.hometask4.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.backend.academy.hometask4.exception.csv.CsvDatabaseException;
import ru.backend.academy.hometask4.exception.not_found.ProductNotFoundException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CsvUtil {

    private final File dataFile;

    public synchronized List<String[]> readAllLines() {
        List<String[]> lines;
        try (CSVReader csvReader = new CSVReader(new FileReader(dataFile))) {
            lines = csvReader.readAll();
            return lines;
        } catch (IOException e) {
            log.error("CSV file reading error ", e);
            throw new CsvDatabaseException();
        }
    }

    public synchronized void writeAllLines(List<String[]> lines, boolean append) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(dataFile, append))) {
            csvWriter.writeAll(lines);
            csvWriter.flush();
        } catch (IOException e) {
            log.error("CSV file writing error", e);
            throw new CsvDatabaseException();
        }
    }

    public synchronized boolean lineExists(String id) {
        return returnLineWithUniqueColumnValue(0, id).length > 0;
    }

    public synchronized String[] returnLineWithUniqueColumnValue(int columnNum, String value) {
        try (CSVReader csvReader = new CSVReader(new FileReader(dataFile))) {
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length - 1 >= columnNum && nextLine[columnNum].equals(value)) {
                    return nextLine;
                }
            }
            return new String[0];
        } catch (IOException e) {
            log.error("CSV file reading error", e);
            throw new CsvDatabaseException();
        }
    }

    public synchronized List<String[]> findAllLinesByValue(int columnNum, String value) {
        List<String[]> lines = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(dataFile))) {
            String[] nextLine;
            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine.length - 1 >= columnNum && nextLine[columnNum].equals(value)) {
                    lines.add(nextLine);
                }
            }
        } catch (IOException e) {
            log.error("CSV file reading error", e);
            throw new CsvDatabaseException();
        }
        return lines;
    }

    public synchronized List<String[]> deleteLine(String id) {
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
