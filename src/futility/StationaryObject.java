package futility;

public class StationaryObject extends FieldObject {
    
    public StationaryObject() {
        this.position = new Point(Settings.INITIAL_POSITION.x,Settings.INITIAL_POSITION.y);
    }
    
    public StationaryObject(String id, double x, double y) {
        this.id = id;
        this.position = new Point(x, y);
    }
}
