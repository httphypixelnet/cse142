package compsci.labs.personalitytest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;
import compsci.Utils;

public class PersonalityTest {
    private static final int DIMENSIONS = 4;
    private static final int[] DIMENSION_PATTERN = {0, 1, 1, 2, 2, 3, 3};
    private static final char[] TYPE_A_LETTERS = {'E', 'S', 'T', 'J'};
    private static final char[] TYPE_B_LETTERS = {'I', 'N', 'F', 'P'};

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        printIntro();
        Scanner input = promptForInput(console);
        PrintStream output = promptForOutput(console);
        processPeople(input, output);
        input.close();
        output.close();
    }

    private static void printIntro() {
        System.out.println("This program processes a file of answers to the");
        System.out.println("Keirsey Temperament Sorter. It converts the");
        System.out.println("various A and B answers for each person into");
        System.out.println("a sequence of B-percentages and then into a");
        System.out.println("four-letter personality type.");
        System.out.println();
    }

    private static Scanner promptForInput(Scanner console) {
        while (true) {
            String fileName = Utils.prompt("input file name?", console).trim();
            try {
                return new Scanner(new File(fileName));
            } catch (FileNotFoundException e) {
                System.out.println("File not found. Try again.");
            }
        }
    }
    private static PrintStream promptForOutput(Scanner console) {
        while (true) {
            String fileName = Utils.prompt("output file name?", console).trim();
            try {
                return new PrintStream(new File(fileName));
            } catch (FileNotFoundException e) {
                System.out.println("Unable to write to that file. Try again.");
            }
        }
    }

    private static void processPeople(Scanner data, PrintStream output) {
        while (data.hasNextLine()) {
            String name = data.nextLine();
            if (!data.hasNextLine()) {
                break;
            }
            String responses = data.nextLine().trim();
            int[] aCounts = new int[DIMENSIONS];
            int[] bCounts = new int[DIMENSIONS];
            tallyResponses(responses, aCounts, bCounts);
            int[] bPercentages = computeBPercentages(aCounts, bCounts);
            String personality = determinePersonalityType(bPercentages);
            output.println(name + ": " + Arrays.toString(bPercentages) + " = " + personality);
        }
    }

    private static void tallyResponses(String responses, int[] aCounts, int[] bCounts) {
        for (int i = 0; i < responses.length(); i++) {
            char ch = responses.charAt(i);
            if (ch == '-') {
                continue;
            }
            char normalized = Character.toUpperCase(ch);
            int dimension = DIMENSION_PATTERN[i % DIMENSION_PATTERN.length];
            if (normalized == 'A') {
                aCounts[dimension]++;
            } else if (normalized == 'B') {
                bCounts[dimension]++;
            }
        }
    }
    private static int[] computeBPercentages(int[] aCounts, int[] bCounts) {
        int[] bPercentages = new int[DIMENSIONS];
        for (int i = 0; i < DIMENSIONS; i++) {
            int total = aCounts[i] + bCounts[i];
            int percent = total == 0 ? 0 : (int) Math.round((bCounts[i] * 100.0) / total);
            bPercentages[i] = percent;
        }
        return bPercentages;
    }

    private static String determinePersonalityType(int[] bPercentages) {
        StringBuilder type = new StringBuilder(DIMENSIONS);
        for (int i = 0; i < DIMENSIONS; i++) {
            int percent = bPercentages[i];
            if (percent > 50) {
                type.append(TYPE_B_LETTERS[i]);
            } else if (percent < 50) {
                type.append(TYPE_A_LETTERS[i]);
            } else {
                type.append('X');
            }
        }
        return type.toString();
    }
}
