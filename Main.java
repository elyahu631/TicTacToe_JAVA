import java.util.Scanner;

/**
 * Main class - contains the main method to start the game,
 * It initializes a scanner for user input and controls the game loop.
 * */
public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            Game game = createGame(scanner);
            if (game == null) {
                break;
            }

            game.playGame();

            System.out.print("Do you want to play again? (y - for yes/n - for no): ");
            String playAgain = scanner.next().toLowerCase();
            if (!playAgain.equals("y")) {
                break; // Exit the loop if the user doesn't want to play again
            }

        }
    }

    private static Game createGame(Scanner scanner) {
        System.out.println("""
        \n===== Welcome to Tic Tac Toe ❎ ⭕! =====
        Select Board Size:
          1. 3x3 Board
          2. 4x4 Board
        Enter your choice (1 or 2):\s""");
        int boardSizeChoice = getIntFromUser(scanner, 2);
        int boardSize = boardSizeChoice == 1 ? 3 : 4;

        System.out.println("""
        \nChoose Your Opponent:
          1. Play Against the Computer
          2. Play Against Another Player
          3. Computer vs Computer
          4. Exit Game
        Enter your choice (1, 2, 3, or 4):\s""");
        int choice = getIntFromUser(scanner, 4);

        if (choice == 4) {
            return null;
        }

        System.out.println("\nChoose Your Symbol:");
        System.out.println("  1. X");
        System.out.println("  2. O");
        System.out.print("Enter your choice (1 or 2): ");
        int symbolChoice = getIntFromUser(scanner, 2);
        char playerSymbol = (symbolChoice == 1) ? 'X' : 'O';

        Player player1, player2;
        if (choice == 1) {
            player1 = new HumanPlayer(playerSymbol, scanner);
            player2 = new ComputerPlayer(playerSymbol == 'X' ? 'O' : 'X');
        } else if (choice == 2) {
            player1 = new HumanPlayer(playerSymbol, scanner);
            player2 = new HumanPlayer(playerSymbol == 'X' ? 'O' : 'X', scanner);
        } else {
            player1 = new ComputerPlayer('X');
            player2 = new ComputerPlayer('O');
        }

        return playerSymbol == 'X' ? new Game(player1, player2, boardSize) : new Game(player2, player1, boardSize);
    }

    private static int getIntFromUser(Scanner scanner, int maxOption) {
        while (true) {
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                if (choice >= 1 && choice <= maxOption) {
                    return choice;
                }
            }
            scanner.nextLine(); // Consume invalid input
            System.out.print("Invalid input. Enter a valid choice (1 - " + maxOption + "): ");
        }
    }
}

/**
 * Game class - Manages the overall game flow and rules.
 * It handles the turn-based logic, alternating between players, and determines the game's end.
 */
class Game {
    private final GameBoard board;
    private final Player playerX;
    private final Player playerO;

    public Game(Player playerX, Player playerO, int boardSize) {
        board = new GameBoard(boardSize);  // Dynamic board size
        this.playerX = playerX;
        this.playerO = playerO;
    }

    public void playGame() {
        Player currentPlayer = playerX.getSymbol() == 'X' ? playerX : playerO;
        boolean gameEnded = false;

        while (!gameEnded) {
            board.printBoard();
            boolean moveValid = false;

            while (!moveValid) {
                moveValid = currentPlayer.makeMove(board);
            }

            gameEnded = board.checkWin() || board.isPotentialWinPossible();

            currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
            if (board.isPotentialWinPossible()) {
                System.out.println("It's a tie!");
                return;  // End the game as a tie
            }
        }

        board.printBoard();
        if (board.checkWin()) {
            System.out.println("We have a winner! " + currentPlayer.getOpponent() + " wins!");
        } else  {
            System.out.println("It's a tie!");
        }
    }
}

/**
 * GameBoard class - Represents the tic-tac-toe game board.
 * It manages the board state, including cell values, board size, and checking win/tie conditions.
 */
class GameBoard {
    private final char[][] board; // 2D array representing the game board.
    private final int size;
    public static final char EMPTY = '_';

    // ANSI color code constants
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public GameBoard(int size) {
        this.size = size;
        board = new char[size][size];
        initializeBoard();
    }

    /**
     * The function checks if there is a potential win possible by checking if any row, column, or
     * diagonal has at least one empty cell.
     **/
    public boolean isPotentialWinPossible() {
        // Check if any row, column, or diagonal has at least one empty cell.
        for (int i = 0; i < size; i++) {
            if (hasEmptyCell(getRow(i)) || hasEmptyCell(getColumn(i))) {
                return false;
            }
        }
        return !hasEmptyCell(getDiagonal(0)) && !hasEmptyCell(getDiagonal(1));
    }

