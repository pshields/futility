/** @file Player.java
 * Representation of a player object on the game field. May also represent this
 * game client's player agent.
 * 
 * @author Team F(utility)
 * @date 20 October 2011
 */

package futility;

/** @class Player
 * Extension of the MobileObject class that represents a player of a team on
 * the game field.
 */
public class Player extends MobileObject {
    public Brain brain;
    public Client client;
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
     * Initiate a player from soccer server name<br>
     * (p ["Teamname" [UniformNumber [goalie]]])
     * @param id
     */
    public Player(String id){
    	String[] parts = id.substring(1, id.length() - 1).split(" ");
    	switch(parts.length){
    	case 4:
    		//is goalie??
    	case 3:
    		number = Integer.valueOf(parts[2]);
    	case 2:
    		team.name = parts[1];
    	case 1:
    		
    		break;
    		default:
    	//TODO log error
    	}
    }
    
    /**
     * Build a player object on the game field with the given uniform number.
     * @param number the uniform number of the player.
     */
    public Player(int number) {
        this.number = number;
    }
    
    /**
     * Builds a representation of this game client's player agent.
     * @param client the interface for network communication
     */
    public Player(Client client) {
        brain = new Brain(this, client);
        this.client = client;
    }
}
