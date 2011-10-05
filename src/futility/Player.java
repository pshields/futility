package futility;

public class Player extends MobileObject {
    public Brain brain;
    public Client client;
    public int number;
    public Team otherTeam = new Team();
    public Team team = new Team();
    
    public Player() {
    }
    
    public Player(int number) {
        this.number = number;
    }
    
    public Player(Client client) {
        brain = new Brain(this);
        this.client = client;
    }
}
