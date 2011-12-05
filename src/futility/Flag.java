/** @file Flag.java
 * Representation of the flags that define the field.
 *
 * @author Team F(utility)
 */

package futility;

/**
 * Extension of StationaryObject that represents an object on the visible playing field.
 */
public class Flag extends StationaryObject {
    
    /**
     * This constructor automatically assign's the flag's position based on its ObjectId.
     * 
     * @param id the flag's ObjectId
     */
    public Flag(String id) {
        this.id = id;
        this.position = this.setPosition();
    }

    /**
     * Returns this flag's position based on its ObjectId.
     * 
     * @return this flag's position based on its ObjectId
     */
    protected PositionEstimate setPosition() {
    	//A switch{} would be better, but not sure if we will have JRE 1.7 available
    	if (this.id == "(f t l 50)") {
            return new PositionEstimate(-50, -39, 1.0, -1);
        }
    	else if (this.id == "(f t l 40)") {
            return new PositionEstimate(-40, -39, 1.0, -1);
        }
    	else if (this.id == "(f t l 30)") {
            return new PositionEstimate(-30, -39, 1.0, -1);
        }
    	else if (this.id == "(f t l 20)") {
            return new PositionEstimate(-20, -39, 1.0, -1);
        }
    	else if (this.id == "(f t l 10)") {
            return new PositionEstimate(-10, -39, 1.0, -1);
        }
    	else if (this.id == "(f t 0)") {
            return new PositionEstimate(0, -39, 1.0, -1);
        }
    	else if (this.id == "(f t r 10)") {
            return new PositionEstimate(10, -39, 1.0, -1);
        }
    	else if (this.id == "(f t r 20)") {
            return new PositionEstimate(20, -39, 1.0, -1);
        }
    	else if (this.id == "(f t r 30)") {
            return new PositionEstimate(30, -39, 1.0, -1);
        }
    	else if (this.id == "(f t r 40)") {
            return new PositionEstimate(40, -39, 1.0, -1);
        }
    	else if (this.id == "(f t r 50)") {
            return new PositionEstimate(50, -39, 1.0, -1);
        }
    	else if (this.id == "(f r t 30)") {
            return new PositionEstimate(57.5, -30, 1.0, -1);
        }
    	else if (this.id == "(f r t 20)") {
            return new PositionEstimate(57.5, -20, 1.0, -1);
        }
    	else if (this.id == "(f r t 10)") {
            return new PositionEstimate(57.5, -10, 1.0, -1);
        }
    	else if (this.id == "(f r 0)") {
            return new PositionEstimate(57.5, 0, 1.0, -1);
        }
    	else if (this.id == "(f r b 10)") {
            return new PositionEstimate(57.5, 10, 1.0, -1);
        }
    	else if (this.id == "(f r b 20)") {
            return new PositionEstimate(57.5, 20, 1.0, -1);
        }
    	else if (this.id == "(f r b 30)") {
            return new PositionEstimate(57.5, 30, 1.0, -1);
        }
    	else if (this.id == "(f b r 50)") {
            return new PositionEstimate(50, 39, 1.0, -1);
        }
    	else if (this.id == "(f b r 40)") {
            return new PositionEstimate(40, 39, 1.0, -1);
        }
    	else if (this.id == "(f b r 30)") {
            return new PositionEstimate(30, 39, 1.0, -1);
        }
    	else if (this.id == "(f b r 20)") {
            return new PositionEstimate(20, 39, 1.0, -1);
        }
    	else if (this.id == "(f b r 10)") {
            return new PositionEstimate(10, 39, 1.0, -1);
        }
    	else if (this.id == "(f b 0)") {
            return new PositionEstimate(0, 39, 1.0, -1);
        }
    	else if (this.id == "(f b l 10)") {
            return new PositionEstimate(-10, 39, 1.0, -1);
        }
    	else if (this.id == "(f b l 20)") {
            return new PositionEstimate(-20, 39, 1.0, -1);
        }
    	else if (this.id == "(f b l 30)") {
            return new PositionEstimate(-30, 39, 1.0, -1);
        }
    	else if (this.id == "(f b l 40)") {
            return new PositionEstimate(-40, 39, 1.0, -1);
        }
    	else if (this.id == "(f b l 50)") {
            return new PositionEstimate(-50, 39, 1.0, -1);
        }
    	else if (this.id == "(f l b 30)") {
            return new PositionEstimate(-57.5, 30, 1.0, -1);
        }
    	else if (this.id == "(f l b 20)") {
            return new PositionEstimate(-57.5, 20, 1.0, -1);
        }
    	else if (this.id == "(f l b 10)") {
            return new PositionEstimate(-57.5, 10, 1.0, -1);
        }
    	else if (this.id == "(f l 0)") {
            return new PositionEstimate(-57.5, 0, 1.0, -1);
        }
    	else if (this.id == "(f l t 10)") {
            return new PositionEstimate(-57.5, -10, 1.0, -1);
        }
    	else if (this.id == "(f l t 20)") {
            return new PositionEstimate(-57.5, -20, 1.0, -1);
        }
    	else if (this.id == "(f l t 30)") {
            return new PositionEstimate(-57.5, -30, 1.0, -1);
        }
    	else if (this.id == "(f l t)") {
            return new PositionEstimate(-52.5, -34, 1.0, -1);
        }
    	else if (this.id == "(f r t)") {
            return new PositionEstimate(52.5, -34, 1.0, -1);
        }
    	else if (this.id == "(f r b)") {
            return new PositionEstimate(52.5, 34, 1.0, -1);
        }
    	else if (this.id == "(f l b)") {
            return new PositionEstimate(-52.5, 34, 1.0, -1);
        }
    	else if (this.id == "(f c t)") {
            return new PositionEstimate(0, -34, 1.0, -1);
        }
    	else if (this.id == "(f c)") {
            return new PositionEstimate(0, 0, 1.0, -1);
        }
    	else if (this.id == "(f c b)") {
            return new PositionEstimate(0, 34, 1.0, -1);
        }
    	else if (this.id == "(f p l t)") {
            return new PositionEstimate(-36, -20.15, 1.0, -1);
        }
    	else if (this.id == "(f p l c)") {
            return new PositionEstimate(-36, 0, 1.0, -1);
        }
    	else if (this.id == "(f p l b)") {
            return new PositionEstimate(-36, 20.15, 1.0, -1);
        }
    	else if (this.id == "(f p r t)") {
            return new PositionEstimate(36, -20.15, 1.0, -1);
        }
    	else if (this.id == "(f p r c)") {
            return new PositionEstimate(36, 0, 1.0, -1);
        }
    	else if (this.id == "(f p r b)") {
            return new PositionEstimate(36, 20.15, 1.0, -1);
        }
    	else if (this.id == "(f g l t)") {
            return new PositionEstimate(-52.5, -7.01, 1.0, -1);
        }
    	else if (this.id == "(f g l b)") {
            return new PositionEstimate(-52.5, 7.01, 1.0, -1);
        }
    	else if (this.id == "(f g r t)") {
            return new PositionEstimate(52.5, -7.01, 1.0, -1);
        }
    	else if (this.id == "(f g r b)") {
            return new PositionEstimate(52.5, 7.01, 1.0, -1);
        }
    	return new PositionEstimate(Double.NaN, Double.NaN, 0.0, -1);
    }
}
