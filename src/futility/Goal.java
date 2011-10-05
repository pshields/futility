package futility;

public class Goal extends StationaryObject {
    
    public Goal(String id) {
        double x = -1.0;
        double y = Settings.FIELD().getCenter().y;
        if (id.charAt(4) == 'l') {
            x = Settings.FIELD().left;
        }
        else if (id.charAt(4) == 'r') {
            x = Settings.FIELD().right;
        }
        else {
            // Throw error. Parsing failure!
        }
        position = new Point(x, y);
    }
}
