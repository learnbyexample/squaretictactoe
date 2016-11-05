class GenericTicTacToe
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
        //printBoardStatus();

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
        numberOfMoves++;
    }

    public void setComputerMove(int index)
    {
        boardValues[index] = COMPUTER_VALUE;
        numberOfMoves++;
    }

    private void printBoardStatus()
    {
        printBoard();

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

    public void printBoard()
    {
        System.out.println("--------------Board--------------");
        for(int i = 0; i < boardArrayLength; i++)
        {
            System.out.print(" " + boardValues[i]);
            if((i+1)% lineSize == 0)
                System.out.println();
        }
        System.out.println("-------------------------------------");
    }
}

class test
{
    static int board[];
    static int gameState;
    static GenericTicTacToe ticTacToeBoard;

    //3 initial points - 0,1,4 are enough as board is symmetrical
    //but better to check all indices as AI implementation may not be truly symmetrical
    static int level1[] = {0,1,2,3,4,5,6,7,8};
    static int level2[] = {0,1,2,3,4,5,6,7,8};
    static int level3[] = {0,1,2,3,4,5,6,7,8};
    static int level4[] = {0,1,2,3,4,5,6,7,8};
    static int level5[] = {0,1,2,3,4,5,6,7,8};

    static int playerMoves[], computerMoves[];

    static int playerIndex, computerIndex;
    static int l2_index, l3_index, l4_index, l5_index;
    static int moveLevel;

    public static void main(String args[])
    {
        playerMoves   = new int[5];
        computerMoves = new int[4];

        for(int l1 = 0; l1 < 9; l1++)
        {
            initGameBoard();
            playerIndex = level1[l1];
            setPlayerMove(playerIndex);
            computerIndex = setComputerMove();
            playerMoves[0]   = playerIndex;
            computerMoves[0] = computerIndex;

            for(int l2 = 0; l2 < 9; l2++)
            {
                if(board[l2] != 0)
                    continue;
                else
                {
                    playerIndex   = level2[l2];
                    setPlayerMove(playerIndex);
                    computerIndex = setComputerMove();
                    playerMoves[1]   = playerIndex;
                    computerMoves[1] = computerIndex;

                    for(int l3 = 0; l3 < 9; l3++)
                    {
                        if(board[l3] != 0)
                            continue;
                        else
                        {
                            playerIndex   = level3[l3];
                            setPlayerMove(playerIndex);
                            playerMoves[2]   = playerIndex;
                            if(gameInPlay() == true)
                            {
                                computerIndex = setComputerMove();
                                computerMoves[2] = computerIndex;

                                if(gameInPlay() == true)
                                {
                                    for(int l4 = 0; l4 < 9; l4++)
                                    {
                                        if(board[l4] != 0)
                                            continue;
                                        else
                                        {
                                            playerIndex   = level4[l4];
                                            setPlayerMove(playerIndex);
                                            playerMoves[3]   = playerIndex;
                                            if(gameInPlay() == true)
                                            {
                                                computerIndex = setComputerMove();
                                                computerMoves[3] = computerIndex;

                                                if(gameInPlay() == true)
                                                {
                                                    //fifth player move - has to be only one empty cell
                                                    //so no need to restart game at end of iteration
                                                    for(int l5 = 0; l5 < 9; l5++)
                                                    {
                                                        if(board[l5] != 0)
                                                            continue;
                                                        else
                                                        {
                                                            playerIndex   = level5[l5];
                                                            setPlayerMove(playerIndex);
                                                            playerMoves[4]   = playerIndex;
                                                            if(gameInPlay() == true)
                                                            {
                                                                computerIndex = setComputerMove();
                                                                computerMoves[4] = computerIndex;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        startGameSpecificFlow(3);
                                    }
                                }
                            }
                        }
                        startGameSpecificFlow(2);
                    }
                }
                startGameSpecificFlow(1);
            }
        }
    }

    private static void startGameSpecificFlow(int level)
    {
        initGameBoard();
        for(int i = 0; i < level; i++)
        {
            int playerIndex   = playerMoves[i];
            setPlayerMove(playerIndex);
            setComputerMove();
        }
    }

    private static void initGameBoard()
    {
        ticTacToeBoard = new GenericTicTacToe(3);
        board = new int[9];

        for(int i = 0; i < 9; i++)
            board[i] = 0;
    }

    private static boolean gameInPlay()
    {
        int gameState = ticTacToeBoard.getGameState();
        if(gameState == GenericTicTacToe.GAME_INPLAY)
            return true;
        else
        {
            if(gameState == GenericTicTacToe.GAME_PLAYER_WINS)
                System.out.println("Player Wins");
            else if(gameState == GenericTicTacToe.GAME_COMPUTER_WINS)
                System.out.println("Computer Wins");
            else
                System.out.println("Tie");

            System.out.println("---------Player Moves--------");
            for(int i = 0; i < 5; i++)
                System.out.print(" " + playerMoves[i]);
            System.out.println("\n----------------------------------");
            System.out.println("---------Computer Moves--------");
            for(int i = 0; i < 4; i++)
                System.out.print(" " + computerMoves[i]);
            System.out.println("\n----------------------------------");

            ticTacToeBoard.printBoard();
            return false;
        }
    }

    private static int setComputerMove()
    {
        int computerIndex = ticTacToeBoard.getComputerMove();
        board[computerIndex] = 2;
        return computerIndex;
    }

    private static void setPlayerMove(int index)
    {
        board[index] = 1;
        ticTacToeBoard.setPlayerMove(index);
    }
}
