import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * @author Josue Nava - Jimenez
 * @version 0.2.2
 * @Since 1/29/26
 *
 * File name - Jotto.java
 * Class - CST 338: Software Design
 * Assignment - HW01: Jotto
 * Description: basic Jotto word guessing game that reads 5-letter words from a text file
 *              calculates scores based on user guesses, keeps track of played words & guesses i think
 **/

public class Jotto {

    private static final int WORD_SIZE = 5;
    private String currentWord;
    private int score;

    private String filename;
    private ArrayList<String> playGuesses = new ArrayList<>();
    private ArrayList<String> playWords = new ArrayList<>();
    private ArrayList<String> wordList = new ArrayList<>();

    private static final boolean DEBUG = true;

    public Jotto(String filename) throws FileNotFoundException {
        this.filename = filename;
        readWords();
    }

    public ArrayList<String> readWords() throws FileNotFoundException {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String word = scanner.nextLine();

                if (!wordList.contains(word)) {
                    wordList.add(word);
                }
            }

            scanner.close();
        }

        catch (IOException e) {
            System.out.println("Couldn't open " + filename);
        }

        return wordList;
    }

    public void play() { // yay!!!
        Scanner scanner = new Scanner(System.in);

        score = 0;
        boolean isPlaying = true;

        System.out.println("Welcome to the game.");

        while (isPlaying) {
            System.out.println("Current Score: " + score);
            System.out.println("=-=-=-=-=-=-=-=-=-=-=");
            System.out.println("Choose one of the following:");
            System.out.println("1:\t Start the game");
            System.out.println("2:\t See the word list");
            System.out.println("3:\t See the chosen words");
            System.out.println("4:\t Show Player guesses");
            System.out.println("zz to exit");
            System.out.println("=-=-=-=-=-=-=-=-=-=-=");
            System.out.print("What is your choice: ");

            String option = scanner.nextLine().trim().toLowerCase();

            switch(option) {
                case "1":
                case "one":
                    if (pickWord()) {
                        score += guess();
                        System.out.println("Your score is " + score);
                    }
                    else {
                        showPlayerGuesses();
                    }
                    break;

                case "2":
                case "two":
                    System.out.println(showWordList());
                    break;

                case "3":
                case "three":
                    System.out.println(showPlayedWords());
                    break;

                case "4":
                case "four":
                    showPlayerGuesses();
                    break;

                case "zz":
                    System.out.println("Final score: " + score);
                    System.out.println("Thank you for playing");
                    isPlaying = false;
                    break;

                default:
                    System.out.println("I don't know what \"" + option + "\" is.");
            }

            if (isPlaying) {
                System.out.println("Press enter to continue");
                scanner.nextLine();
            }
        }
    }

    public String showPlayedWords() {
        if (playWords.isEmpty()) {
            return "No words have been played.";
        }

        StringBuffer sb = new StringBuffer("Current list of played words:\n");

        for (String word : playWords) {
            sb.append(word).append("\n");
        }

        return sb.toString();
    }

    public String showWordList() {

        if (wordList.isEmpty()) {
            System.out.println("Current word list:\n");
            return "Current word list:\n";
        }

        StringBuffer sb = new StringBuffer("Current word list:\n");

        for (String word : wordList) {
            sb.append(word).append("\n");
        }

        return sb.toString();
    }

    public ArrayList<String> showPlayerGuesses() {
        if (playGuesses.isEmpty()) {
            System.out.println("No guesses yet");
        }
        else {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Current player guesses:");

            for (String guesses : playGuesses) {
                System.out.println(guesses);
            }

            System.out.println("Would you like to add the words to the word list? (y/n)");
            String option = scanner.nextLine().trim().toLowerCase();

            if (option.equals("y")) {
                System.out.println("Updating word list.");
                updateWordList();
                System.out.println(showWordList());
            }
        }

        return playGuesses;
    }

    public int guess() {
        ArrayList<String> currentGuesses = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        int letterCount = 0;
        int score = WORD_SIZE + 1;
        String wordGuess;

        while (true) {
            System.out.println("Current Score: " + score);
            System.out.print("What is your guess (q to quit):");
            wordGuess = scanner.nextLine().trim().toLowerCase();

            if (wordGuess.equals("q")) {
                score = Math.min(score, 0);
                break;
            }
            if (wordGuess.length() != WORD_SIZE) {
                System.out.println("Word must be 5 characters (" + wordGuess + " is " + wordGuess.length() + ")");
                continue;
            }
            if (currentGuesses.contains(wordGuess)) {
                System.out.println(wordGuess + " was already entered.");
                continue;
            }

            addPlayerGuess(wordGuess);
            currentGuesses.add(wordGuess);

            if (wordGuess.equals(currentWord)) {
                System.out.println("DINGDINGDING!!! the word was " + currentWord);
                playerGuessScores(currentGuesses);
                return score;
            }

            letterCount = getLetterCount(wordGuess);

            if (letterCount == WORD_SIZE) {
                System.out.println("That word is an anagram!");
            }
            else {
                System.out.println(wordGuess + " has a Jotto score of " + letterCount);
            }

            playerGuessScores(currentGuesses);
            score--;
        }

        return score;
    }

    public int getLetterCount(String wordGuess) {
        int count = 0;
        ArrayList<Character> letters = new ArrayList<>();

        if (wordGuess.equalsIgnoreCase(currentWord)) {
            return 5;
        }

        for (char character : currentWord.toLowerCase().toCharArray()) {
            if (!letters.contains(character)) {
                letters.add(character);
            }
        }

        for (char character : wordGuess.toLowerCase().toCharArray()) {
            if (letters.contains(character)) {
                count++;
                letters.remove((Character) character);
            }
        }

        return count;
    }

    void updateWordList() {
        try {
            FileWriter fw = new FileWriter(filename);

            for (String guess : playGuesses) {
                if (!wordList.contains(guess)) {
                    wordList.add(guess);
                }
            }
            for (String word : wordList) {
                fw.write(word + "\n");
            }

            fw.close();
        }

        catch (IOException e) {
            System.out.println("Error updating word list.");
        }
    }

    public boolean pickWord() {
        if (playWords.size() == wordList.size()) {
            System.out.println("You've guessed them all!");
            return false;
        }

        Random random = new Random();
        currentWord = wordList.get(random.nextInt(wordList.size()));

        if (playWords.contains(currentWord)) {
            return pickWord();
        }

        playWords.add(currentWord);

        if (DEBUG) {
            System.out.println(currentWord);
        }

        return true;
    }

    public boolean addPlayerGuess(String wordGuess) {
        if (!playGuesses.contains(wordGuess)) {
            playGuesses.add(wordGuess);
            return true;
        }

        return false;
    }

    void playerGuessScores(ArrayList<String> playGuesses) {
        StringBuffer sb = new StringBuffer("Guess \t Score\n");

        for (String guess : playGuesses) {
            sb.append(guess).append(" \t ").append(getLetterCount(guess)).append("\n");
        }

        System.out.println(sb.toString());
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public ArrayList<String> getPlayedWords() {
        return playWords;
    }

    public String getCurrentWord() {
        return currentWord;
    }

}
