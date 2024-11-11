import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameLogic implements PlayableLogic {
    private Disc[][] board = new Disc[8][8];
    private Player player1;
    private Player player2;
    private Stack<Move> moveHistory = new Stack<>();
    private Player currentPlayer;



    public GameLogic() {
        initializeBoard();
    }

    private void switchTurn() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    private void initializeBoard() {
        board[3][3] = new SimpleDisc(player1);
        board[3][4] = new SimpleDisc(player2);
        board[4][3] = new SimpleDisc(player2);
        board[4][4] = new SimpleDisc(player1);
        currentPlayer = player1;


    }

    private boolean isValidPosition(int row, int col) {
        return row >= 0 && col >= 0 && row < board.length && col < board[0].length;
    }

    private boolean checkDirection(int row, int col, int rowAdd, int colAdd, Disc disc) {
        int rowMove = row + rowAdd;
        int colMove = col + colAdd;
        boolean found = false;
        while (isValidPosition(rowMove, colMove)) {
            if (board[rowMove][colMove] == null) {
                return false;
            }
            if (board[rowMove][colMove].getOwner().isPlayerOne() != disc.getOwner().isPlayerOne()) {
                found = true;
            } else {
                return found;
            }
            rowMove += rowAdd;
            colMove += colAdd;
        }
        return false;
    }

    private boolean isValidMove(int row, int col, Disc disc)
    {
        if (board[row][col]!=null)
        {
            return false;
        }
        if (disc instanceof UnflippableDisc && disc.getOwner().number_of_unflippedable<=0)
        {
            return false;
        }
        if (disc instanceof BombDisc && disc.getOwner().number_of_bombs<=0)
        {
            return false;
        }
        return  checkDirection(row, col, 1, 0, disc) ||
                checkDirection(row, col, 1, 1, disc) ||
                checkDirection(row, col, 1, -1, disc) ||
                checkDirection(row, col, 0, 1, disc) ||
                checkDirection(row, col, 0, -1, disc) ||
                checkDirection(row, col, -1, -1, disc) ||
                checkDirection(row, col, -1, 1, disc) ||
                checkDirection(row, col, -1, 0, disc);

    }
    private void flipDirection(int row, int col, int rowAdd, int colAdd, Disc disc, List<Position> discsToFlip)
    {
        int rowMove = row + rowAdd;
        int colMove = col + colAdd;
        List<Position>temp=new ArrayList<>();

        while (isValidPosition(rowMove,colMove)) {
            Disc currentDisk=board[rowMove][colMove];
            if (currentDisk==null) {
                return;
            }
            if (currentDisk.getOwner().isPlayerOne()!=disc.getOwner().isPlayerOne())
            {
                if (currentDisk instanceof UnflippableDisc)
                {
                    return;
                }
                temp.add(new Position(rowMove,colMove));
            }
            else {
                discsToFlip.addAll(temp);
                for (Position pos:temp)
                {
                    board[pos.getRow()][pos.getCol()].setOwner(disc.getOwner());
                }
                return;

            }
            rowMove += rowAdd;
            colMove += colAdd;
        }
    }
    private List<Position> flipDiscs(int row, int col, Disc disc) {
        List<Position> flippedPositions = new ArrayList<>();

        flipDirection(row, col, 1, 0, disc, flippedPositions);
        flipDirection(row, col, 1, 1, disc, flippedPositions);
        flipDirection(row, col, 1, -1, disc, flippedPositions);
        flipDirection(row, col, 0, 1, disc, flippedPositions);
        flipDirection(row, col, 0, -1, disc, flippedPositions);
        flipDirection(row, col, -1, -1, disc, flippedPositions);
        flipDirection(row, col, -1, 1, disc, flippedPositions);
        flipDirection(row, col, -1, 0, disc, flippedPositions);

        if (disc instanceof BombDisc) {
            flipAdjacentDiscs(row, col, disc.getOwner());
        }

        return flippedPositions;
    }
    private int countFlipsInDirection(int row, int col, int rowAdd, int colAdd, Player currentPlayer)
    {
        int rowMove = row + rowAdd;
        int colMove = col + colAdd;
        int count=0;
        while (isValidPosition(rowMove,colMove))
        {
            Disc currentDisc=board[rowMove][colMove];

            if (currentDisc==null)
            {
                return 0;
            }
            if (currentDisc.getOwner()!=currentPlayer)
            {
                count++;
            }
            else
            {
                return count;
            }
            rowMove += rowAdd;
            colMove += colAdd;
        }
        return 0;
    }
    private void flipAdjacentDiscs(int row, int col, Player newOwner) {
        int[] directions = {-1, 0, 1}; // בדיקה לכל כיוון

        for (int rowAdd : directions) {
            for (int colAdd : directions) {
                if (rowAdd == 0 && colAdd == 0) continue;

                int adjRow = row + rowAdd;
                int adjCol = col + colAdd;

                if (isValidPosition(adjRow, adjCol) && board[adjRow][adjCol] != null) {
                    Disc adjDisc = board[adjRow][adjCol];
                    if (!(adjDisc instanceof UnflippableDisc)) {
                        adjDisc.setOwner(newOwner);
                        System.out.println("Flipped disc at (" + adjRow + ", " + adjCol + ")");
                    }
                }
            }
        }
    }






        @Override
    public boolean locate_disc(Position a, Disc disc) {
        int row = a.getRow();
        int col = a.getCol();
        if (!isValidMove(row,col,disc))
        {
            return false;
        }
        board[row][col]=disc;
        if (disc instanceof UnflippableDisc)
        {
            disc.getOwner().reduce_unflippedable();
        }
        if (disc instanceof BombDisc)
        {
            disc.getOwner().reduce_bomb();
        }
            System.out.println("Player " + (currentPlayer == player1 ? "1" : "2") + " placed a " + disc.getType() + " in (" + row + ", " + col + ")");
            List<Position> flippedPositions = flipDiscs(row, col, disc);
            for (Position pos : flippedPositions) {
                Disc flippedDisc = board[pos.getRow()][pos.getCol()];
                System.out.println("Player " + (currentPlayer == player1 ? "1" : "2") + " flipped the " + flippedDisc.getType() + " in (" + pos.getRow() + ", " + pos.getCol() + ")");
            }
        moveHistory.push(new Move(a, disc, flippedPositions));
        switchTurn();
        System.out.println();
        return true;
    }

    @Override
    public Disc getDiscAtPosition(Position position) {
        return board[position.getRow()][position.getCol()];
    }

    @Override
    public int getBoardSize() {
        return board.length;
    }

    @Override
    public List<Position> ValidMoves() {
        List<Position> validMoves = new ArrayList<>();
        Disc currentDisc = new SimpleDisc(currentPlayer);
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (isValidMove(row, col, currentDisc)) {
                    validMoves.add(new Position(row, col));
                }
            }
        }
        return validMoves;
    }

    @Override
    public int countFlips(Position a) {
        int totalFlips = 0;
        int row = a.getRow();
        int col = a.getCol();

        if (board[row][col] != null) {
            return 0;
        }
        totalFlips += countFlipsInDirection(row, col, 1, 0, currentPlayer);
        totalFlips += countFlipsInDirection(row, col, 1, 1, currentPlayer);
        totalFlips += countFlipsInDirection(row, col, 1, -1, currentPlayer);
        totalFlips += countFlipsInDirection(row, col, 0, 1, currentPlayer);
        totalFlips += countFlipsInDirection(row, col, 0, -1, currentPlayer);
        totalFlips += countFlipsInDirection(row, col, -1, -1, currentPlayer);
        totalFlips += countFlipsInDirection(row, col, -1, 1, currentPlayer);
        totalFlips += countFlipsInDirection(row, col, -1, 0, currentPlayer);
        return totalFlips;
    }

    @Override
    public Player getFirstPlayer() {
        return player1;
    }

    @Override
    public Player getSecondPlayer() {
        return player2;
    }

    @Override
    public void setPlayers(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer=player1;
        initializeBoard();
    }

    @Override
    public boolean isFirstPlayerTurn() {
        return currentPlayer == player1;
    }

    @Override
    public boolean isGameFinished() {

        for (int i = 0; i < board.length; i++) {
            {
                for (int j = 0; j < board[0].length; j++) {
                    if (board[i][j] == null)
                    return false;
                }
            }
        }
        if (!ValidMoves().isEmpty()) {
            return false;
        }
        switchTurn();
        boolean otherPlayerHasMoves = !ValidMoves().isEmpty();
        switchTurn();
        if (!otherPlayerHasMoves) {
            System.out.println("Game over. No more valid moves.");
            return true;
        }
        return false;
    }

    @Override
    public void reset()
    {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = null;
            }
        }
        moveHistory.clear();
        player1.reset_bombs_and_unflippedable();
        player2.reset_bombs_and_unflippedable();
        initializeBoard();
        currentPlayer=player1;
        System.out.println("The game has been reset. Ready for a new game.");

    }

    @Override
    public void undoLastMove()
    {
        if (!moveHistory.isEmpty())
        {
            Move lastMove = moveHistory.pop();
            int row = lastMove.getPosition().getRow();
            int col = lastMove.getPosition().getCol();
            board[row][col] = null;

            for (Position pos: lastMove.getFlippedPositions())
            {
                Disc flippedDisc = board[pos.getRow()][pos.getCol()];
                if (currentPlayer == player1) {
                    flippedDisc.setOwner(player2);
                } else {
                    flippedDisc.setOwner(player1);
                }
            }
            currentPlayer = (currentPlayer == player1) ? player2 : player1;
            System.out.println("Undo: Last move has been undone.");
        } else {
            System.out.println("No previous move available to undo.");
        }
    }



    }

