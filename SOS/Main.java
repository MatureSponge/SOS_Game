/******************************************* 
Bo-hyun (Jordan) Lee

SOS Game main file containing game procedure
*******************************************/

import gameTool.SosGame;
import validation.Validate;
import java.util.Scanner;

class Main {
  public static void main(String[] args) { 
    Scanner input = new Scanner(System.in);

    System.out.println("Welcome to SOS!\n");
    System.out.println("This is a game of SOS, where you can play against the computer.\n");

    //get the size of the grid
    System.out.println("What size Grid (nxn)?");
    String gridIntCheck = input.next();
    int gridSize;
    if (Validate.isInteger(gridIntCheck)) {
      gridSize = Integer.parseInt(gridIntCheck);
    } else {
      gridSize = -1;
    }
    //make sure the grid size is larger than 2 (so the game is playable)
    gridSize = Validate.size(gridSize);
    String[][] gamePlate = new String[gridSize][gridSize];
    System.out.println("");

    //who go first? 
    System.out.println("Who should go first Human or CPU (H/C)? ");
    String turn = input.next();
    //it should be human or computer
    turn = Validate.turn(turn);
    System.out.println("");

    boolean gameOver = SosGame.isPlateFull(gamePlate);
    int userScore = 0;
    int cpuScore = 0;

    while (!gameOver) {
      String plotChoice = "";
      String move = "";
      int[] moveLocation = {-1,-1};
      int scoreEarned = 0;

      if (turn.equals("H")) {
        //Plot choice = string
        System.out.println("(Human) Which letter to place (S/O) ?");
        plotChoice = input.next();
        plotChoice = Validate.plotLetter(plotChoice);
        System.out.println("");

        //move = string "int-int"
        System.out.println("(Human) Make your move (row-col):");
        move = input.next();
        moveLocation = Validate.assignMove(move, gamePlate);
        moveLocation = Validate.confirmMove(moveLocation, gamePlate);
      } else {
        System.out.print("THINKING...\n\n");

        //computerMove function returns String[]
        //plotData[0] = row, plotData[1] = col
        //plotData[2] = "S" or "O"
        String[] plotData = SosGame.computerMove(gamePlate);

        //assign variable based on the data in array
        plotChoice = plotData[2];
        move = plotData[0] + "-" + plotData[1];
        moveLocation = SosGame.getMove(move); 

        System.out.print("(Computer) CPU Letter: " + plotChoice + "\n");
        System.out.println("(Computer) CPU Move: " + move + "\n");

      }

      //calculate the score to get with the last move, AND THEN apply the movement
      scoreEarned = SosGame.getScore(gamePlate, moveLocation[0], moveLocation[1], plotChoice);
      gamePlate = SosGame.applyMove(gamePlate, moveLocation[0], moveLocation[1], plotChoice);
      SosGame.displayPlate(gamePlate);

      //apply score (0 is added if nothing)
      if (turn.equals("H")) {
        userScore += scoreEarned;
      } else {
        cpuScore += scoreEarned;
        if (scoreEarned > 0) {
          //if CPU earned something, it will make consecutive moves
          //make the move each second (not all at once)
          waitSec();
        }
      }
      
      System.out.println("Human score: " + userScore);
      System.out.println("CPU score: " + cpuScore +"\n");
    
      turn = SosGame.changeTurn(turn, scoreEarned);

      //if gameOver == true, the loop terminates
      gameOver = SosGame.isPlateFull(gamePlate);
    }

    //detect winner
    if (userScore > cpuScore) {
      System.out.println("HUMAN WON!");
    } else if (cpuScore > userScore) {
      System.out.println("CPU WON!");
    } else {
      System.out.println("TIE!");
    }

    input.close();
  }

  public static void waitSec() {
    //function that waits a second for clarity
    try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
    }
  }
}