    /**
     * The function checks if a given array of characters contains an empty cell.
     * (if there is an empty cell in the given line)
     */   
    private boolean hasEmptyCell(char[] line) {
        for (char cell : line) {
            if (cell == EMPTY) {
                return true;
            }
        }
        return false;
    }

    public char[][] getBoard() {
        return board;
    }

    // returns the row at the specified index from a 2D char array.
    public char[] getRow(int rowIndex) {
        return board[rowIndex];
    }

    // returns a char array representing a column of characters from a 2D board.
    public char[] getColumn(int columnIndex) {
        char[] column = new char[size];
        for (int i = 0; i < size; i++) {
            column[i] = board[i][columnIndex];
        }
        return column;
    }

    /**
        * The function "getDiagonal" returns a char array representing a diagonal of a board, based on the
        * given diagonal index.
        * 
        * @param diagonalIndex The diagonalIndex parameter is an integer that represents which diagonal to
        * retrieve from the board. If diagonalIndex is 0, it retrieves the main diagonal (from top-left to
        * bottom-right). If diagonalIndex is 1, it retrieves the secondary diagonal (from top-right to
        * bottom-left).
        * @return char array representing a diagonal of a board.
        */
    public char[] getDiagonal(int diagonalIndex) {
        char[] diagonal = new char[size];
        for (int i = 0; i < size; i++) {
            diagonal[i] = diagonalIndex == 0 ? board[i][i] : board[i][size - 1 - i];
        }
        return diagonal;
    }

    // initializes a 2D array called "board" with   "_" for each element.
    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    /**
     * The function makes a move on a game board if the move is valid and the specified position is
     * empty.
     * 
     * @param row represents the row index of the cell where the player wants to make
     * a move on the game board.
     * @param col represents the column index of the move that the player wants to
     * make on the game board.
     * @param playerSymbol represents the symbol or character that represents the player making the move.
     * It could be 'X' or 'O' for example, depending on the game being played.
     * @return boolean value.
     */
    public boolean makeMove(int row, int col, char playerSymbol) {
        if (isValidMove(row, col) && board[row][col] == EMPTY) {
            board[row][col] = playerSymbol;
            return true;
        }
        return false;
    }

    // Checks if there is a winning line (row, column, or diagonal) in a given game board.
    public boolean checkWin() {
        // Check rows
        for (int i = 0; i < size; i++) {
            if (checkLine(board[i])) {
                return true;
            }
        }

        // Check columns
        for (int j = 0; j < size; j++) {
            char[] column = new char[size];
            for (int i = 0; i < size; i++) {
                column[i] = board[i][j];
            }
            if (checkLine(column)) {
                return true;
            }
        }

        // Check diagonals
        char[] diagonal1 = new char[size];
        char[] diagonal2 = new char[size];
        for (int i = 0; i < size; i++) {
            diagonal1[i] = board[i][i];
            diagonal2[i] = board[i][size - 1 - i];
        }
        return checkLine(diagonal1) || checkLine(diagonal2);
    }

    // The function checks if all elements in a given array are the same character.
    private boolean checkLine(char[] line) {
        char first = line[0];
        if (first == EMPTY) {
            return false;
        }
        for (char cell : line) {
            if (cell != first) {
                return false;
            }
        }
        return true;
    }
    
    // The function checks if a specific position on the board is empty.
    public boolean isEmpty(int row, int col) {
        return isValidMove(row, col) && board[row][col] == EMPTY;
    }

    public int getSize() {
        return size;
    }

    // The function checks if a given row and column are within the valid range.
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    // Prints the current state of a board, highlighting 'X' in red and 'O' in blue.
    public void printBoard() {
        System.out.println("Current Board:");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == 'X') {
                    System.out.print(ANSI_GREEN + board[i][j] + ANSI_RESET + " ");
                } else if (board[i][j] == 'O') {
                    System.out.print(ANSI_RED + board[i][j] + ANSI_RESET + " ");
                } else {
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        }
    }

}

/**
 * Abstract Player class - Defines a player in the game.
 * It includes common attributes and methods used by both human and computer players.
 */
abstract class Player {
    protected char symbol;
    protected char opponentSymbol;
    protected static final int MAX_DEPTH = 6; 

    public Player(char symbol) {
        this.symbol = symbol;
        this.opponentSymbol = (symbol == 'X') ? 'O' : 'X';
    }

    public char getSymbol() {
        return symbol;
    }

    public abstract boolean makeMove(GameBoard board);

