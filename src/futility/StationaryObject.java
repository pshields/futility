package futility;

public class StationaryObject extends FieldObject {
    
    public StationaryObject() {
    }
    
    public StationaryObject(String id, double x, double y) {
        this.id = id;
        this.position = new PositionEstimate(x, y, 1.0, true);
    }
    
    public boolean isStationaryObject() {
        return true;
    }
}
