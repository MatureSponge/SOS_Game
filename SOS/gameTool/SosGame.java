/******************************************* 
Bo-hyun (Jordan) Lee

Tools necessary for SOS Game 
(including AI move)
*******************************************/

package gameTool;
import validation.Validate;

public class SosGame {
    public static int[] getMove(String nextMove) {
        //return array containing a valid, checked move input
        int[] moveArray = Validate.splitMoveInput(nextMove);

        return moveArray;
    }
      
    public static boolean isPlateFull(String[][] plate) {
        //check if plate is full 

        //if plate is full, the game should be over
        for (int i = 0; i < plate.length; i++) {
            for (int j = 0; j < plate[i].length; j++) {
                if (plate[i][j] == null)  {
                    return false;
                }
            }
        }
        return true;
    }

    public static String[][] applyMove(String[][] plate, int moveRow, int moveCol, String moveType) {
        //apply move based on given input (execute AFTER the validation check!!)
        if  ((Validate.isInBounds(plate.length, moveRow, moveCol)) &&(plate[moveRow][moveCol] == null)) {
            plate[moveRow][moveCol] = moveType;
        }

        return plate;
    }

    public static void displayPlate(String[][] plate) {
        //display the game plate
        for (int i = 0; i < plate.length; i++) {
            for (int j = 0; j < plate[i].length; j++) {
                if (plate[i][j] == null) {
                    System.out.print(".");
                } else {
                    System.out.print(plate[i][j]);
                }
            }
            System.out.println("");
        }
    }

    public static int getScore(String[][] plate, int moveRow, int moveCol, String moveType) {
        //from the move that was JUST made, check if the score is made 

        int score = 0;

        if (moveType.equals("S")) {
            //directions to check is the last move was "S"
            int[][] winningMethodsForS = {
                {-1,0}, {1,0},
                {0,-1}, {0,1},
                {-1,-1}, {1,1},
                {-1,1}, {1,-1}
            };

            //loop through that list, add those values to the location of last input
            for (int[] direction : winningMethodsForS) {
                int rowExample1 = moveRow + direction[0];
                int colExample1 = moveCol + direction[1];

                int rowExample2 = moveRow + 2*direction[0];
                int colExample2 = moveCol + 2*direction[1];

                //check bounds and null 
                //null.equals("String") returns error
                if ((Validate.isInBounds(plate.length, rowExample1, colExample1) && Validate.isInBounds(plate.length, rowExample2, colExample2)) 
                && (plate[rowExample1][colExample1] != null && plate[rowExample2][colExample2] != null)) {
                    if (plate[rowExample1][colExample1].equals("O") && plate[rowExample2][colExample2].equals("S")) {
                        score++;
                    }
                }

            }
        }

        //do the similaring with move "O"
        if (moveType.equals("O")) {
            //directions to check if the last move was "O"
            int[][] winningMethodsForO = {
                {1, 0}, {0, 1},
                {1, 1}, {-1, 1}
            };

            for (int[] direction : winningMethodsForO) {
                int rowExample1 = moveRow - direction[0];
                int colExample1 = moveCol - direction[1];

                int rowExample2 = moveRow + direction[0];
                int colExample2 = moveCol + direction[1];

                if ((Validate.isInBounds(plate.length, rowExample1, colExample1) && Validate.isInBounds(plate.length, rowExample2, colExample2)) 
                && (plate[rowExample1][colExample1] != null && plate[rowExample2][colExample2] != null)) {
                    if (plate[rowExample1][colExample1].equals("S") && plate[rowExample2][colExample2].equals("S")) {
                        score++;
                    }
                }
            }
        }

        return score;
    }

    public static String changeTurn(String currentTurn, int score) {
        //change turn based on game status
        if (score > 0) {
          return currentTurn;
        } else {
          if (currentTurn.equals("H")) {
            return "C";
          } else {
            return "H";
          }
        }
    }