    /**
     * symbol of the opposing player.
     * This is useful to keep track of the opponent's moves and to calculate the best move accordingly.
     * @return The symbol ('X' or 'O') of the opponent.
     */   
    public char getOpponent() {
        return opponentSymbol;
    }

    /**
     * Determines the best move for the player on the given board state.
     * It iterates over all possible moves, applies each one, and uses the minimax algorithm
     * to evaluate the move. The best move is the one that maximizes the player's chances of winning.
     *
     * @param board The current state of the game board.
     * @return An array of two integers representing the row and column of the best move.
     */
    protected int[] findBestMove(GameBoard board) {
        int bestVal = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};
        int alpha = Integer.MIN_VALUE; // Alpha value for alpha-beta pruning
        int beta = Integer.MAX_VALUE;

        // Iterating over all cells to check potential moves.
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) { 
                if (board.isEmpty(i, j)) { // Check if the cell is empty to make a move.
                    board.getBoard()[i][j] = symbol; // Temporarily make the move on the board.
                    int moveVal = minimax(board, 0, false, alpha, beta); // Evaluate the move.
                    board.getBoard()[i][j] = GameBoard.EMPTY;  // Undo the move.
                    // If the value of the current move is better than the best value, update best value and best move.
                    if (moveVal > bestVal) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestVal = moveVal;
                    }
                }
            }
        }
        return bestMove;
    }
    
    /*Alpha-beta pruning is a sophisticated technique within the minimax algorithm, used to find the best move in games,
     assuming the opponent is also playing optimally.

        Minimax algorithm:

        Searches for the optimal move for a player, taking into account the opponent's play.
        Used in games like chess or tic-tac-toe to help a computer player choose their next move.
        Alpha-beta pruning:

        Reduces the number of nodes that need to be evaluated in the game tree.
        Skips the evaluation of certain branches that don't need to be searched because there's already a better
        move available.

        Alpha:

        In this context, alpha represents the best already explored option along the path to the root for the maximizer.
        In simpler terms, it's the best score the maximizer can guarantee for themselves so far.
        When searching through possible moves, if the minimizer finds a move that leads to a score worse than alpha,
        it can stop considering further moves in that direction (as the maximizer already has a better option).
        Beta:

        Similarly, beta is the counterpart for the minimizer.
        It represents the best already explored option for the minimizer.
        If the maximizer encounters a situation that leads to a score better than beta, it can stop exploring further as
        the minimizer would avoid that path.

        How it works?

        Alpha and beta are passed down the game tree together during the recursive calls of the minimax algorithm.
        They are updated and used to prune unnecessary branches, thus significantly reducing the number of nodes that
        need to be evaluated, thus optimizing the decision-making process.
        In conclusion, alpha-beta pruning is an efficient technique that improves the speed of the decision-making
        process in the minimax algorithm.
    */

    /**
     * The minimax algorithm with alpha-beta pruning. It recursively evaluates all possible moves
     * and returns the value of the board. Minimax algorithm is a backtracking algorithm used in decision-making
     * and game theory to find the optimal move for a player, assuming the opponent also plays optimally.
     *
     * @param board The game board.
     * @param depth The current depth in the game tree.
     * @param isMax Whether the current move is a maximizing move or a minimizing move.
     * @param alpha The best (highest) score that the maximizer can guarantee at that level or above.
     * @param beta The best (lowest) score that the minimizer can guarantee at that level or above.
     * @return The score of the board.
     */
    private int minimax(GameBoard board, int depth, boolean isMax, int alpha, int beta) {
        int score = evaluate(board); // Evaluate the current board state.

        // Base conditions for recursion termination.
        if (depth == MAX_DEPTH || score == 10 || score == -10 || board.isPotentialWinPossible()) {
            return score;
        }

        int best; // The best score that can be achieved from this point.
        if (isMax) {
            best = Integer.MIN_VALUE;
            // Maximizer's move
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    if (board.isEmpty(i, j)) {
                        // Make a move, evaluate and undo
                        board.getBoard()[i][j] = symbol;
                        // Recurse for minimax.
                        best = Math.max(best, minimax(board, depth + 1, false, alpha, beta));
                        // Undo the move.
                        board.getBoard()[i][j] = GameBoard.EMPTY;
                        // Update alpha.
                        alpha = Math.max(alpha, best);
                        // Alpha-beta pruning
                        if (beta <= alpha) break; 
                    }
                }
            }
        } else {
            best = Integer.MAX_VALUE;
            // Similar iteration for the minimizer player.
            for (int i = 0; i < board.getSize(); i++) {
                for (int j = 0; j < board.getSize(); j++) {
                    if (board.isEmpty(i, j)) {
                        // Make a move, evaluate and undo
                        board.getBoard()[i][j] = opponentSymbol;
                        best = Math.min(best, minimax(board, depth + 1, true, alpha, beta));
                        board.getBoard()[i][j] = GameBoard.EMPTY;
                        beta = Math.min(beta, best);
                        if (beta <= alpha) break; 
                    }
                }
            }
        }
        return best;
    }

    /**
     * Evaluates the board and returns a score based on the current state.
     * @param board The game board.
     * @return Score of the board. Positive for favorable outcomes for the player, negative for the opponent.
     */
    private int evaluate(GameBoard board) {
        // Check all rows, columns, and diagonals to calculate the score.
        for (int i = 0; i < board.getSize(); i++) {
            if (isWinningLine(symbol, board.getRow(i)) ||
                    isWinningLine(symbol, board.getColumn(i)) ||
                    (i < 2 && isWinningLine(symbol, board.getDiagonal(i)))) {
                return +10; // Return positive score if the player is winning.
            }
            if (isWinningLine(opponentSymbol, board.getRow(i)) ||
                    isWinningLine(opponentSymbol, board.getColumn(i)) ||
                    (i < 2 && isWinningLine(opponentSymbol, board.getDiagonal(i)))) {
                return -10; // Return negative score if the opponent is winning.
            }
        }
        return 0; // Return 0 if no one is winning.
    }

    /**
     * Checks if a line (row, column, or diagonal) is winning for a given player.
     * @param playerSymbol The player's symbol ('X' or 'O').
     * @param line The line (row, column, or diagonal) to be checked.
     * @return True if the line is winning for the player, false otherwise.
     */
    private boolean isWinningLine(char playerSymbol, char[] line) {
        // If any cell in the line is not the player's symbol, return false otherwise return true
        for (char cell : line) {
            if (cell != playerSymbol) {
                return false;
            }
        }
        return true;
    }
}

