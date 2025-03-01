package org.example;
public class ContentFiltering {
    public static void main(String[] args) {
        ArgumentParser argumentParser = new ArgumentParser(args);
        StatisticCollector collector = new StatisticCollector(argumentParser.isFullStat());
        FileProcessor fileProcessor = new FileProcessor(argumentParser, collector);
        fileProcessor.groupByFiles();
        collector.returnStat();
    }
}