import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Random;

public class Solver{
    static final char[] LETTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    static final int[] SCRABBLE_SCORES = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
    
    public static void main(String[] args){
        // 'shorter list.txt' is the list of all possible correct answers for Wordle
		// it is the 'shorter' of the two text files since Wordle accepts certain words that it will never set as the correct word
		File wordFile = new File("shorter list.txt");
        
		String wordsString = "";

        // wordleInfoLists contains a list of lists
        // each list corresponds to a guess, so a new list will be added for each guess
        // each list contains all the possible words the answer could be based on that guess's wordle score
        ArrayList<ArrayList<Word>> wordleInfoLists = new ArrayList<ArrayList<Word>>();

		// read the contents from the file and store them in an array list
		try (FileInputStream fis = new FileInputStream(wordFile); BufferedInputStream bis = new BufferedInputStream(fis)){
			wordsString = new String(bis.readAllBytes());
		} catch (FileNotFoundException ex){
			System.err.println(wordFile + " not found");
		} catch (IOException ex){
			System.err.println("IO exception when attempting to read " + wordFile);
		}

        File freqFile = new File("word frequencies.txt");
        ArrayList<Word> words = new ArrayList<Word>();
        try (FileInputStream fis = new FileInputStream(freqFile); BufferedInputStream bis = new BufferedInputStream(fis)){
			String s = new String(bis.readAllBytes());
			String[] arr = s.split("\n");
			for (String each : arr){
                String name = each.substring(0, each.indexOf(" "));
                double frequency = Double.valueOf(each.substring(each.indexOf(" ")+1));
                if (wordsString.contains(name)){
                    words.add(new Word(name, frequency));
                }
            }
		} catch (FileNotFoundException ex){
			System.err.println(freqFile + " not found");
		} catch (IOException ex){
			System.err.println("IO exception");
		}
		
		// begin taking in user input
		Scanner kb = new Scanner(System.in);
		
        while (true){

            // the loops are for input protection
            // they will loop until the user puts in a valid string
            boolean acceptedInput = false;
            String guess = "";
            Word guessWord = new Word();
            while (!acceptedInput){
                System.out.print("Enter your guess: ");
                guess = kb.next();
                if (guess.equals("exit")){
                    break;
                } 
                
                for (Word word : words){
                    if (word.equals(guess)){
                        acceptedInput = true;
                        guessWord = new Word(guess, word.getFreq());
                    }
                }
                
                if (!acceptedInput) {
                    System.out.println("That is not a valid guess, try again.");
                }
            }

            if (guess.equals("exit"))
                break;
            
            // takes in the guess's scrabble score comparison
            acceptedInput = false;
            char scrabble_comp = ' ';
            while (!acceptedInput){
                System.out.print("Is your word's Scrabble score too high, too low, or equal? (enter h/l/e): ");
                scrabble_comp = kb.next().charAt(0);
                if (scrabble_comp == 'h' || scrabble_comp == 'l' || scrabble_comp == 'e'){
                    acceptedInput = true;
                } else {
                    System.out.println("That is not a valid entry, try again.");
                }
            }

            final char scrabbleComp = scrabble_comp;

            // takes in the guess's dictionary comparison
            acceptedInput = false;
            char dictionary_comp = ' ';
            while (!acceptedInput){
                System.out.print("Should you go towards A or Z? ");
                dictionary_comp = kb.next().charAt(0);
                if (dictionary_comp == 'a' || dictionary_comp == 'z'){
                    acceptedInput = true;
                } else {
                    System.out.println("That is not a valid entry, try again.");
                }
            }

            final char dictionaryComp = dictionary_comp;

            acceptedInput = false;
            char freq_comp = ' ';
            while (!acceptedInput){
                System.out.print("Is your word too common or too obscure? (enter c/o): ");
                freq_comp = kb.next().charAt(0);
                if (freq_comp == 'c' || freq_comp == 'o'){
                    acceptedInput = true;
                } else {
                    System.out.println("That is not a valid entry, try again.");
                }
            }

            final char freqComp = freq_comp;

            // takes in the guess's wordle score
            acceptedInput = false;
            String wordle_comp = "";
            while (!acceptedInput){
                System.out.print("What is your word's simplified Wordle score?\nEnter the first letter of the color followed by the number (eg g2 or y1),\nor 'x' if none match): ");
                wordle_comp = kb.next();
                if (wordle_comp.charAt(0) == 'x'){
                    acceptedInput = true;
                } else if (wordle_comp.length() == 2 && (wordle_comp.charAt(0) == 'g' || wordle_comp.charAt(0) == 'y')
                    && (wordle_comp.charAt(1) == '1' || wordle_comp.charAt(1) == '2' || wordle_comp.charAt(1) == '3'
                    || wordle_comp.charAt(1) == '4')){
                        acceptedInput = true;
                    } else {
                        System.out.println("That is not a valid entry, try again.");
                    }
            }

            // now words will be eliminated from the pool of potential words

            // dictionary check
            // since the list of words is sorted alphabetically, elimination is easy
            // simply return a sublist with the word after the guess as the start, or the word before the guess as the end
            if (dictionaryComp == 'a'){
                words = new ArrayList<Word>(words.subList(0, words.indexOf(guessWord)));
            } else if (dictionaryComp == 'z'){
                words = new ArrayList<Word>(words.subList(words.indexOf(guessWord)+1, words.size()));
            }

            // scrabble check
            // calculate each word's scrabble score, compare it to the guess's, and remove if it does not match scrabbleComp
            // scores are calculated on the fly instead of being stored in a lookup table due to their short calculation time
            if (scrabbleComp == 'h'){ 
                for (int i = 0; i < words.size(); i++){
                    Word word = words.get(i);
                    if (word.getScrabbleScore() >= guessWord.getScrabbleScore()){
                        words.remove(i);
                        i--;
                    }
                }
            } else if (scrabbleComp == 'l'){
                for (int i = 0; i < words.size(); i++){
                    Word word = words.get(i);
                    if (word.getScrabbleScore() <= guessWord.getScrabbleScore()){
                        words.remove(i);
                        i--;
                    }
                }
            } else if (scrabbleComp == 'e'){
                for (int i = 0; i < words.size(); i++){
                    Word word = words.get(i);
                    if (word.getScrabbleScore() != guessWord.getScrabbleScore()){
                        words.remove(i);
                        i--;
                    }
                }
            }

            // frequency check
            // similar implementation to scrabble check
            if (freqComp == 'c'){
                double guessFreq = guessWord.getFreq();
                for (int i = 0; i < words.size(); i++){
                    Word word = words.get(i);

                    // if the word is more common than the guess, and the guess is already too common, the word must be too common as well
                    if (word.getFreq() >= guessFreq){
                        // System.out.println("eliminating " + word + " for being too common");
                        words.remove(i);
                        i--;
                    }
                }
            } else if (freqComp == 'o'){
                double guessFreq = guessWord.getFreq();
                for (int i = 0; i < words.size(); i++){
                    Word word = words.get(i);

                    // if the word is more obscure than the guess, and the guess is already too obscure, the word must be too obscure as well
                    if (word.getFreq() <= guessFreq){
                        // System.out.println("eliminating " + word + " for being too obscure");
                        words.remove(i);
                        i--;
                    }
                }
            }

            // wordle check
            ArrayList<Word> wordleInfo = new ArrayList<Word>();
            for (Word word : words){
                if (wordleEliminator(guessWord, word, wordle_comp)){
                    wordleInfo.add(word);
                }
            }

            wordleInfoLists.add(wordleInfo);

            for (int i = 0; i < words.size(); i++){
                for (ArrayList<Word> list : wordleInfoLists){
                    if (!list.contains(words.get(i))){
                        words.remove(i);
                        i--;
                    }
                }
            }

            for (Word each : words){
                System.out.println(each);
            }
            System.out.println(words.size() + " possible words");
        }
    }

