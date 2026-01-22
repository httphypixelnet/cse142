package compsci.labs;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GuessingGame {
    int bestGame = 0;
    Scanner sc = new Scanner(System.in);
    AtomicInteger victories = new AtomicInteger(0);
    boolean init = true;
    Random rand = new Random();
    int correct = -1;
    AtomicInteger guesses = new AtomicInteger(0);
    int totalGuesses = 0;
    final int MAX;
    final int MIN;
    private int guess(Scanner sc) {
        try {
            System.out.print("Your guess?\n > ");
            // nextInt() doesn't skip bad tokens after they match the int pattern
            // therefore we need to use nextLine and manually parse to flush the user's input if it's a decimal
            // ts pmo
            String line = sc.nextLine();
            if (Objects.equals(line, "exit") | Objects.equals(line, "quit")) {
                return -1;
            }
            int next = Integer.parseInt(line);
            if (next < MIN || next > MAX) {
                throw new InputMismatchException();
            }

            return next;
        }
        catch (NumberFormatException | InputMismatchException e) {
                System.out.println("Invalid input! Try entering a number between 1 and 100.");
                return guess(sc);
        }
    }
    private boolean playAgain(Scanner sc) {
        System.out.print("Would you like to play again?\n > ");
        String ans = sc.nextLine();
        return List.of("y", "yes", "sure").contains(ans.toLowerCase());
    }
    public GuessingGame() {
        this.MIN = 1;
        this.MAX = 100;
    }
    public GuessingGame(int min, int max) {
        this.MIN = min;
        this.MAX = max;
    }
    public void run() {
        while (true) {
            if (init) {
                correct = rand.nextInt(MAX - MIN) + MIN;
                init = false;
                System.out.printf("I'm thinking of a number between %s and %s...%n", MIN, MAX);
            }
            guesses.incrementAndGet();
            int userGuess = guess(sc);
            if (userGuess == -1) {
                //exit
                totalGuesses = guesses.get();
                break;
            }
            if (userGuess == correct) {
                int g = guesses.get();
                totalGuesses += g;
                if (bestGame == 0 || g < bestGame) {
                    bestGame = g;
                }
                System.out.printf("You guessed correctly in %s guess%s!%n", g, guesses.getAndSet(0) == 1 ? "" : "s");
                victories.incrementAndGet();
                init = true;
                if (!playAgain(sc)) {
                    break;
                }

            }
            else if (userGuess > correct) {
                System.out.println("It's lower.");
            }
            else {
                System.out.println("It's higher.");
            }
        }
        System.out.println("Thanks for playing!");
        System.out.printf(
                "Overall stats: %n" +
                "Wins: %s%n" +
                "Guesses: %s%n" +
                "Guesses/Game: %s%n" +
                "Best game: %s%n", victories.get(), totalGuesses, Math.round(totalGuesses*100/victories.doubleValue())/100, bestGame);
    }
    public static void main(String[] args) {
        new GuessingGame().run();
    }
}
