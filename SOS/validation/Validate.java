/******************************************* 
Bo-hyun (Jordan) Lee

SOS Game validation file checking input validness
(reject inaccurate ones)
*******************************************/


package validation;
import java.util.Scanner;

public class Validate {
    static Scanner input = new Scanner(System.in);

    public static int size(int grid) {
        String gridIntCheck;

        //if grid size is less then 3, keep asking for the number
        while (grid < 3) {
            System.out.println("Invalid size!");
            gridIntCheck = input.next();
            //check if the input is integer (prevent the program from crashing due to wrong input)
            if (isInteger(gridIntCheck)) {
                grid = Integer.parseInt(gridIntCheck);
            } else {
                grid = -1;
            }
        }
        return grid;
    }

    public static String turn(String firstTurn) {
        //keep asking for the valid turn (H or C)
        while (!firstTurn.equals("H") && !firstTurn.equals("C")) {
            System.out.println("Invalid turn!");
            firstTurn = input.next();
        }
        return firstTurn;
    }

    public static String plotLetter(String letter) {
        //keep asking for valid letter (S or O)
        while (!letter.equals("S") && !letter.equals("O")) {
            System.out.println("Invalid letter!");
            letter = input.next();
        }
        return letter;
    }

    public static int[] assignMove(String nextMove, String[][] plate) {
        //this function assigns the move by human, ONLY IF the move is in bound && valid 
        int[] moveStore = {-1, -1};

        //should contain positive integer values if the move was valid
        int[] moveCollection = splitMoveInput(nextMove);

        //if move is valid, 
        if (moveCollection[0] != -1 && moveCollection[1] != -1) {
            int row = moveCollection[0];
            int col = moveCollection[1];
            //check bound
            if (isInBounds(plate.length, row, col)) {
                if (plate[row][col] == null) {
                    moveStore[0] = row; 
                    moveStore[1] = col;
                    return moveStore;
                }
            }
        }
        return moveStore;
    }

    public static int[] confirmMove(int[] moveStore, String[][] plate) {
        //keep asking for valid move (in bounds, integer)
        while (moveStore[0] == -1) {
            System.out.println("Invalid move!");
            String nextMove = input.next();
            moveStore = assignMove(nextMove, plate);
        }
        return moveStore;
    }

    public static boolean isInteger(String character) {
        //check if string includes a pure int
        try {
            Integer.parseInt(character);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isInBounds(int arrayLength, int row, int col) {
        //check if two values are in bound of 2D array
        return row>=0 && col>=0 && row<arrayLength && col<arrayLength;
    }

    public static int[] splitMoveInput(String move) {
        //when move is given "int-int", split those two integers and return an array storing them 

        //if there is no "-", the move is invalid
        if (move.indexOf("-") == -1) {
            return new int[] {-1, -1};
        } else {
            String row = "";
            String col = "";

            //loop through, return based on format
            for (int i = 0; i < move.length(); i++) {
                if (i < move.indexOf("-") && isInteger(String.valueOf(move.charAt(i)))) {
                    row += move.substring(i, i+1);
                } else if (i > move.indexOf("-") && isInteger(String.valueOf(move.charAt(i)))) {
                    col += move.substring(i, i+1);
                } else if (move.substring(i, i+1).equals("-")){
                    continue;
                } else {
                    return new int[] {-1, -1};
                }
            }

            //set array and return it
            int[] moveStore = new int[2];
            moveStore[0] = Integer.parseInt(row);
            moveStore[1] = Integer.parseInt(col);

            return moveStore;
        }
    }
}