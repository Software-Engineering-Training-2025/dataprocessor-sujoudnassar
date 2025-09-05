package org.example;

import org.example.dataprocessor.DataProcessorService;
import org.example.dataprocessor.enums.AnalysisType;
import org.example.dataprocessor.enums.CleaningType;
import org.example.dataprocessor.enums.OutputType;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        DataProcessorService service = new DataProcessorService();

        List<Integer> data = Arrays.asList(3, -1, 2, 5, -7, 2);

        // Mean
        double mean = service.process(
                CleaningType.REPLACE_NEGATIVES_WITH_ZERO,
                AnalysisType.MEAN,
                OutputType.CONSOLE,
                data
        );
        System.out.println("Returned (Mean): " + mean);

        // Median
        double median = service.process(
                CleaningType.REPLACE_NEGATIVES_WITH_ZERO,
                AnalysisType.MEDIAN,
                OutputType.CONSOLE,
                data
        );
        System.out.println("Returned (Median): " + median);

        // Std Dev
        double stddev = service.process(
                CleaningType.REPLACE_NEGATIVES_WITH_ZERO,
                AnalysisType.STD_DEV,
                OutputType.CONSOLE,
                data
        );
        System.out.println("Returned (Std Dev): " + stddev);

        // P90
        double p90 = service.process(
                CleaningType.REPLACE_NEGATIVES_WITH_ZERO,
                AnalysisType.P90_NEAREST_RANK,
                OutputType.CONSOLE,
                data
        );
        System.out.println("Returned (P90): " + p90);

        // Top3 Frequent
        double top3 = service.process(
                CleaningType.REPLACE_NEGATIVES_WITH_ZERO,
                AnalysisType.TOP3_FREQUENT_COUNT_SUM,
                OutputType.CONSOLE,
                data
        );
        System.out.println("Returned (Top3 Frequent): " + top3);
    }
}
