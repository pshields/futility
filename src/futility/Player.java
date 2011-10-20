package futility;

public class Player extends MobileObject {
    public Brain brain;
    public Client client;
    public int number;
    public Team otherTeam = new Team();
    public Team team = new Team();
    
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
    
    public Player(int number) {
        this.number = number;
    }
    
    public Player(Client client) {
        brain = new Brain(this, client);
        this.client = client;
    }
}
