package futility;

public class StationaryObject extends FieldObject {
    
    public StationaryObject() {
        this.position = new Point(Settings.INITIAL_POSITION.getX(),Settings.INITIAL_POSITION.getY());
    }
    
    public StationaryObject(String id, double x, double y) {
        this.id = id;
        this.position = new Point(x, y);
    }
    
    public boolean isStationaryObject() {
        return true;
    }
}
