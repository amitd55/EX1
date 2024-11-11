import java.util.ArrayList;
import java.util.List;

public class GreedyAI extends AIPlayer {

    public GreedyAI(boolean isPlayerOne)
    {
        super((isPlayerOne));

    }
    @Override
    public Move makeMove(PlayableLogic gameStatus) {
        List<Position> validMoves = gameStatus.ValidMoves();

        if (validMoves.isEmpty()) {
            return null;
        }

        Position bestMove = null;
        int maxFlips = -1;

        for (Position pos : validMoves) {
            int flips = gameStatus.countFlips(pos);

            if (flips > maxFlips ||
                    (flips == maxFlips && pos.getCol() > bestMove.getCol()) ||
                    (flips == maxFlips && pos.getCol() == bestMove.getCol() && pos.getRow() > bestMove.getRow())) {

                bestMove = pos;
                maxFlips = flips;
            }
        }

        if (bestMove == null) {
            return null;
        }

        Disc chosenDisc = new SimpleDisc(this);

        return new Move(bestMove, chosenDisc, new ArrayList<>());
    }
}
