import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;

import javax.management.loading.MLetContent;

import java.util.Random;

public class Solver{
    static final char[] LETTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    static final int[] SCRABBLE_SCORES = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};
    
    public static void main(String[] args){
        // 'shorter list.txt' is the list of all possible correct answers for Wordle
		// it is the 'shorter' of the two text files since Wordle accepts certain words that it will never set as the correct word
		File file = new File("shorter list.txt");
        
		ArrayList<String> words = new ArrayList<String>();

        // wordleInfoLists contains a list of lists
        // each list corresponds to a guess, so a new list will be added for each guess
        // each list contains all the possible words the answer could be based on that guess's wordle score
        ArrayList<ArrayList<String>> wordleInfoLists = new ArrayList<ArrayList<String>>();

		// read the contents from the file and store them in an array list
		try (FileInputStream fis = new FileInputStream(file); BufferedInputStream bis = new BufferedInputStream(fis)){
			String s = new String(bis.readAllBytes());
			String[] arr = s.split("\n");
			for (String each : arr)
				words.add(each);
		} catch (FileNotFoundException ex){
			System.err.println("file not found");
		} catch (IOException ex){
			System.err.println("IO exception");
		}
		
		// begin taking in user input
		Scanner kb = new Scanner(System.in);
		
        while (true){
            System.out.print("Enter your guess: ");
            String guess = kb.next();

            if (guess.equals("exit"))
                break;
            
            System.out.print("Is your word's Scrabble score too high, too low, or equal? (enter h/l/e): ");
            final char scrabble_comp = kb.next().charAt(0);

            System.out.print("Should you go towards A or Z? ");
            final char dictionary_comp = kb.next().charAt(0);

            System.out.print("What is your word's simplified Wordle score? Enter the first letter of the color followed by the number (eg g2 or y1), or 'x' if none match): ");
            final String wordle_comp = kb.next();

            // dictionary check
            if (dictionary_comp == 'a'){
                words = new ArrayList<String>(words.subList(0, words.indexOf(guess)));
            } else if (dictionary_comp == 'z'){
                words = new ArrayList<String>(words.subList(words.indexOf(guess)+1, words.size()));
            }

            // scrabble check
            if (scrabble_comp == 'h'){ 
                for (int i = 0; i < words.size(); i++){
                    String word = words.get(i);
                    if (ScrabbleScore(word) >= ScrabbleScore(guess)){
                        words.remove(i);
                        i--;
                    }
                }
            } else if (scrabble_comp == 'l'){
                for (int i = 0; i < words.size(); i++){
                    String word = words.get(i);
                    if (ScrabbleScore(word) <= ScrabbleScore(guess)){
                        words.remove(i);
                        i--;
                    }
                }
            } else if (scrabble_comp == 'e'){
                for (int i = 0; i < words.size(); i++){
                    String word = words.get(i);
                    if (ScrabbleScore(word) != ScrabbleScore(guess)){
                        words.remove(i);
                        i--;
                    }
                }
            }

            // wordle check
            ArrayList<String> wordleInfo = new ArrayList<String>();
            for (String word : words){
                if (wordleEliminator(guess, word, wordle_comp)){
                    wordleInfo.add(word);
                }
            }

            wordleInfoLists.add(wordleInfo);

            for (int i = 0; i < words.size(); i++){
                for (ArrayList<String> list : wordleInfoLists){
                    if (!list.contains(words.get(i))){
                        words.remove(i);
                        i--;
                    }
                }
            }

            for (String each : words){
                System.out.println(each + " " + ScrabbleScore(each));
            }
            System.out.println(words.size() + " possible words");
        }
    }

    static int ScrabbleScore(String s){
        int l1 = SCRABBLE_SCORES[(int)(s.charAt(0)) - 97];
        int l2 = SCRABBLE_SCORES[(int)(s.charAt(1)) - 97];
        int l3 = SCRABBLE_SCORES[(int)(s.charAt(2)) - 97];
        int l4 = SCRABBLE_SCORES[(int)(s.charAt(3)) - 97];
        int l5 = SCRABBLE_SCORES[(int)(s.charAt(4)) - 97];

        return l1+l2+l3+l4+l5;
    }

    // evaluates whether the guess is possible given its wordle score
    static boolean wordleEliminator(String guess, String word, String score){
        // System.out.println("checking " + word);
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