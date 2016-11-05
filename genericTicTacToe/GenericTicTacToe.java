package me.squaretictactoe.genericTicTacToe;

public class GenericTicTacToe
{
    private int lineSize, boardArrayLength;
    private int boardValues[];
    private int allLinesIndex[][];
    private int playerBoardStatus[];
    private int computerBoardStatus[];
    private int numberOfLines;
    private int playerSum, computerSum, playerPreSum, computerPreSum;

    //Patch work :(
    private int numberOfMoves;

    private final static int EMPTY_VALUE  = 0;
    private final static int PLAYER_VALUE = 1;
    private static int       COMPUTER_VALUE;

    public final static int GAME_INPLAY         = 0;
    public final static int GAME_PLAYER_WINS    = 1;
    public final static int GAME_COMPUTER_WINS  = 2;
    public final static int GAME_TIE            = 3;

    public GenericTicTacToe(int bSize)
    {
        this.lineSize = bSize;
        boardArrayLength = lineSize * lineSize;

        //ensures value of single computer move is higher than playerSum
        COMPUTER_VALUE  = lineSize + 1;

        //to find if any player has won
        playerSum       = PLAYER_VALUE   * lineSize;
        computerSum     = COMPUTER_VALUE * lineSize;
        //to find winning move
        playerPreSum    = PLAYER_VALUE   * (lineSize -1);
        computerPreSum  = COMPUTER_VALUE * (lineSize -1);

        numberOfMoves = 0;
        initializeArrays();
        getAllLinesIndex();
    }

    private void initializeArrays()
    {
        boardValues = new int[boardArrayLength];
        playerBoardStatus = new int[boardArrayLength];
        computerBoardStatus = new int[boardArrayLength];

        for(int i : boardValues)
            i = EMPTY_VALUE;
    }

    public int getComputerMove()
    {
        int lineSum, index;
        int playerPreSumIndex   = -1;
        int computerPreSumIndex = -1;
        boolean indexMatch;
        int playerMaxStrength        = 0;
        int computerMaxStrength      = 0;
        //to detect tie, initialize index as -1
        int playerMaxStrengthIndex   = -1;
        int computerMaxStrengthIndex = -1;

        //initialize board status for every function call
        for(int i = 0; i < boardArrayLength; i++)
        {
            playerBoardStatus[i]    = 0;
            computerBoardStatus[i]  = 0;
        }

        //for each empty board index, iterate over all lines to calculate index strength
        for(int i = 0; i < boardArrayLength; i++)
        {
            //strength of only empty cells are needed
            if(boardValues[i] != EMPTY_VALUE)
                continue;

            for(int j = 0; j < numberOfLines; j++)
            {
                lineSum = 0;
                indexMatch = false;
                for(int k = 0; k < lineSize; k++)
                {
                    index = allLinesIndex[j][k];
                    lineSum += boardValues[index];
                    if(i == index)
                        indexMatch = true;
                }
                //if board index isn't part of this line, don't use it for index strength calculation
                if(indexMatch == false)
                    continue;

                //empty line or line with only player moves
                if(lineSum >= 0 && lineSum <= playerSum)
                    playerBoardStatus[i] += lineSum + 1;
                //empty line or line with only computer moves
                if(lineSum == 0 || lineSum%COMPUTER_VALUE == 0)
                    computerBoardStatus[i] += lineSum/COMPUTER_VALUE + 1;

                //save winning move board index (line with one empty cell)
                if(lineSum == playerPreSum && playerPreSumIndex == -1)
                    playerPreSumIndex = i;
                if(lineSum == computerPreSum && computerPreSumIndex == -1)
                    computerPreSumIndex = i;
            }

            //save board index with highest strength for both player and computer
            if(playerBoardStatus[i] > playerMaxStrength)
            {
                playerMaxStrengthIndex = i;
                playerMaxStrength = playerBoardStatus[i];
            }
            if(computerBoardStatus[i] > computerMaxStrength)
            {
                computerMaxStrengthIndex = i;
                computerMaxStrength = computerBoardStatus[i];
            }
        }

        //prints board and status matrices
        //matrix orientation won't match with actual game board
        printBoardStatus();

        //decision making to choose computer's next move
        // 1) Choose computer winning move if available
        // 2) Block player winning move if available
        // 3) If player max index strength is >= computer max, choose that
        // 4) Else choose computer max index strength
        // 5) Check for 3x3 corner case - patch work
        if(computerPreSumIndex != -1)
            index = computerPreSumIndex;
        else if(playerPreSumIndex != -1)
            index = playerPreSumIndex;
        else if(playerMaxStrength >= computerMaxStrength)
            index = playerMaxStrengthIndex;
        else
            index = computerMaxStrengthIndex;

        //patch work
        if(numberOfMoves == 3 && lineSize == 3)
        {
            //diagonal 1
            if(boardValues[0] == PLAYER_VALUE && boardValues[4] == COMPUTER_VALUE && boardValues[8] == PLAYER_VALUE)
                index = 1;
            //diagonal 2
            if(boardValues[2] == PLAYER_VALUE && boardValues[4] == COMPUTER_VALUE && boardValues[6] == PLAYER_VALUE)
                index = 1;
        }

        //game is heading to a tie if index = -1
        //playerMaxStrengthIndex and computerMaxStrengthIndex will be -1 if neither player has possibility to form a line
        if(index == -1)
        {
            //return the first empty index
            for(int i = 0; i < boardArrayLength; i++)
                if(boardValues[i] == EMPTY_VALUE)
                    index = i;
        }

        setComputerMove(index);
        return index;
    }

