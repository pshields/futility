package futility;

public class StationaryObject extends FieldObject {
    String id;
    
    public StationaryObject() {
        this.position = Settings.INITIAL_POSITION();
    }
    
    public StationaryObject(String id, double x, double y) {
        this.id = id;
        this.position = new Point(x, y);
    }
}
