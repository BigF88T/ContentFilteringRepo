package org.example;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class StatisticCollector {
    private final boolean fullStat;
    private Map<String, Object> numbers = new HashMap<>();
    private Map<String, Object> strings = new HashMap<>();

    public StatisticCollector(boolean fullStat) {
        this.fullStat = fullStat;
        numbers.put("count", 0);
        numbers.put("sum", 0.0);
        numbers.put("max", Double.MIN_VALUE);
        numbers.put("min", Double.MAX_VALUE);

        strings.put("count", 0);
        strings.put("max_length", 0.0);
        strings.put("min_length", Double.MAX_VALUE);
    }

    public void addString(String str) {
        int length = str.length();

        incrementMetric(strings, "count");

        setIfBigger(strings, "max_length", length);
        setIfSmaller(strings, "min_length", length);
    }

    // Метод для обработки числового значения
    public void addNumber(double num) {
        // Обновляем счетчик чисел
        incrementMetric(numbers, "count");

        // Обновляем сумму чисел
        addToMetric(numbers, "sum", num);

        // Обновляем минимальное и максимальное число
        setIfBigger(numbers, "max", num);
        setIfSmaller(numbers, "min", num);
    }

    private Map<String, Object> getFullStat() {
        Map<String, Object> result = new HashMap<>();
        result.putAll(numbers);
        result.putAll(strings);
        return result;
    }

    private int getTotalElementsCount() {
        return (Integer) numbers.get("count") + (Integer) strings.get("count");
    }

    // Увеличение счетчика данных
    private void incrementMetric(Map<String, Object> map, String key) {
        int currentValue = (Integer) map.get(key);
        map.put(key, currentValue + 1);
    }

    // Добавляет значение к текущей сумме
    private void addToMetric(Map<String, Object> map, String key, double value) {
        double currentSum = (Double) map.get(key);
        map.put(key, currentSum + value);
    }

    // Устанавливает новое значение, если оно больше текущего
    private void setIfBigger(Map<String, Object> map, String key, double value) {
        double currentValue = (Double) map.get(key);
        if (value > currentValue) {
            map.put(key, value);
        }
    }

    // Устанавливает новое значение, если оно меньше текущего
    private void setIfSmaller(Map<String, Object> map, String key, double value) {
        double currentValue = (Double) map.get(key);
        if (value < currentValue) {
            map.put(key, value);
        }
    }

    public void returnStat(){
        StringBuilder sb = new StringBuilder();
        sb.append("Количество элементов, записанных в исходящие файлы: ").append(getTotalElementsCount()).append("\n");

        if (fullStat){
            double avg = (double) numbers.get("sum") / (int) numbers.get("count");
            if ((double) numbers.get("min") == Double.MAX_VALUE)
                numbers.put("min", 0.0);
            if ((double) numbers.get("max") == Double.MIN_VALUE)
                numbers.put("max", 0.0);
            sb.append("Минимальное число: ").append(BigDecimal.valueOf((Double) numbers.get("min")).floatValue()).append("\n");
            sb.append("Максимальное число: ").append(BigDecimal.valueOf((Double) numbers.get("max")).floatValue()).append("\n");
            sb.append("Сумма числе: ").append(BigDecimal.valueOf((Double) numbers.get("sum")).floatValue()).append("\n");
            sb.append("Среднее значение: ").append(BigDecimal.valueOf(avg).floatValue()).append("\n");
            sb.append("Количество строк: ").append(strings.get("count")).append("\n");
            sb.append("Размер самой короткой строки: ").append(BigDecimal.valueOf((Double) strings.get("min_length")).intValue()).append("\n");
            sb.append("Размер самой длинной строки: ").append(BigDecimal.valueOf((Double) strings.get("max_length")).intValue()).append("\n");
        }

        System.out.println(sb);
    }
}