    public static String[] computerMove(String[][] plate) {
        //COMPUTER MOVE - return array: {row, col, letter}
        // ex) {"0", "1", "S"}

        String[] finalMove = new String[3];
        int bestScore = 0;

        //-----------------------------------------------------------------------------------------------------
        //Calculating the best winning move among all
        for (int row = 0; row < plate.length; row++) {
            for (int col = 0; col < plate[row].length; col++) {
                if (plate[row][col] == null) {
                    plate[row][col] = "S";
                    //check if next move wins any point
                    if (getScore(plate, row, col, "S") > 0) {
                        //win points are not the same --> computer can win more point afterwards 
                        int currentlyEarnedScore = getScore(plate, row, col, "S");
                        int maxScorePossible = predictBestMove(plate, row, col, currentlyEarnedScore);

                        //get the best one (among the moves that win point, the move that can win the MOST POINT)
                        if (maxScorePossible > bestScore) {
                            bestScore = maxScorePossible;
                            finalMove[0] = Integer.toString(row);
                            finalMove[1] = Integer.toString(col);
                            finalMove[2] = "S";
                        }
                    }

                    //similar thing with letter O
                    plate[row][col] = "O";
                    if (getScore(plate, row, col, "O") > 0) {
                        int currentlyEarnedScore = getScore(plate, row, col, "O");
                        int maxScorePossible = predictBestMove(plate, row, col, currentlyEarnedScore);

                        if (maxScorePossible > bestScore) {
                            bestScore = maxScorePossible;
                            finalMove[0] = Integer.toString(row);
                            finalMove[1] = Integer.toString(col);
                            finalMove[2] = "O";
                        }
                    }

                    plate[row][col] = null;
                }
            }
        }

        //if there were score winning moves
        if (bestScore != 0) {
            //return the best one chosen
            return finalMove;
        }
        //-----------------------------------------------------------------------------------------------------


        //-----------------------------------------------------------------------------------------------------
        //go for safest move if there is no winning move 
        int bestBuildUp = 0;
        int depth; 
        if (plate.length >= 10) {
            depth = 2;
        } else {
            depth = 3;
        }
        for (int row = 0; row < plate.length; row++) {
            for (int col = 0; col < plate.length; col++) {
                if (plate[row][col] == null) {
                    //if safe
                    if (isSafeMove(plate, row, col, "S")) {
                        int potentialSafe = 0;
                        plate[row][col] = "S";
                        //plot, and check for next possibilities 
                        //the opponent should be the one that have to plot a RISKY MOVE (waste all the safe moves by that point)
                        //try to make even safe spot left after plotting, so the computer will plot the last remaining safe move
                        potentialSafe = buildUp(plate, "H", depth, 1, 0);

                        plate[row][col] = null;

                        //choose best one
                        if (potentialSafe > bestBuildUp) {
                            bestBuildUp = potentialSafe;
                            finalMove[0] = Integer.toString(row);
                            finalMove[1] = Integer.toString(col); 
                            finalMove[2] = "S";
                        }
                    }

                    //similar with letter "O"
                    if (isSafeMove(plate, row, col, "O")) {
                        int potentialSafe = 0;
                        plate[row][col] = "O";
                        potentialSafe = buildUp(plate, "H", depth, 1, 0);


                        plate[row][col] = null;

                        if (potentialSafe > bestBuildUp) {
                            bestBuildUp = potentialSafe;
                            finalMove[0] = Integer.toString(row);
                            finalMove[1] = Integer.toString(col);
                            finalMove[2] = "O";
                        }
                    }
                }
            }
        }
        
        //if some move is forcing the opponent to plot a risk move, 
        if (finalMove[0] != null) {
            //return that move
            return finalMove;
        }
        //-----------------------------------------------------------------------------------------------------

    
        //-----------------------------------------------------------------------------------------------------
        //if there is no "good" safe move, try to find any safe move available
        for (int row = 0; row < plate.length; row++) {
            for (int col = 0; col < plate[row].length; col++) {
                if (plate[row][col] == null) {
                    if (isSafeMove(plate, row, col, "S")) {
                        plate[row][col] = "S";
                        int safeCount = safeMoveLeft(plate);

                        if (safeCount % 2 == 0) {
                            finalMove[0] = Integer.toString(row);
                            finalMove[1] = Integer.toString(col);
                            finalMove[2] = "S";
                            plate[row][col] = null;
                            return finalMove;
                        } else if (finalMove[0] == null) {
                            finalMove[0] = Integer.toString(row);
                            finalMove[1] = Integer.toString(col);
                            finalMove[2] = "S";
                        }

                        plate[row][col] = null;
                    }
                
                    //repeat for letter "O"
                    if (isSafeMove(plate, row, col, "O")) {
                        plate[row][col] = "O";
                        int safeCount = safeMoveLeft(plate);

                        if (safeCount % 2 == 0) {
                            finalMove[0] = Integer.toString(row);
                            finalMove[1] = Integer.toString(col);
                            finalMove[2] = "O";
                            plate[row][col] = null;
                            return finalMove;
                        } else if (finalMove[0] == null) {
                            finalMove[0] = Integer.toString(row);
                            finalMove[1] = Integer.toString(col);
                            finalMove[2] = "O";
                        }

                        plate[row][col] = null;
                    }
                }
            }
        }

        //if there was any safe move left 
        if (finalMove[0] != null) {
            //return no matter it was good or not
            return finalMove;
        }
        //-----------------------------------------------------------------------------------------------------


        //-----------------------------------------------------------------------------------------------------
        //at this point, no safe move is left 
        //then go for losing the least amount of point

        int scoreToLose = Integer.MIN_VALUE;
        int leastLost = Integer.MAX_VALUE;

        for (int row = 0; row < plate.length; row++) {
            for (int col = 0; col < plate[row].length; col++) {
                if (plate[row][col] == null) {
                    plate[row][col] = "S";
                    //use the same function as win point calculation, this time simulating human
                    scoreToLose = predictBestMove(plate, row, col, 0);

                    //now, go for LEAST amount of points gained
                    if (leastLost > scoreToLose) {
                        leastLost = scoreToLose;
                        finalMove[0] = Integer.toString(row);
                        finalMove[1] = Integer.toString(col);
                        finalMove[2] = "S";
                    }

                    //repeat for letter "O"
                    plate[row][col] = "O";
                    scoreToLose = predictBestMove(plate, row, col, 0);
                    if (leastLost > scoreToLose) {
                        leastLost = scoreToLose;
                        finalMove[0] = Integer.toString(row);
                        finalMove[1] = Integer.toString(col);
                        finalMove[2] = "O";
                    }

                    plate[row][col] = null;
                }
            }
        }

        //return the final move
        System.out.print("DONE!\n");
        return finalMove;
        //-----------------------------------------------------------------------------------------------------
    }

