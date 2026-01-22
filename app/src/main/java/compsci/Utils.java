package compsci;

import java.util.Scanner;
import java.util.function.Supplier;

public class Utils {
    /**
     * Left pad a string with spaces
     * @param input the string to pad
     * @param totalLength the total length of the padded string
     * @return the padded string
    */
    public static String pad(String input, int totalLength) {
        return pad(input, totalLength, ' ');
    }
    /**
         * Left pad a string using the padding param
         * @param input the string to pad using the padding param
         * @param totalLength the total length of the padded string
         * @param padding the padding to use in the padded string
         * @return the padded string
         */
        public static String pad(String input, int totalLength, char padding) {
            if (input.length() >= totalLength) {
                return input;
            }
            return input + String.valueOf(padding).repeat(totalLength - input.length());
        }

        /**
         * Prompts the user for input and returns the input
         * @param prompt the prompt to display
         * @param scanner the scanner to use for input
         * @return the user input
         * */
    public static String prompt(String prompt, Scanner scanner) {
        return prompt(prompt, scanner, '>');
    }
    public static String prompt(String prompt, Scanner scanner, char promptChar) {
        System.out.printf("%s\n %s ", prompt, promptChar);
        String line = scanner.nextLine().trim();
        return line.isEmpty() ? "0.0" : line;
    }
    /**
     * Result class that holds either an error or data
     *
     * @param <T> the type of data
     */
        public record Result<T>(Exception error, T data) {

        public boolean hasError() {
                return error != null;
            }

            public boolean isSuccess() {
                return error == null;
            }
        }

    /**
     * Safely executes a provider function and returns a Result containing either the data or an error
     * @param provider the function to execute
     * @param <T> the return type of the provider
     * @return Result object containing either error or data
     */
    public static <T> Result<T> safe(Supplier<T> provider) {
        try {
            return new Result<>(null, provider.get());
        } catch (Exception e) {
            return new Result<>(e, null);
        }
    }
}
