package org.example;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class ContentFiltering {

    private static boolean append = false;
    private static boolean fullStat;
    private static String prefix;
    private static Path path;
    private static ArrayList<Path> clearedFiles = new ArrayList<>();
    private static int countOfStrings = 0;
    private static double maxNum = Double.MIN_VALUE;
    private static double minNum = Double.MAX_VALUE;
    private static double sumNum = 0.0;
    private static int countOfNum = 0;
    private static int maxStringLength = 0;
    private static int minStringLength = Integer.MAX_VALUE;

    public static void main(String[] args) {

        List<String> fileNames = new ArrayList<>();
        List<String> parametrs = new ArrayList<>();
        for(int i = 0; i < args.length; i++) {
            String param = args[i];

            if (param.startsWith("-")){
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
            } else if (Files.isRegularFile(Path.of(param))){
                fileNames.add(param);
            }
        }

        if (fileNames.isEmpty()){
            System.out.println("Не найдено ни одно из указанных файлов. Проверьте правильность указания пути файла, либо их наличие.");
        }

        for (String fileName : fileNames) {
            try (FileReader fileReader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader))
            {
                while (bufferedReader.ready()) {
                    groupByFiles(bufferedReader.readLine());
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        returnStat();
    }

    private static void groupByFiles (String content) throws IOException {
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
            sumNum += c;
            if (maxNum < c)
                maxNum = c;
            else if (minNum > c)
                minNum = c;
            countOfNum++;
            writeInFile(c, resultPath);

        } catch (NumberFormatException | IOException e) {
            try {
                float c = Float.parseFloat(content );
                sumNum += c;
                if (maxNum < c)
                    maxNum = c;
                if (minNum > c)
                    minNum = c;
                countOfNum++;
                writeInFile(c, resultPath);

            } catch (NumberFormatException e2) {

                countOfStrings++;
                if (maxStringLength < content.length())
                    maxStringLength = content.length();
                if (minStringLength > content.length() )
                    minStringLength = content.length();

                writeInFile(content, resultPath);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void writeInFile(Object c, Path resultPath) throws IOException {
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

    private static void returnStat() {
        if (minNum == Double.MAX_VALUE)
            minNum = 0.0;
        else if (maxNum == Double.MIN_VALUE)
            maxNum = 0.0;

        if(fullStat){
            System.out.println("Количество элементов, записанных в исходящие файлы: " + (countOfNum + countOfStrings));
            System.out.println("Минимальное число: " + BigDecimal.valueOf(minNum).floatValue());
            System.out.println("Максимальное число: " + BigDecimal.valueOf(maxNum).floatValue());
            System.out.println("Сумма чисел: " + BigDecimal.valueOf(sumNum).floatValue());
            System.out.println("Среднее значение: " + (Double.isNaN(sumNum / countOfNum) ? 0 : sumNum / countOfNum));
            System.out.println("Количество строк: " + countOfStrings);
            System.out.println("Размер самой короткой строки: " + minStringLength);
            System.out.println("Размер самой длинной строки: " + maxStringLength);
        } else
            System.out.println("Количество элементов, записанных в исходящие файлы: " + countOfNum + countOfStrings);
    }
}