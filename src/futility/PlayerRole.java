package futility;

public class PlayerRole {
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
     * @param role in question
     * @return true if the role is a defensive position and not the goalie
     */
    public static boolean isOnDefenseAndNotGoalie(Role role){
    	if(role == Role.LEFT_DEFENDER || role == Role.RIGHT_DEFENDER || role == Role.SWEEPER) return true;
    	else return false;
    }
    
    /**
     * @param role in question
     * @return true if the role is an offensive position 
     */
    public static boolean isOnOffense(Role role){
    	final boolean onD = isOnDefenseAndNotGoalie(role);
    	return !onD && role != Role.GOALIE;
    }
}
