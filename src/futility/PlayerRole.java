/** @file PlayerRole.java
 * Strategic player roles.
 *
 * @author Team F(utility)
 */

package futility;

/**
 * A class for housing information on player roles.
 */
public class PlayerRole {
    
    /**
     * The various player roles.
     */
    public enum Role {
        LEFT_WING,
        RIGHT_WING,
        STRIKER,
        LEFT_MIDFIELDER,
        CENTER_MIDFIELDER,
        RIGHT_MIDFIELDER,
        LEFT_DEFENDER,
        RIGHT_DEFENDER,
        SWEEPER,
        GOALIE
    }
    
    /**
     * Returns an indication of whether a player with a given role is a defender.
     * 
     * @param role the player's role
     * @return true if the player is a defender
     */
    public static final boolean isDefender(Role role) {
    	return role == Role.LEFT_DEFENDER || role == Role.RIGHT_DEFENDER || role == Role.SWEEPER;
    }
    
    /**
     * Returns an indiciation of whether a player with a given role is a wing.
     * 
     * @param role the player's role
     * @return true if the player is a wing
     */
    public static final boolean isWing(Role role) {
        return role == Role.LEFT_WING || role == Role.RIGHT_WING;
    }
    
    /**
     * @param role in question
     * @return true if the role is an offensive position 
     */
    public static boolean isOnOffense(Role role){
    	final boolean onD = PlayerRole.isDefender(role);
    	return !onD && role != Role.GOALIE;
    }
}
