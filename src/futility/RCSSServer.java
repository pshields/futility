package futility;

public class RCSSServer {
    public Process process;
    
    /**
     * Starts an rcssserver process.
     */
    public void start() {
        try {
            Runtime runtime = Runtime.getRuntime();
            this.process = runtime.exec("rcssserver");
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }
    
    /**
     * Restarts an rcssserver process.
     */
    public void restart() {
        this.stop();
        this.start();
    }
    
    public void stop() {
        this.process.destroy();
    }
}
