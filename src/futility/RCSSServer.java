/** @file RCSSServer.java
 * Class for interacting with the 'rcssserver' software.
 * 
 * @author Team F(utility)
 */

package futility;

/**
 * An rcssserver process.
 */
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
    
    /**
     * Stops an rcssserver process.
     */
    public void stop() {
        this.process.destroy();
    }
}
