import java.util.List;

public class Move {

    private Position position;
    private Disc placedDisc;
    private final List<Position>flippedPositions;


    public Move(Position position,Disc placedDisc, List<Position>flippedPositions)
    {
        this.position=position;
        this.placedDisc=placedDisc;
        this.flippedPositions=flippedPositions;
    }


    public List<Position> getFlippedPositions() {
        return flippedPositions;
    }

    public Disc getPlacedDisc() {
        return placedDisc;
    }

    public Position getPosition() {
        return position;
    }

    public Position position()
    {
        return getPosition();
    }

    public Disc disc()
    {
        return getPlacedDisc();
    }
}
