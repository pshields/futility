/** @file Player.java
 * Representation of a player object on the game field. May also represent this
 * game client's player agent.
 * 
 * @author Team F(utility)
 */

package futility;

/**
 * Representation of a player on the field.
 */
public class Player extends MobileObject {
    public Brain brain = null;
    public Client client = null;
    public int number;
    public Team otherTeam = new Team();
    public Team team = new Team();

    /**
     * Default constructor, builds a Player with no central logic data or
     * known information.
     */
    public Player() {
    }

    /**
     * Initiates a player from an ObjectId.
     *
     * @param id the player's ObjectId
     */
    public Player(String id){
    	String[] parts = id.substring(1, id.length() - 1).split(" ");
    	switch(parts.length){
    	case 5:
    		//is kicking/tackling? 
    	case 4:
    		//is goalie??
    	case 3:
    		number = Integer.valueOf(parts[2]);
    	case 2:
    		team.name = parts[1];
    	case 1:
    		
    		break;
    	default:
    		Log.e("Error parsing a player unexpected number of attributes. " + id);
    	}
    }
    
    /**
     * Build a player object on the game field with the given uniform number.
     * 
     * @param number the uniform number of the player.
     */
    public Player(int number) {
        this.number = number;
    }
    
    /**
     * Builds a representation of this game client's player agent.
     * 
     * @param client the interface for network communication
     */
    public Player(Client client) {
        brain = new Brain(this, client);
        this.client = client;
    }
    
    /**
     * Returns the ObjectId of the opponent team's goal.
     * 
     * @return the ObjectId of the opponent team's goal
     */
    public String getOpponentGoalId() {
        return new String("(g " + (this.team.side == 'r' ? "l" : "r") + ")");
    }
    
    /**
     * Returns the ObjectId of this team's goal.
     * 
     * @return the ObjectId of this team's goal
     */
    public String getGoalId() {
        return new String("(g " + this.team.side + ")");
    }
    
    /**
     * Returns true if this Player has a brain associated with it.
     * 
     * @return true if this Player has a brain associated with it
     */
    public boolean hasBrain() {
        return this.brain != null;
    }
    
    /**
     * Returns a string representing this player.
     * 
     * @return a string representing this player
     */
    public final String render() {
        return "Player " + String.valueOf(this.number) + " on team " + this.team.name;
    }
    
    /**
     * Returns the estimated velocity of this Player.
     * 
     * @return the estimated velocity of this Player
     */
    public VelocityVector velocity() {
        if (this.hasBrain()) {
            return this.brain.velocity;
        }
        else {
            return super.velocity();
        }
    }
}
