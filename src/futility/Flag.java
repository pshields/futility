/** @file Flag.java
 * Representation of a flag object on the visible playing field.
 * 
 * @author Team F(utility)
 */

package futility;

/** @class Flag
 * An extension of StationaryObject that represents a visible flag on the field
 */
public class Flag extends StationaryObject {
    
    /**
     * Flag constructor from ObjectInfo string.
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
                x = Settings.FIELD().getLeft();
            }
            else if (parts[2].equals("r")) {
                // The flag marks a goalpost on the right side
                x = Settings.FIELD().getRight();
            }
            // Set the horizontal value
            if (parts[3].equals("t")) {
                // The flag marks the top goalpost
                y = Settings.FIELD().getCenter().getY() + Settings.GOAL_HEIGHT / 2;
            }
            else if (parts[3].equals("b")) {
                // The flag marks the bottom goalpost
                y = Settings.FIELD().getCenter().getY() - Settings.GOAL_HEIGHT / 2;
            }
        }
        // Handle center flags
        else if (parts[1].equals("c")) {
            x = Settings.FIELD().getCenter().getX();
            if (parts.length == 2) {
                // The flag marks center field
                y = Settings.FIELD().getCenter().getY();
            }
            else if (parts[2].equals("t")) {
                // The flag marks top-center field
                y = Settings.FIELD().getTop();
            }
            else if (parts[2].equals("b")) {
                // The flag marks bottom-center field
                y = Settings.FIELD().getBottom();
            }
        }
        // Handle flags on penalty area corners
        else if (parts[1].equals("p")) {
            // Set the horizontal value
            if (parts[2].equals("l")) {
                // The flag is on the left side
                x = Settings.PENALTY_AREA_LEFT().getRight();
            }
            else if (parts[2].equals("r")) {
                // The flag is on the right side
                x = Settings.PENALTY_AREA_RIGHT().getLeft();
            }
            // Set the vertical value
            if (parts[3].equals("t")) {
                // The flag is at the top of the penalty area
                y = Settings.PENALTY_AREA_LEFT().getTop();
            }
            else if (parts[3].equals("c")) {
                // The flag is at the vertical center of the penalty area
                y = Settings.PENALTY_AREA_LEFT().getCenter().getY();
            }
            else if (parts[3].equals("b")) {
                // The flag is at the bottom of the penalty area
                y = Settings.PENALTY_AREA_LEFT().getBottom();
            }
        }        
        else if (parts.length == 3) {
            // Handle the four half-way points on the physical boundary
            if (parts[2].equals("0")) {
                if (parts[1].equals("t")) {
                    x = Settings.PHYSICAL_BOUNDARY().getCenter().getX();
                    y = Settings.PHYSICAL_BOUNDARY().getTop();
                }
                else if (parts[1].equals("b")) {
                    x = Settings.PHYSICAL_BOUNDARY().getCenter().getX();
                    y = Settings.PHYSICAL_BOUNDARY().getBottom();
                }
                else if (parts[1].equals("l")) {
                    x = Settings.PHYSICAL_BOUNDARY().getLeft();
                    y = Settings.PHYSICAL_BOUNDARY().getCenter().getY();
                }
                else if (parts[1].equals("r")) {
                    x = Settings.PHYSICAL_BOUNDARY().getRight();
                    y = Settings.PHYSICAL_BOUNDARY().getCenter().getY();
                }
                
            }
            // Handle flags in the four corners of the field
            else {
                // Set the horizontal value
                if (parts[1].equals("l")) {
                    // The flag is on the left side of the field
                    x = Settings.FIELD().getLeft();
                }
                else if (parts[1].equals("r")) {
                    // The flag is on the right side of the field
                    x = Settings.FIELD().getRight();
                }
                // Set the vertical value
                if (parts[2].equals("t")) {
                    // The flag is at the top of the field
                    y = Settings.FIELD().getTop();
                }
                else if (parts[2].equals("b")) {
                    // The flag is at the bottom of the field
                    y = Settings.FIELD().getBottom();
                }
            }
        }

        // Handle physical boundary flags
        else if (parts.length == 4) {
            double offset = Double.parseDouble(parts[3]);
            // Set the horizontal value
            if (parts[1].equals("l")) {
                // The flag is on the left side of the physical boundary
                x = Settings.PHYSICAL_BOUNDARY().getLeft();
            }
            else if (parts[1].equals("r")) {
                // The flag is on the right side of the physical boundary
                x = Settings.PHYSICAL_BOUNDARY().getRight();
            }
            else if (parts[2].equals("l")) {
                // The flag is on the left half of the physical boundary
                x = Settings.FIELD().getCenter().getX() - offset;
            }
            else if (parts[2].equals("r")) {
                // The flag is on the right half of the physical boundary
                x = Settings.FIELD().getCenter().getX() + offset;
            }
            // Set the vertical value
            if (parts[1].equals("t")) {
                // The flag is on the top side of the physical boundary
                y = Settings.PHYSICAL_BOUNDARY().getTop();
            }
            else if (parts[1].equals("b")) {
                // The flag is on the bottom side of the physical boundary
                y = Settings.PHYSICAL_BOUNDARY().getBottom();
            }
            else if (parts[2].equals("t")) {
                // The flag is on the top half of the field
                y = Settings.FIELD().getCenter().getY() + offset;
            }
            else if (parts[2].equals("b")) {
                // The flag is on the bottom half of the field
                y = Settings.FIELD().getCenter().getY() - offset;
            }
        }
        if (x == -1 || y == -1) {
            System.err.println("Flag " + this.id + " didn't parse it's position correctly");
        }
        this.position = new PositionEstimate(x, y, 1.0, true);
    }
}
