package org.example.dataprocessor;

import org.example.dataprocessor.enums.AnalysisType;
import org.example.dataprocessor.enums.CleaningType;
import org.example.dataprocessor.enums.OutputType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class DataProcessorService {

    public double process(
            CleaningType cleaningType,
            AnalysisType analysisType,
            OutputType outputType,
            List<Integer> data) throws Exception {

        List<Integer> copy = new ArrayList<>(data);
        List<Integer> cleaned = cleanData(cleaningType, copy);
        double result = analyzeData(analysisType, cleaned);
        outputResult(outputType, result);
        return result;
    }

    private List<Integer> cleanData(CleaningType cleaningType, List<Integer> data) {
        List<Integer> cleaned = new ArrayList<>();

        switch (cleaningType) {
            case REMOVE_NEGATIVES:
                for (int num : data) {
                    switch (num >= 0 ? 1 : 0) {
                        case 1: cleaned.add(num); break;
                        default: break;
                    }
                }
                break;

            case REPLACE_NEGATIVES_WITH_ZERO:
                for (int num : data) {
                    switch (num >= 0 ? 1 : 0) {
                        case 1: cleaned.add(num); break;
                        case 0: cleaned.add(0); break;
                    }
                }
                break;
        }

        return cleaned;
    }

    private double analyzeData(AnalysisType analysisType, List<Integer> data) {
        switch (analysisType) {
            case MEAN:
                switch (data.isEmpty() ? 0 : 1) {
                    case 0: return Double.NaN;
                    case 1: return data.stream().mapToDouble(Integer::doubleValue).average().orElse(Double.NaN);
                }

            case MEDIAN:
                switch (data.isEmpty() ? 0 : 1) {
                    case 0: return Double.NaN;
                    case 1:
                        List<Integer> sorted = new ArrayList<>(data);
                        Collections.sort(sorted);
                        int n = sorted.size();
                        return n % 2 == 1
                                ? sorted.get(n / 2)
                                : (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
                }

            case STD_DEV:
                switch (data.isEmpty() ? 0 : 1) {
                    case 0: return Double.NaN;
                    case 1:
                        double mean = data.stream().mapToDouble(Integer::doubleValue).average().orElse(Double.NaN);
                        double variance = data.stream()
                                .mapToDouble(x -> Math.pow(x - mean, 2))
                                .sum() / data.size();
                        return Math.sqrt(variance);
                }

            case P90_NEAREST_RANK:
                switch (data.isEmpty() ? 0 : 1) {
                    case 0: return Double.NaN;
                    case 1:
                        List<Integer> sorted90 = new ArrayList<>(data);
                        Collections.sort(sorted90);
                        int rank = (int) Math.ceil(0.90 * sorted90.size());
                        return sorted90.get(rank - 1);
                }

            case TOP3_FREQUENT_COUNT_SUM:
                switch (data.isEmpty() ? 0 : 1) {
                    case 0: return 0.0;
                    case 1:
                        Map<Integer, Integer> freq = new HashMap<>();
                        for (int num : data) {
                            freq.put(num, freq.getOrDefault(num, 0) + 1);
                        }
                        List<Integer> counts = new ArrayList<>(freq.values());
                        counts.sort(Collections.reverseOrder());
                        return counts.stream().limit(3).mapToDouble(Integer::doubleValue).sum();
                }

            default:
                return Double.NaN;
        }
    }

    private void outputResult(OutputType outputType, double result) throws IOException {
        String text = "Result = " + result;

        switch (outputType) {
            case CONSOLE:
                System.out.println(text);
                break;

            case TEXT_FILE:
                Path path = Paths.get("target/result.txt");
                Files.createDirectories(path.getParent());
                Files.write(path, text.getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                break;
        }
    }
}
