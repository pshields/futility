package futility;

public class Flag extends StationaryObject {
    
    /** Flag constructor from ObjectInfo string
     * 
     * Pass in an object info string, get back a flag! It's magic!
     * @param id the ObjectInfo string
     */
    public Flag(String id) {
        this.id = id;
        // Remove opening/closing parenthesis for readability
        String[] parts = id.substring(1, id.length() - 1).split(" ");
        double x = -1.0;
        double y = -1.0;

        // Handle goalpost flags
        if (parts[1].equals("g")) {
            // Set the horizontal value
            if (parts[2].equals("l")) {
                // The flag marks a goalpost on the left side
                x = Settings.FIELD().left;
            }
            else if (parts[2].equals("r")) {
                // The flag marks a goalpost on the right side
                x = Settings.FIELD().right;
            }
            // Set the horizontal value
            if (parts[3].equals("t")) {
                // The flag marks the top goalpost
                y = Settings.FIELD().getCenter().y + Settings.GOAL_HEIGHT / 2;
            }
            else if (parts[3].equals("b")) {
                // The flag marks the bottom goalpost
                y = Settings.FIELD().getCenter().y - Settings.GOAL_HEIGHT / 2;
            }
        }
        // Handle center flags
        else if (parts[1].equals("c")) {
            x = Settings.FIELD().getCenter().x;
            if (parts.length == 2) {
                // The flag marks center field
                y = Settings.FIELD().getCenter().y;
            }
            else if (parts[2].equals("t")) {
                // The flag marks top-center field
                y = Settings.FIELD().top;
            }
            else if (parts[2].equals("b")) {
                // The flag marks bottom-center field
                y = Settings.FIELD().bottom;
            }
        }
        // Handle flags on penalty area corners
        else if (parts[1].equals("p")) {
            // Set the horizontal value
            if (parts[2].equals("l")) {
                // The flag is on the left side
                x = Settings.PENALTY_AREA_LEFT().right;
            }
            else if (parts[2].equals("r")) {
                // The flag is on the right side
                x = Settings.PENALTY_AREA_RIGHT().left;
            }
            // Set the vertical value
            if (parts[3].equals("t")) {
                // The flag is at the top of the penalty area
                y = Settings.PENALTY_AREA_LEFT().top;
            }
            else if (parts[3].equals("c")) {
                // The flag is at the vertical center of the penalty area
                y = Settings.PENALTY_AREA_LEFT().getCenter().y;
            }
            else if (parts[3].equals("b")) {
                // The flag is at the bottom of the penalty area
                y = Settings.PENALTY_AREA_LEFT().bottom;
            }
        }
        
        // Handle flags in the four corners of the field
        else if (parts.length == 3) {
            // Set the horizontal value
            if (parts[1].equals("l")) {
                // The flag is on the left side of the field
                x = Settings.FIELD().left;
            }
            else if (parts[1].equals("r")) {
                // The flag is on the right side of the field
                x = Settings.FIELD().right;
            }
            // Set the vertical value
            if (parts[2].equals("t")) {
                // The flag is at the top of the field
                y = Settings.FIELD().top;
            }
            else if (parts[2].equals("b")) {
                // The flag is at the bottom of the field
                y = Settings.FIELD().bottom;
            }
        }
        // Handle physical boundary flags
        else if (parts.length == 4) {
            double offset = Double.parseDouble(parts[3]);
            // Set the horizontal value
            if (parts[1].equals("l")) {
                // The flag is on the left side of the physical boundary
                x = Settings.PHYSICAL_BOUNDARY().left;
            }
            else if (parts[1].equals("r")) {
                // The flag is on the right side of the physical boundary
                x = Settings.PHYSICAL_BOUNDARY().right;
            }
            else if (parts[2].equals("l")) {
                // The flag is on the left half of the physical boundary
                x = Settings.FIELD().getCenter().x - offset;
            }
            else if (parts[2].equals("r")) {
                // The flag is on the right half of the physical boundary
                x = Settings.FIELD().getCenter().x + offset;
            }
            // Set the vertical value
            if (parts[1].equals("t")) {
                // The flag is on the top side of the physical boundary
                y = Settings.PHYSICAL_BOUNDARY().top;
            }
            else if (parts[1].equals("b")) {
                // The flag is on the bottom side of the physical boundary
                y = Settings.PHYSICAL_BOUNDARY().bottom;
            }
            else if (parts[2].equals("t")) {
                // The flag is on the top half of the field
                y = Settings.FIELD().getCenter().y + offset;
            }
            else if (parts[2].equals("b")) {
                // The flag is on the bottom half of the field
                y = Settings.FIELD().getCenter().y - offset;
            }
        }
        position = new Point(x, y);
    }
}
