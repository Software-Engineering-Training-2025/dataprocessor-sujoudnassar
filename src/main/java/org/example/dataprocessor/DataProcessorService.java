package org.example.dataprocessor;

import org.example.dataprocessor.enums.AnalysisType;
import org.example.dataprocessor.enums.CleaningType;
import org.example.dataprocessor.enums.OutputType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Students ONLY implement the process(...) method below.
 *
 * Requirements:
 * - Order: Clean -> Analyze -> Output -> Return result
 * - Do NOT mutate the original input list
 * - Handle empties as specified in AnalysisType docs
 * - Output format EXACTLY: "Result = <value>"
 * - TEXT_FILE path: target/result.txt (create parent dirs, overwrite file)
 */
public class DataProcessorService {

    /**
     * Implement this method.
     */
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
                    if (num >= 0) {
                        cleaned.add(num);
                    }
                }
                break;

            case REPLACE_NEGATIVES_WITH_ZERO:
                for (int num : data) {
                    if (num < 0) {
                        cleaned.add(0);
                    } else {
                        cleaned.add(num);
                    }
                }
                break;
        }

        return cleaned;
    }


    private double analyzeData(AnalysisType analysisType, List<Integer> data) {
        if (data.isEmpty()) {
            switch (analysisType) {
                case TOP3_FREQUENT_COUNT_SUM:
                    return 0.0;
                default:
                    return Double.NaN;
            }
        }

        switch (analysisType) {
            case MEAN:
                double sum = 0;
                for (int num : data) sum += num;
                return sum / data.size();

            case MEDIAN:
                List<Integer> sorted = new ArrayList<>(data);
                Collections.sort(sorted);
                int n = sorted.size();
                if (n % 2 == 1) {
                    return sorted.get(n / 2);
                } else {
                    return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
                }

            case STD_DEV:
                double mean = analyzeData(AnalysisType.MEAN, data);
                double sumSq = 0;
                for (int num : data) {
                    sumSq += Math.pow(num - mean, 2);
                }
                return Math.sqrt(sumSq / data.size());

            case P90_NEAREST_RANK:
                List<Integer> sorted90 = new ArrayList<>(data);
                Collections.sort(sorted90);
                int rank = (int) Math.ceil(0.90 * sorted90.size());
                return sorted90.get(rank - 1);

            case TOP3_FREQUENT_COUNT_SUM:
                Map<Integer, Integer> freq = new HashMap<>();
                for (int num : data) {
                    freq.put(num, freq.getOrDefault(num, 0) + 1);
                }
                List<Integer> counts = new ArrayList<>(freq.values());
                counts.sort(Collections.reverseOrder());

                double topSum = 0;
                for (int i = 0; i < Math.min(3, counts.size()); i++) {
                    topSum += counts.get(i);
                }
                return topSum;

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

