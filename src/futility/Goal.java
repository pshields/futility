package futility;

public class Goal extends StationaryObject {
    
    public Goal(String id) {
        this.id = id;
        double x = -1.0;
        double y = Settings.FIELD().getCenter().y;
        if (id.charAt(4) == 'l') {
            x = Settings.FIELD().left;
        }
        else if (id.charAt(4) == 'r') {
            x = Settings.FIELD().right;
        }
        else {
            // TODO get logging to work here
            //log(Settings.LOG_LEVELS.ERROR, "Couldn't parse goal id "+id);
        }
        position = new Point(x, y);
    }
}