    public static int predictBestMove(String[][] plate, int moveRow, int moveCol, int currentlyEarnedScore) {
        if (isPlateFull(plate)) {
            return currentlyEarnedScore;
        }

        int scoreEarned = currentlyEarnedScore;

        int bestScore = 0;

        for (int row = 0; row < plate.length; row++) {
            for (int col = 0; col < plate[row].length; col++) {
                if (plate[row][col] == null) {
                    plate[row][col] = "S";
                    if (getScore(plate, row, col, "S") > 0) {
                        //identify how much score was earned
                        int gained = getScore(plate, row, col, "S");
                        //continue with current board (counting the earned score)
                        scoreEarned = predictBestMove(plate, row, col, scoreEarned + gained);
                    }

                    //set the best score
                    bestScore = Math.max(scoreEarned, bestScore);

                    //reset for "O" calculation
                    scoreEarned = currentlyEarnedScore;
                    plate[row][col] = null;
                    
                    //similar thing
                    plate[row][col] = "O";
                    if (getScore(plate, row, col, "O") > 0) {
                        int gained = getScore(plate, row, col, "O");
                        scoreEarned = predictBestMove(plate, row, col, scoreEarned + gained);
                    }
                    
                    bestScore = Math.max(scoreEarned, bestScore);

                    plate[row][col] = null;
                }
            }
        }

        //at last, return the largest amount of score earned
        return bestScore;
    }
    
    public static boolean isSafeMove(String[][] plate, int moveRow, int moveCol, String moveType) {
        //check if any threat is present for next move (if next move is safe to make)

        //directions that pose threat 
        int[][] OthreatForS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {1, -1}, {1, 1}, {-1, 1}
        };

        int[][] SthreatForS = {
            {-2, 0}, {2, 0}, {0, -2}, {0, 2},
            {-2, -2}, {2, -2}, {2, 2}, {-2, 2}
        };

        if (moveType.equals("S")) {
            for (int i = 0; i < OthreatForS.length; i++) {
                if (Validate.isInBounds(plate.length, moveRow+OthreatForS[i][0], moveCol+OthreatForS[i][1]) 
                && (Validate.isInBounds(plate.length, moveRow+SthreatForS[i][0], moveCol+SthreatForS[i][1]))) {
                    int oRow = moveRow+OthreatForS[i][0];
                    int oCol = moveCol+OthreatForS[i][1];

                    int sRow = moveRow+SthreatForS[i][0];
                    int sCol = moveCol+SthreatForS[i][1];
                    
                    if (plate[sRow][sCol] != null) {
                        if (plate[oRow][oCol] == null && plate[sRow][sCol].equals("S")) {
                            //x_S --> do not place S in x!
                            return false;
                        }
                    }

                    if (plate[oRow][oCol] != null) {
                        //xo_ --> do not place S in x!
                        if (plate[oRow][oCol].equals("O") && plate[sRow][sCol] == null) {
                            return false;
                        }
                    }
                }
            }
        }
        
