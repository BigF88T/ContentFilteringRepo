package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {
    private final Path path;
    private final String prefix;
    private final List<String> fileNames;
    private static Boolean append;
    private static final ArrayList<Path> clearedFiles = new ArrayList<>();
    private final StatisticCollector statisticCollector;

    public FileProcessor(ArgumentParser argumentParser, StatisticCollector statisticCollector) {
        path = argumentParser.getPath();
        prefix = argumentParser.getPrefix();
        fileNames = argumentParser.getFileNames();
        append = argumentParser.isAppend();
        this.statisticCollector = statisticCollector;
    }

    public void groupByFiles() {
        for (String fileName : fileNames) {
            try (FileReader fileReader = new FileReader(fileName);
                 BufferedReader bufferedReader = new BufferedReader(fileReader))
            {
                while (bufferedReader.ready()) {
                    dataTypeDefinition(bufferedReader.readLine());
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void dataTypeDefinition(String content) throws IOException {
        if (content.isEmpty())
            return;

        Path resultPath = Path.of("");

        if (path != null)
            resultPath = path;
        else if (prefix != null) {
            resultPath = Path.of(resultPath + prefix);
        }

        try {
            long c = Long.parseLong(content);
            statisticCollector.addNumber(c);
            writeToFile(c, resultPath);

        } catch (NumberFormatException | IOException e) {
            try {
                float c = Float.parseFloat(content);
                statisticCollector.addNumber(c);
                writeToFile(c, resultPath);

            } catch (NumberFormatException e2) {
                statisticCollector.addString(content);
                writeToFile(content, resultPath);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void writeToFile(Object c, Path resultPath) throws IOException {
        switch (c.getClass().getSimpleName()) {
            case "String" ->
                    resultPath = Path.of(resultPath + "string.txt");
            case "Long" ->
                    resultPath = Path.of(resultPath + "integer.txt");
            case "Float" ->
                    resultPath = Path.of(resultPath + "float.txt");
        }

        if(!Files.exists(resultPath)) {
            Files.createFile(resultPath);
        } else if (!append && !clearedFiles.contains(resultPath)) {
            Files.writeString(resultPath, "", StandardOpenOption.TRUNCATE_EXISTING);
            clearedFiles.add(resultPath);
        }

        Files.writeString(resultPath, String.valueOf(c), StandardOpenOption.APPEND);
        Files.writeString(resultPath, System.lineSeparator(), StandardOpenOption.APPEND);
    }
}