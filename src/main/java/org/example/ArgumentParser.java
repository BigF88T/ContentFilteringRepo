package org.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ArgumentParser {
    private final List<String> fileNames = new ArrayList<>();
    private boolean append = false;
    private boolean fullStat = false;
    private String prefix;
    private Path path;

    public ArgumentParser(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String param = args[i];

            if (param.startsWith("-")) {
                switch (param) {
                    case "-o":
                        path = Path.of(args[i + 1]);
                        i++;
                        break;
                    case "-p":
                        prefix = args[i + 1];
                        i++;
                        break;
                    case "-a":
                        append = true;
                        break;
                    case "-s":
                        fullStat = false;
                        break;
                    case "-f":
                        fullStat = true;
                        break;
                }
            } else if (isValidFile(param)) {
                fileNames.add(param);
            }
        }

        if (fileNames.isEmpty()) {
            System.err.println("Не найдено ни одного указанного файла. Проверьте правильность указания пути файла, либо их наличие.");
        }
    }

    private boolean isValidFile(String filename) {

        return Files.isRegularFile(Path.of(filename));
    }

    public List<String> getFileNames() {
        return fileNames;
    }

    public boolean isAppend() {
        return append;
    }

    public boolean isFullStat() {
        return fullStat;
    }

    public String getPrefix() {
        return prefix;
    }

    public Path getPath() {
        return path;
    }

}
