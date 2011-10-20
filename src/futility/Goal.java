package futility;

public class Goal extends StationaryObject {
    
    public Goal(String id) {
        this.id = id;
        double x = -1.0;
        double y = Settings.FIELD().getCenter().getY();
        if (id.charAt(3) == 'l') {
            x = Settings.FIELD().left;
        }
        else if (id.charAt(3) == 'r') {
            x = Settings.FIELD().right;
        }
        else {
            System.out.println("Couldn't parse goal id "+id);
        }
        position = new PositionEstimate(x, y, 1.0, true);
    }
}