    //to detect if game is over or inplay
    public int getGameState()
    {
        for(int i = 0; i < numberOfLines; i++)
        {
            int lineSum = 0;
            for(int j = 0; j < lineSize; j++)
                lineSum += boardValues[allLinesIndex[i][j]];

            if(lineSum == playerSum)
                return GAME_PLAYER_WINS;
            else if(lineSum == computerSum)
                return GAME_COMPUTER_WINS;
        }

        for(int i = 0; i < boardArrayLength; i++)
            if(boardValues[i] == EMPTY_VALUE)
                return GAME_INPLAY;

        return GAME_TIE;
    }

    //indices of winning line
    public int[] getWinningIndices()
    {
        int winningIndex = 0;
        int winningArray[] = new int[lineSize];
        for(int i = 0; i < numberOfLines; i++)
        {
            int lineSum = 0;
            for(int j = 0; j < lineSize; j++)
                lineSum += boardValues[allLinesIndex[i][j]];

            if(lineSum == playerSum || lineSum == computerSum)
            {
                winningIndex = i;
                break;
            }
        }

        for(int i = 0; i < lineSize; i++)
            winningArray[i] = allLinesIndex[winningIndex][i];

        return winningArray;
    }

    private void getAllLinesIndex()
    {
        numberOfLines = (lineSize * 2) + 2;
        allLinesIndex = new int[numberOfLines][lineSize];

        //Horizontal Lines
        for(int i = 0; i < lineSize; i++)
            for(int j = 0; j < lineSize; j++)
                allLinesIndex[i][j] = i*lineSize + j;

        //Vertical Lines
        for(int i = 0; i < lineSize; i++)
            for(int j = 0; j < lineSize; j++)
                allLinesIndex[i + lineSize][j] = i + j*lineSize;

        //Diagonal Lines
        for(int i = 0; i < lineSize; i++)
            allLinesIndex[lineSize *2][i] = i + i*lineSize;
        for(int i = 0; i < lineSize; i++)
            allLinesIndex[lineSize *2 + 1][i] = (lineSize - 1) + i*lineSize - i;
    }

    public void setPlayerMove(int index)
    {
        boardValues[index] = PLAYER_VALUE;
        //patch work
        numberOfMoves++;
    }

    public void setComputerMove(int index)
    {
        boardValues[index] = COMPUTER_VALUE;
        //patch work
        numberOfMoves++;
    }

    private void printBoardStatus()
    {
        System.out.println("--------------Board--------------");
        for(int i = 0; i < boardArrayLength; i++)
        {
            System.out.print(" " + boardValues[i]);
            if((i+1)% lineSize == 0)
                System.out.println();
        }
        System.out.println("--------------Player--------------");
        for(int i = 0; i < boardArrayLength; i++)
        {
            System.out.print(" " + playerBoardStatus[i]);
            if((i+1)% lineSize == 0)
                System.out.println();
        }
        System.out.println("-------------------------------------");
        System.out.println("--------------Computer--------------");
        for(int i = 0; i < boardArrayLength; i++)
        {
            System.out.print(" " + computerBoardStatus[i]);
            if((i+1)% lineSize == 0)
                System.out.println();
        }
        System.out.println("-------------------------------------");
    }
}