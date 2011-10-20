package futility;

public class Main {
    
    /** Main function
    *
    * First function to execute. Reads command-line arguments and activates
    * one or more clients as specified in the args.
    * 
    * The ability to activate multiple clients here is meant as a CPU-saving
    * technique for development. When competing, use the separate spin-up
    * script to ensure complete process isolation. 
    * 
    * @param args command-line arguments
    */
   public static void main(String[] args) {
       boolean customStart = false;
       for (int i = 0; i < args.length; i++ )
       {
           // Depending on these settings, initalize one or more clients
           if (args[i].equals("-c") || args[i].equals("--compete")) {
               customStart = true;
               startTeam(args);
               startTeam(args, Settings.OTHER_TEAM_NAME);
           }
           else if (args[i].equals("-s") || args[i].equals("--start-team")) {
               customStart = true;
               startTeam(args);
           }
       }
       if (!customStart) {
           initClient(args);
       }
   }
   
   /** Initialize a new client
    * 
    * @param args arguments to treat as if they were command-line arguments
    */
   public static final void initClient(String[] args) {
       Client client = new Client(args);
       client.init();
   }
   
   /** Initialize a client for a specific team
    * 
    * @param args arguments to treat as if they were command-line arguments
    * @param teamName a team name to override any other defaults
    */
   public static final void initClient(String[] args, String teamName) {
       Client client = new Client(args);
       client.player.team.name = teamName;
       client.init();
   }
   
   /** Start a team of clients with the given arguments
    * 
    * @param args command-line arguments to pass to the created clients
    */
   public static final void startTeam(String[] args) {
       for (int i=0; i<=10; i++) {
           initClient(args);
       }
   }
   
   /** Start a team of clients with arguments and a team name
    * 
    * @param args command-line arguments to pass to the created clients
    * @param teamName a team name to override all others
    */
   public static final void startTeam(String[] args, String teamName) {
       for (int i=0; i<=10; i++) {
           initClient(args, teamName);
       }
   }
}
