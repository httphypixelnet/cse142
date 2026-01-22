package compsci.labs.critters.shared.dto;

import compsci.labs.critters.shared.Critter;
import compsci.labs.critters.shared.CritterInfo;

public record CritterInfoDTO(
    Critter.Neighbor front,
    Critter.Neighbor back,
    Critter.Neighbor left,
    Critter.Neighbor right,
    Critter.Direction direction,
    boolean frontThreat,
    boolean backThreat,
    boolean leftThreat,
    boolean rightThreat
) implements CritterInfo {
    @Override public Critter.Neighbor getFront() { return front; }
    @Override public Critter.Neighbor getBack() { return back; }
    @Override public Critter.Neighbor getLeft() { return left; }
    @Override public Critter.Neighbor getRight() { return right; }
    @Override public Critter.Direction getDirection() { return direction; }
    @Override public boolean frontThreat() { return frontThreat; }
    @Override public boolean backThreat() { return backThreat; }
    @Override public boolean leftThreat() { return leftThreat; }
    @Override public boolean rightThreat() { return rightThreat; }
}