/**
 * HumanPlayer class - Represents a human player in the game.
 * It extends the Player class and implements user input for making moves.
 */
class HumanPlayer extends Player {
    protected Scanner scanner;

    public HumanPlayer(char symbol, Scanner scanner) {
        super(symbol);
        this.scanner = scanner;
    }

    /**
        * The function prompts the player for a move, either a two-digit number representing a row and
        * column on the game board or the letter 's' for a suggestion, and then makes the move or provides
        * a suggestion based on the current game board.
        * 
        * @param board The parameter "board" is an instance of the "GameBoard" class. It represents the
        * current state of the game board.
        * @return  boolean value.
        */
    @Override
    public boolean makeMove(GameBoard board) {
        System.out.println("Player " + symbol + ", enter your move (e.g., 12 for row 1, column 2), or 's' for a suggestion: ");
        String input = scanner.next();

        if (input.equalsIgnoreCase("s")) {
            int[] bestMove = findBestMove(board);
            System.out.println("Suggested best move: " + (bestMove[0] + 1) + ", " + (bestMove[1] + 1));
            return false; // No move made, just a suggestion
        } else {
            return processInputAndMakeMove(input, board);
        }
    }

    /**
        * Takes user input, validates it, and makes a move on the game board if the input is
        * valid.
        * 
        * @param input  represents the user's input. It is expected to
        * be a two-digit number.
        * @param board an instance of the `GameBoard` class. It represents the
        * game board on which the moves are being made.
        * @return The method is returning a boolean value.
        */
    private boolean processInputAndMakeMove(String input, GameBoard board) {
        try {
            int move = Integer.parseInt(input);
            if (move >= 11 && move <= (board.getSize() * 11)) { 
                int row = move / 10 - 1; 
                int col = move % 10 - 1; 
                return board.makeMove(row, col, symbol);
            } else {
                System.out.println("Invalid input. Please enter a two-digit number (e.g., 12 for row 1, column 2).");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a two-digit number.");
            return false; 
        }
    }

}

/**
 * ComputerPlayer class - Represents a computer-controlled player.
 * It extends the Player class and implements AI logic for making moves.
 */
class ComputerPlayer extends Player {

    public ComputerPlayer(char symbol) {
        super(symbol);
    }

    /**
        * This function finds the best move for the current player and makes that move on the game board.
        * 
        * @param board The "board" parameter is an instance of the GameBoard class. It represents the
        * current state of the game board on which the move is being made.
        * @return boolean value.
        */
    @Override
    public boolean makeMove(GameBoard board) {
        int[] bestMove = findBestMove(board);
        return board.makeMove(bestMove[0], bestMove[1], this.symbol);
    }
}

