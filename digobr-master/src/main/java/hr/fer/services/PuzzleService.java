package hr.fer.services;

import hr.fer.common.PuzzleDifficulty;
import hr.fer.dto.PuzzleDto;
import hr.fer.dto.PuzzleTypeInfoDto;
import hr.fer.dto.Word;
import hr.fer.dto.openai.ChatGPTResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class PuzzleService {

    public static final int GRID_SIZE = 100;
    public static String[][] puzzle = new String[GRID_SIZE][GRID_SIZE];
    //1=horizontal 2=vertical
    public static int direction = 0;

    public static List<PuzzleDto> allPuzzles = new ArrayList<PuzzleDto>();

    public static int mL;
    public static int mT;

    public String createPrompt(PuzzleTypeInfoDto puzzleTypeInfo) {
        allPuzzles.clear();
        //TODO: formiraj prompt koristeci podatke iz PuzzleTypeInfoDto

        //testni prompt
        String prompt;
        if(puzzleTypeInfo.getDifficulty() == PuzzleDifficulty.EASY) {
            prompt = "When was Leonardo da Vinci born?";
        } else {
            prompt = "When did Leonardo da Vinci die?";
        }
        return prompt;
    }

    public PuzzleDto createPuzzle(ChatGPTResponse response) {
        initializeEmptyPuzzle();

        List<String> wordList = new ArrayList<>();
        List<String[][]> listOfPuzzles = new ArrayList<>();
        List<String> questions = getQuestions(response);
        List<String> answers = getAnswers(response);

        for(int i = 0; i < 1; i++) {
            initializeEmptyPuzzle();
            wordList.clear();
            generateWords(wordList, response);
            Collections.shuffle(wordList);
            listOfPuzzles.add(generatePuzzle(wordList));
        }

        String[][] bestPuzzle = evaluatePuzzles(listOfPuzzles);
        printPuzzle(bestPuzzle);

        System.out.println(mL);
        System.out.println(mT);

        formatPuzzle();
        return allPuzzles.get(0);

        //return new PuzzleDto(bestPuzzle, questions, answers);
    }

    private static void initializeEmptyPuzzle() {
        //inicijalizacija praznog polja
        for (String[] strings : puzzle) {
            Arrays.fill(strings, " ");
        }
    }

    private static List<String> getQuestions(ChatGPTResponse response) {
        //TODO: izvaditi pitanja iz response
        return new ArrayList<>();
    }

    private static List<String> getAnswers(ChatGPTResponse response) {
        //TODO: izvaditi odgovore iz response
        return new ArrayList<>();
    }

    private static void generateWords(List<String> wordList, ChatGPTResponse response) {
        //TODO: iz response izvaditi rijeci koje je chatGPT generirao i ubaciti ih u listu

        //testni skup rijeci 1
        wordList.add("Banana".toUpperCase());
        wordList.add("Suncokret".toUpperCase());
        wordList.add("Avion".toUpperCase());
        wordList.add("Knjiga".toUpperCase());
        wordList.add("Plaža".toUpperCase());
        wordList.add("Robot".toUpperCase());
        wordList.add("Planina".toUpperCase());
        wordList.add("Oblak".toUpperCase());
        wordList.add("Kava".toUpperCase());
        wordList.add("Slon".toUpperCase());
        wordList.add("More".toUpperCase());
        wordList.add("Laptop".toUpperCase());
        wordList.add("Voće".toUpperCase());
        wordList.add("Gitara".toUpperCase());
        wordList.add("Proljeće".toUpperCase());
        wordList.add("Klavir".toUpperCase());
        wordList.add("Snijeg".toUpperCase());
        wordList.add("Jagoda".toUpperCase());
        wordList.add("Balon".toUpperCase());
        wordList.add("Prijatelj".toUpperCase());

    }

    private static String[][] generatePuzzle(List<String> wordList) {
        int maxWords = wordList.size();
        String word = wordList.get(0);

        placeHorizontal(GRID_SIZE/2,GRID_SIZE/2, word);
        int count = 1;

        wordList.remove(word);

        int numberOfItterations = 0;

        PuzzleDto p = new PuzzleDto();
        List<Word> wordsInPuzzle = new ArrayList<>();
        p.puzzle = wordsInPuzzle;

        NEXT: while(count < maxWords && wordList.size()>0) {
            word = wordList.get(0);

            Word w = new Word();
            w.word = word;

            char[] listaSlova = word.toCharArray();
            for(int c = 0; c<listaSlova.length; c++) {
                for(int i = 0;i <GRID_SIZE;i++) {
                    for (int j = 0; j<GRID_SIZE; j++) {
                        if(puzzle[i][j].equals(String.valueOf(listaSlova[c]))) {
                            boolean canPLace = canPlace(word, i, j,c);
                            if(canPLace) {
                                if(direction == 1) {
                                    placeHorizontal(i, j-c, word);
                                    w.startPosition = new int[]{i, j - c};
                                    w.endPosition = new int[]{i, (j -c) + word.length()};
                                    w.vertical = false;
                                    w.desc = "TODO";
                                }
                                if(direction == 2) {
                                    placeVertical(j, i-c, word);
                                    w.startPosition = new int[]{i-c, j};
                                    w.endPosition = new int[]{i + word.length(), j -c};
                                    w.vertical = true;
                                    w.desc = "TODO";
                                }
                                count++;
                                wordList.remove(word);
                                wordsInPuzzle.add(w);
                                //resetiraj brojac iteracija za sljedecu rijec
                                numberOfItterations=0;
                                continue NEXT;
                            }
                        }
                    }
                }

            }
            wordList.remove(word);
            wordList.add(word);
            numberOfItterations++;

            //ako nakon vise iteracija ne mogu posloziti rijec u krizaljku (znaci da nema zajednickih slova) - izbaci ju iz liste
            //vjerojatno bi bilo dovoljno provjeriti samo 2 iteracije (numberOfItterations==2)
            if(numberOfItterations==5) {
                wordList.remove(word);}
        }

        String[][] generatedPuzzle = cutOut();
        allPuzzles.add(p);
        return generatedPuzzle;
    }

    //iz matrice 100*100 vraca neprazni dio matrice (samo ona polja koja sadrze pojmove)
    private static String[][] cutOut() {
        int minTop = 100;
        int minLeft = 100;
        int maxBottom = 0;
        int maxRight = 0;

        for(int i=0;i<GRID_SIZE;i++) {
            for(int j=0; j<GRID_SIZE; j++) {
                if(!puzzle[i][j].equals(" ") && j < minLeft) {
                    minLeft = j;
                    mL = minLeft;
                }
                if(!puzzle[i][j].equals(" ") && j > maxRight) {
                    maxRight = j;
                }
                if(!puzzle[i][j].equals(" ") && i < minTop) {
                    minTop = i;
                    mT=minTop;
                }
                if(!puzzle[i][j].equals(" ") && i > maxBottom) {
                    maxBottom = i;
                }
            }

        }
        int newRows = maxBottom-minTop+1;
        int newColumns = maxRight-minLeft+1;
        String[][] newPuzzle = new String[newRows][newColumns];

        for(int i = 0; i < maxBottom-minTop+1 ; i++) {
            for(int j = 0; j<maxRight-minLeft+1;j++) {
                newPuzzle[i][j] = puzzle[minTop+i][minLeft+j];
            }
        }

        return newPuzzle;
    }

    //ocjenjuje "kvalitetu" krizaljke i vraca najbolju (ocjenjuje se gustoca i "pravilna" dimenzija krizaljke)
    public static String[][] evaluatePuzzles(List<String[][]> allPuzzles) {
        double bestScore = -100.0;
        String[][] bestPuzzle = null;

        for(String[][] puzzle : allPuzzles) {
            double densityScore = calculateDensity(puzzle);
            //ocjena za kvadratnu dimenziju trenutno iskljucena - cini mi se da su bolje krizaljke bez te ocjene
            double squarenessScore = /*calculateSquareness(puzzle);*/ 0;

            //bitnije je da krizaljka bude gusca, zato tu ocjenu mnozim sa vecim faktorom
            double totalScore = (densityScore * 15.0) + (squarenessScore*5.0);
            if(totalScore > bestScore) {
                bestScore = totalScore;
                bestPuzzle = puzzle;
            }
        }
        return bestPuzzle;
    }

    public static double calculateDensity(String[][] puzzle) {
        double densityScore;
        int numOfEmptyCells = 0;
        int numOfFilledCells = 0;

        for(int i = 0; i<puzzle.length; i++) {
            for(int j = 0; j<puzzle[0].length; j++) {
                if(puzzle[i][j].equals(" ")) {
                    numOfEmptyCells++;
                } else {
                    numOfFilledCells++;
                }
            }
        }

        //TODO: provjeravaj dijeljejnje s 0 (iako je "nemoguce" da bude 0 praznih celija)

        //veci omjer popunjenih čelija u odnosu na prazne = veci broj bodova
        densityScore = (numOfFilledCells*1.0)/(numOfEmptyCells*1.0);
        return densityScore;
    }

    public static double calculateSquareness(String[][] puzzle) {
        //ako je razlika između redaka i stupaca 0 (tj. ako je križaljka kvadratna), dodijeli maksimalan broj bodova: 1.0
        return  1.0 /(1+(Math.abs(puzzle.length-puzzle[0].length)));
    }

    public static boolean canPlace(String word, int row, int column, int offset) {
        if(canPlaceHorizontal(word, row, column, offset)) {
            direction=1;
            return true;
        } else if(canPlaceVertical(word, row, column, offset)) {
            direction = 2;
            return true;
        } else {
            direction =0;
            return false;
        }
    }

    public static boolean canPlaceHorizontal(String word, int row, int column, int offset) {
        char[] listaSlova = word.toCharArray();
        int br = 0;

        //rijec ne stane u matricu
        if(column-offset+word.length()>=GRID_SIZE) {
            return false;
        }

        if(row < 0 || column<0) {
            return false;
        }

        //provjera nedozvoljene pozicije 1 i 2
        if(offset>0) {
            if(!puzzle[row-1][column-1].isBlank()) {
                return false;
            }
            if(!puzzle[row+1][column-1].isBlank()) {
                return false;
            }
        }

        //provjera nedozvoljene pozicije 3 i 4
        if(offset < word.length()-1) {
            if(!puzzle[row-1][column+1].isBlank()) {
                return false;
            }
            if(!puzzle[row+1][column+1].isBlank()) {
                return false;
            }
        }

        for(int i = column-offset; i<column-offset+word.length(); i++) {
            if(!puzzle[row][i].equals(" ") && !puzzle[row][i].equals(String.valueOf(listaSlova[br]))) {
                return false;
            }
            br++;
        }
        return true;
    }

    public static boolean canPlaceVertical(String word, int row, int column, int offset) {
        char[] listaSlova = word.toCharArray();

        int br = 0;

        //rijec ne stane u matricu
        if(row-offset+word.length()>=GRID_SIZE) {
            return false;
        }

        if(row-offset < 0 || column<0) {
            return false;
        }

        //provjera nedozvoljene pozicije 1 i 2
        if(offset>0) {
            if(!puzzle[row-1][column-1].isBlank()) {
                return false;
            }
            if(!puzzle[row-1][column+1].isBlank()) {
                return false;
            }
        }

        //provjera nedozvoljene pozicije 3 i 4
        if(offset < word.length()-1) {
            if(!puzzle[row+1][column-1].isBlank()) {
                return false;
            }
            if(!puzzle[row+1][column+1].isBlank()) {
                return false;
            }
        }

        for(int i = row-offset; i<row-offset+word.length(); i++) {
            if(!puzzle[i][column].equals(" ") && !puzzle[i][column].equals(String.valueOf(listaSlova[br]))) {
                return false;
            }
            br++;
        }
        return true;
    }

    public static void placeHorizontal(int row, int startingPosition, String word) {
        char[] listaSlova = word.toCharArray();
        int br = 0;
        int wordLength = word.length();
        for (int j = startingPosition; j < startingPosition+wordLength; j++) {
            puzzle[row][j] = String.valueOf(listaSlova[br]);
            br++;
        }
    }

    public static void placeVertical(int column, int startingPosition, String word) {
        char[] listaSlova = word.toCharArray();
        int br = 0;
        int wordLength = word.length();
        for (int i = startingPosition; i < startingPosition+wordLength; i++) {
            puzzle[i][column] = String.valueOf(listaSlova[br]);
            br++;
        }
    }

    public static void printPuzzle(String[][] puzzle) {
        int rows = puzzle.length;
        int columns = puzzle[0].length;

        System.out.printf("%-5s","*");
        for(int j = 0; j<columns; j++) {
            System.out.printf("%-5s",j);
        }
        System.out.println();

        for (int i = 0; i < 5*(columns+1); i++) {System.out.print("-");}System.out.println();

        for(int i = 0; i<rows; i++) {
            System.out.printf("%-3s|%1s", i, "");
            for(int j = 0; j<columns; j++) {
                System.out.printf("%-5s",puzzle[i][j]);
            }
            System.out.println();
        }

        for (int i = 0; i < 5*(columns+1); i++) {System.out.print("-");}System.out.println();
    }

    public static void formatPuzzle() {
        for(PuzzleDto p : allPuzzles) {
            for(Word w : p.puzzle) {
                int[] newPosition = new int[]{w.startPosition[0] - mT, w.startPosition[1] - mL};
                w.startPosition = newPosition;
                if(w.vertical) {
                    w.endPosition = new int[]{w.startPosition[0] + w.word.length()-1, w.startPosition[1]};
                } else {
                    w.endPosition = new int[]{w.startPosition[0], w.startPosition[1] + w.word.length()-1};
                }
            }
        }
    }
}