        if (moveType.equals("O")) {
            for (int i = 0; i < OthreatForS.length; i++) {
                //_Ox 
                //xO_
                if ((Validate.isInBounds(plate.length, moveRow+OthreatForS[i][0], moveCol+OthreatForS[i][1]))
                && (Validate.isInBounds(plate.length, moveRow-OthreatForS[i][0], moveCol-OthreatForS[i][1]))) {
                    int sRow = moveRow+OthreatForS[i][0];
                    int sCol = moveCol+OthreatForS[i][1];

                    int sRow2 = moveRow-OthreatForS[i][0];
                    int sCol2 = moveCol-OthreatForS[i][1];

                    if (plate[sRow][sCol] != null) {
                        //Sx_ --> do not place O in x
                        if (plate[sRow][sCol].equals("S") && plate[sRow2][sCol2] == null) {
                            return false;
                        }
                    }
                    if (plate[sRow2][sCol2] != null) {
                        if (plate[sRow2][sCol2].equals("S") && plate[sRow][sCol] == null) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    public static int safeMoveLeft(String[][] plate) {
        //check safe spots left 
        //if "S" and "O" are both safe in one spot, count that as one
        int safeMove = 0;

        for (int row = 0; row < plate.length; row++) {
            for (int col = 0; col < plate[row].length; col++) {
                if (plate[row][col] == null) {
                    if (isSafeMove(plate, row, col, "S") || isSafeMove(plate, row, col, "O")) {
                        safeMove++;
                    } 
                }
            }
        }

        return safeMove;
    }

    public static int buildUp(String[][] plate, String turn, int depth, int maxFactor, int minFactor) {
        //calculation of best move other than win point
        if (turn.equals("C") && safeMoveLeft(plate) == 0) {
            return -1;
        } else if (turn.equals("H") && safeMoveLeft(plate) == 1) {
            return -1;
        }

        if (turn.equals("C") && safeMoveLeft(plate) == 1) {
            return 2;
        } else if (turn.equals("H") && safeMoveLeft(plate) == 0) {
            return 2;
        }

        if (safeMoveLeft(plate) == 0) {
            if (turn.equals("H")) {
                return 2;
            } else {
                return -1;
            }
        }

        if (isPlateFull(plate)) {
            return 0;
        }
        
        //|| safeMoveLeft(plate) > (plate.length * plate.length)/1.5

        if (depth == 0 || safeMoveLeft(plate) > (plate.length * plate.length)/1.5) {
            if (turn.equals("C") && safeMoveLeft(plate) % 2 == 1) {
                //if current turn is "C" and there are odd safe move left, it means CPU will plot the last safe move
                //human is forced to give up point
                if (safeMoveLeft(plate) < emptySpotLeft(plate)) {
                    return 2;
                }
                return 1;
            } else if (turn.equals("H") && safeMoveLeft(plate) % 2 == 0) {
                //if current turn is "H" and there are even safe move left, it means human have to plot unsafe move eventually
                if (safeMoveLeft(plate) < emptySpotLeft(plate)) {
                    return 2;
                }
                return 1;
            } else {
                return -1;
            }
        }

        int bestScoreFactor = Integer.MIN_VALUE;
        for (int row = 0; row < plate.length; row++) {
            for (int col = 0; col < plate.length; col++) {
                if (plate[row][col] == null) {
                    if (isSafeMove(plate, row, col, "S")) {
                        plate[row][col] = "S";
                        int outcomeS = 0;
                        //assume each player is making safe move all the time (keep changing the turn)
                        //if human doesn't, good for CPU
                        if (turn.equals("H")) {
                            outcomeS = buildUp(plate, "C", depth-1, maxFactor, minFactor);
                        } else {
                            outcomeS = buildUp(plate, "H", depth-1, maxFactor, minFactor);
                        }
                        
                        //making best score
                        bestScoreFactor = Math.max(outcomeS, bestScoreFactor);
                        plate[row][col] = null;

                        //this means the point was earned and a good move was made 
                        //reduces run time significantly
                        if ((turn.equals("C") && bestScoreFactor > maxFactor) 
                        || (turn.equals("H") && bestScoreFactor < minFactor)) {
                            return bestScoreFactor;
                        }
                    }
                    
                    //same thing for "O"
                    if (isSafeMove(plate, row, col, "O")) {
                        plate[row][col] = "O";
                        int outcomeO = 0;
                        if (turn.equals("H")) {
                            outcomeO = buildUp(plate, "C", depth-1, maxFactor, minFactor);
                        } else {
                            outcomeO = buildUp(plate, "H", depth-1, maxFactor, minFactor);
                        }
                        bestScoreFactor = Math.max(outcomeO, bestScoreFactor);
                        plate[row][col] = null;

                        if ((turn.equals("C") && bestScoreFactor > maxFactor) 
                        || (turn.equals("H") && bestScoreFactor < minFactor)) {
                            return bestScoreFactor;
                        }
                    }
                }
            }
        }
        
        //return at last
        return bestScoreFactor;
    }

    public static int emptySpotLeft(String[][] plate) {
        int emptySpot = 0;

        for (int row = 0; row < plate.length; row++) {
            for (int col = 0; col < plate[row].length; col++) {
                if (plate[row][col] == null) {
                    emptySpot ++;
                }
            }
        }

        return emptySpot;
    }
}