    // evaluates whether the guess is possible given its wordle score
    static boolean wordleEliminator(Word guessIn, Word wordIn, String score){
        // System.out.println("checking " + word);
        String guess = guessIn.get();
        String word = wordIn.get();
        switch (score.charAt(0)){
            case 'g':
                boolean[] checks;
                boolean check;
                int counter;
                String doubleGuess, doubleWord;
                switch (score.charAt(1)){
                    // this tells us there is only one letter in the right spot
                    // therefore, we want to eliminate both words with no letters in the right spot
                    // and words with more than one letter in the right spot
                    case '1':
                        // System.out.println("case g1");
                        doubleGuess = guess + guess;
                        doubleWord = word + word;
                        checks = new boolean[5];
                        for (int i = 0; i < guess.length(); i++){
                            checks[i] = doubleGuess.charAt(i) == doubleWord.charAt(i)
                             && doubleGuess.charAt(i+1) != doubleWord.charAt(i+1)
                             && doubleGuess.charAt(i+2) != doubleWord.charAt(i+2)
                             && doubleGuess.charAt(i+3) != doubleWord.charAt(i+3)
                             && doubleGuess.charAt(i+4) != doubleWord.charAt(i+4);
                        }

                        check = checks[0];
                        for (int i = 1; i < checks.length; i++){
                            check = check || checks[i];
                        }
                        return check;

                    case '2':
                        checks = new boolean[10];
                        counter = 0;
                        for (int i = 0; i < guess.length()-1; i++){
                            for (int j = i+1; j < guess.length(); j++){
                                boolean hCheck = false;
                                for (int h = 0; h < i; h++){
                                    hCheck = hCheck || guess.charAt(h) == word.charAt(h);
                                }

                                boolean iCheck = guess.charAt(i) == word.charAt(i);

                                boolean kCheck = false;
                                for (int k = i+1; k < j; k++){
                                    kCheck = kCheck || guess.charAt(k) == word.charAt(k);
                                }

                                boolean jCheck = guess.charAt(j) == word.charAt(j);

                                boolean lCheck = false;
                                for (int l = j+1; l < guess.length(); l++){
                                    lCheck = lCheck || guess.charAt(l) == word.charAt(l);
                                }

                                checks[counter] = !hCheck && iCheck && !kCheck && jCheck && !lCheck;
                                counter++;
                            }
                        }

                        check = checks[0];
                        for (int i = 1; i < checks.length; i++){
                            check = check || checks[i];
                        }
                        return check;

                    case '3':
                        checks = new boolean[10];
                        counter = 0;
                        for (int i = 0; i < guess.length()-2; i++){
                            for (int j = i+1; j < guess.length()-1; j++){
                                for (int k = j+1; k < guess.length(); k++){
                                    boolean hCheck = false;
                                    for (int h = 0; h < i; h++){
                                        hCheck = hCheck || guess.charAt(h) == word.charAt(h);
                                    }

                                    boolean iCheck = guess.charAt(i) == word.charAt(i);
    
                                    boolean lCheck = false;
                                    for (int l = i+1; l < j; l++){
                                        lCheck = lCheck || guess.charAt(l) == word.charAt(l);
                                    }

                                    boolean jCheck = guess.charAt(j) == word.charAt(j);

                                    boolean mCheck = false;
                                    for (int m = j+1; m < k; m++){
                                        mCheck = mCheck || guess.charAt(m) == word.charAt(m);
                                    }

                                    boolean kCheck = guess.charAt(k) == word.charAt(k);

                                    boolean nCheck = false;
                                    for (int n = k+1; n < guess.length(); n++){
                                        nCheck = nCheck || guess.charAt(n) == word.charAt(n);
                                    }

                                    checks[counter] = !hCheck && iCheck && !lCheck && jCheck && !mCheck && kCheck && !nCheck;
                                    counter++;
                                }
                            }
                        }

                        check = checks[0];
                        for (int i = 1; i < checks.length; i++){
                            check = check || checks[i];
                        }
                        return check;

                    case '4':
                        doubleGuess = guess + guess;
                        doubleWord = word + word;
                        checks = new boolean[5];
                        for (int i = 0; i < guess.length(); i++){
                            checks[i] = doubleGuess.charAt(i) != doubleWord.charAt(i)
                            && doubleGuess.charAt(i+1) == doubleWord.charAt(i+1)
                            && doubleGuess.charAt(i+2) == doubleWord.charAt(i+2)
                            && doubleGuess.charAt(i+3) == doubleWord.charAt(i+3)
                            && doubleGuess.charAt(i+4) == doubleWord.charAt(i+4);
                        }

                        check = checks[0];
                        for (int i = 1; i < checks.length; i++){
                            check = check || checks[i];
                        }
                        return check;

                    default:
                        break;
                }
            
            case 'y':
                // if the score is y1, it tells us a few things
                // 1: 4 letters are not in the word
                // 2: the 1 letter that is is not in the right place
                switch (score.charAt(1)){
                    case '1':
                        // System.out.println("case y1");
                        doubleGuess = guess + guess;
                        doubleWord = word + word;
                        checks = new boolean[5];
                        for (int i = 0; i < guess.length(); i++){
                            checks[i] = doubleWord.indexOf(doubleGuess.charAt(i)) >= 0
                            && doubleWord.charAt(i) != doubleGuess.charAt(i)
                            && doubleWord.indexOf(doubleGuess.charAt(i+1)) < 0
                            && doubleWord.indexOf(doubleGuess.charAt(i+2)) < 0
                            && doubleWord.indexOf(doubleGuess.charAt(i+3)) < 0
                            && doubleWord.indexOf(doubleGuess.charAt(i+4)) < 0;
                        }

                        check = checks[0];
                        for (int i = 1; i < checks.length; i++){
                            check = check || checks[i];
                        }

                        // System.out.println((check ? "accepted " : "rejected ") + word);
                        // System.out.println(checks[0] + " " + checks[1] + " " + checks[2] + " " + checks[3] + " " + checks[4]);
                        return check;

                    case '2':
                        // System.out.println("in case y2");
                        checks = new boolean[10];
                        counter = 0;
                        for (int i = 0; i < guess.length()-1; i++){
                            for (int j = i+1; j < guess.length(); j++){
                                boolean hCheck = false;
                                for (int h = 0; h < i; h++){
                                    hCheck = hCheck || word.indexOf(guess.charAt(h)) >= 0;
                                }

                                boolean iCheck = word.indexOf(guess.charAt(i)) >= 0 && guess.charAt(i) != word.charAt(i);

                                boolean kCheck = false;
                                for (int k = i+1; k < j; k++){
                                    kCheck = kCheck || word.indexOf(guess.charAt(k)) >= 0;
                                }

                                boolean jCheck = word.indexOf(guess.charAt(j)) >= 0 && guess.charAt(j) != word.charAt(j);

                                boolean lCheck = false;
                                for (int l = j+1; l < guess.length(); l++){
                                    lCheck = lCheck || word.indexOf(guess.charAt(l)) >= 0;
                                }

                                checks[counter] = !hCheck && iCheck && !kCheck && jCheck && !lCheck;
                                counter++;
                            }
                        }

                        check = checks[0];
                        for (int i = 1; i < checks.length; i++){
                            check = check || checks[i];
                        }
                        return check;

                    case '3':
                        checks = new boolean[10];
                        counter = 0;
                        for (int i = 0; i < guess.length()-2; i++){
                            for (int j = i+1; j < guess.length()-1; j++){
                                for (int k = j+1; k < guess.length(); k++){
                                    boolean hCheck = false;
                                    for (int h = 0; h < i; h++){
                                        hCheck = hCheck || word.indexOf(guess.charAt(h)) >= 0;
                                    }

                                    boolean iCheck = word.indexOf(guess.charAt(i)) >= 0 && guess.charAt(i) != word.charAt(i);

                                    boolean lCheck = false;
                                    for (int l = i+1; l < j; l++){
                                        lCheck = lCheck || word.indexOf(guess.charAt(l)) >= 0;
                                    }

                                    boolean jCheck = word.indexOf(guess.charAt(j)) >= 0 && guess.charAt(j) != word.charAt(j);

                                    boolean mCheck = false;
                                    for (int m = j+1; m < k; m++){
                                        mCheck = mCheck || word.indexOf(guess.charAt(m)) >= 0;
                                    }

                                    boolean kCheck = word.indexOf(guess.charAt(k)) >= 0 && guess.charAt(k) != word.charAt(k);

                                    boolean nCheck = false;
                                    for (int n = k+1; n < guess.length(); n++){
                                        nCheck = nCheck || word.indexOf(guess.charAt(n)) >= 0;
                                    }

                                    checks[counter] = !hCheck && iCheck && !lCheck && jCheck && !mCheck && kCheck && !nCheck;
                                    counter++;
                                }
                            }
                        }

                        check = checks[0];
                        for (int i = 1; i < checks.length; i++){
                            check = check || checks[i];
                        }
                        return check;

                    case '4':
                        doubleGuess = guess + guess;
                        doubleWord = word + word;
                        checks = new boolean[5];
                        for (int i = 0; i < guess.length(); i++){
                            checks[i] = doubleWord.indexOf(doubleGuess.charAt(i)) < 0
                            && doubleWord.indexOf(doubleGuess.charAt(i+1)) >= 0
                            && doubleWord.charAt(i+1) != doubleGuess.charAt(i+1)
                            && doubleWord.indexOf(doubleGuess.charAt(i+2)) >= 0
                            && doubleWord.charAt(i+2) != doubleGuess.charAt(i+2)
                            && doubleWord.indexOf(doubleGuess.charAt(i+3)) >= 0
                            && doubleWord.charAt(i+3) != doubleGuess.charAt(i+3)
                            && doubleWord.indexOf(doubleGuess.charAt(i+4)) >= 0
                            && doubleWord.charAt(i+4) != doubleGuess.charAt(i+4);
                        }

                        check = checks[0];
                        for (int i = 1; i < checks.length; i++){
                            check = check || checks[i];
                        }
                        return check;

                    default:
                        break;
                }
            
            case 'x':
                for (char each : guess.toCharArray()){
                    if (word.indexOf(each) >= 0){
                        return false;
                    }
                }

                return true;

            default:
                System.out.println("error: Wordle score is not 'g', 'y', or 'x'");
                return false;
        }
    }
}