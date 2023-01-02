import java.util.HashMap;
import java.util.Objects;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map.Entry;

public class WordRecommender{

    private static void printArray(Word[] arr){
        for (Word each : arr){
            System.out.println(each);
        }
    }

    // helper method for quick sort
    private static void swap(Word[] arr, int i, int j){
        Word temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // returns -1, 0, or 1
    private static int compare(Word w1, Word w2, char method){
        int diffi = 0;
        double diffd = 0.0;
        // print("comparing " + w1.get() + " and " + w2.get());
        switch (method){
            case 'd':
                diffi = w1.get().compareTo(w2.get());
                // print("diff = " + (diffi / Math.abs(diffi)));
                if (diffi == 0)
                    return diffi;
                return diffi / Math.abs(diffi);

            case 's':
                diffi = w1.getScrabbleScore() - w2.getScrabbleScore();
                // print("diff = " + diffi);
                if (diffi == 0)
                    return diffi;
                return diffi / Math.abs(diffi);

            case 'f':
                diffd = w1.getFreq() - w2.getFreq();
                // print("diff = " + diffd);
                if (diffd == 0.0)
                    return 0;
                return (int)(diffd / Math.abs(diffd));

            default:
                return 0;
        }
    }

    // helper method for quick sort
    private static int partition(Word[] arr, int low, int high, char method){
        Word pivot = arr[high];
        int i = low-1;

        for (int j = low; j <= high-1; j++){
            if (compare(arr[j], pivot, method) < 0){
                i++;
                swap(arr, i, j);
            }
        }

        swap(arr, i+1, high);
        return i+1;
    }

    // quick sort, which can sort by dictionary order, scrabble score, or frequency
    private static void sortRec(Word[] arr, int low, int high, char method){
        if (low >= high){
            return;
        }

        int pi = partition(arr, low, high, method);
        sortRec(arr, low, pi-1, method);
        sortRec(arr, pi+1, high, method);
    }

    private static Word[] sort(Word[] arr, char method){
        sortRec(arr, 0, arr.length-1, method);
        return arr;
    }

    private static Word[] bubbleSort(Word[] arr, char method){
        for (int i = 0; i < arr.length-1; i++){
            for (int j = 0; j < arr.length; j++){
                if (compare(arr[i], arr[j], method) < 0){
                    // print("swapping " + arr[i].get() + " and " + arr[j].get());
                    swap(arr, i, j);
                }
            }
        }

        // print("after sorting");
        // printArray(arr);
        return arr;
    }

    public static <Word, Integer> HashSet<Word> getKeysByValue(HashMap<Word, Integer> map, Integer value) {
        HashSet<Word> keys = new HashSet<Word>();
        for (Entry<Word, Integer> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public static HashSet<Word> recommendWord(Word[] words){
        HashMap<Word, Integer> wordScores = new HashMap<Word, Integer>();
        // print("phase 1");

        Word[] dictSortedWords = bubbleSort(words, 'd');
        for (int i = 0; i < dictSortedWords.length; i++){
            int deviation = Math.abs(dictSortedWords.length/2 - i);
            // print(dictSortedWords.length/2 + " - " + i + " = " + deviation);
            wordScores.put(dictSortedWords[i], deviation);
            // print(dictSortedWords[i].get() + " " + deviation);
        }

        // System.out.println("phase 1");
        // for (Word each : wordScores.keySet()){
        //     System.out.println(each + " " + wordScores.get(each));
        // }

        Word[] scrabbleSortedWords = sort(words, 's');
        int medianScore = scrabbleSortedWords[scrabbleSortedWords.length/2].getScrabbleScore();
        for (int i = 0; i < scrabbleSortedWords.length; i++){
            if (scrabbleSortedWords[i].getScrabbleScore() != medianScore){
                wordScores.replace(scrabbleSortedWords[i], wordScores.get(scrabbleSortedWords[i]) + scrabbleSortedWords.length/2);
            }
        }

        Word[] freqSortedWords = sort(words, 'f');
        for (int i = 0; i < freqSortedWords.length; i++){
            int deviation = Math.abs(freqSortedWords.length/2 - i);
            wordScores.replace(freqSortedWords[i], wordScores.get(freqSortedWords[i]) + deviation);
        }

        print("best word is " + getKeysByValue(wordScores, Collections.min(wordScores.values())) + " with a score of " + Collections.min(wordScores.values()));
        return getKeysByValue(wordScores, Collections.min(wordScores.values()));
    }

    private static void print(String s){ System.out.println(s); }
}