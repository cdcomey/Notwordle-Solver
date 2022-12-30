public class Word{
    private final String name;
    private final int scrabbleScore;
    private final double frequency;
    private static final int[] SCRABBLE_SCORES = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10};

    public Word(){
        name = "null";
        frequency = 0.0;
        scrabbleScore = 0;

    }
    public Word(String name, double frequency){
        this.name = name;
        this.frequency = frequency;
        scrabbleScore = scrabbleScore(name);
    }

    public String get(){ return name; }
    public double getFreq(){ return frequency; }
    public int getScrabbleScore(){ return scrabbleScore; }

    private int scrabbleScore(String s){
        int l1 = SCRABBLE_SCORES[(int)(s.charAt(0)) - 97];
        int l2 = SCRABBLE_SCORES[(int)(s.charAt(1)) - 97];
        int l3 = SCRABBLE_SCORES[(int)(s.charAt(2)) - 97];
        int l4 = SCRABBLE_SCORES[(int)(s.charAt(3)) - 97];
        int l5 = SCRABBLE_SCORES[(int)(s.charAt(4)) - 97];

        return l1+l2+l3+l4+l5;
    }

    @Override
    public boolean equals(Object o){
        if (o == this){
            return true;
        }

        if (!(o instanceof Word)){
            return false;
        }

        Word w = (Word)o;

        return name.equals(w.get());
    }

    public boolean equals(String s){
        return name.equals(s);
    }

    @Override
    public String toString(){
        return name + " " + scrabbleScore + " " + frequency;
    }

}