package futility;

import java.util.LinkedList;

/**
 * Futil: F(utility) utilities.
 */
public final class Futil {
    
    /**
     * Extracts the arguments from a correctly-formatted object info string.
     * 
     * @param info correctly-formatted object info string
     * @return array of arguments as strings
     */
    public static final String[] extractArgs(String info) {
        if (!isCorrectlyFormatted(info)) {
            return new String[] {};
        }
        int beginIndex = 2 + extractId(info).length();  // account for '(' and space separator following id
        int endIndex = info.length() - 1;  // don't include final char, ')'
        return info.substring(beginIndex, endIndex).split("\\s");
    }
    
    /**
     * Extracts object info strings from a `see` message.
     * 
     * @param see `see` message
     * @return object info strings
     */
    public static final LinkedList<String> extractInfos(String see) {
        int beginIndex = see.indexOf("((");
        int endIndex = see.length() - 1 ;  // don't include final ')'
        if (beginIndex == -1) {
            return new LinkedList<String>();
        }
        return stringToList(see.substring(beginIndex, endIndex));
    }
    
    /**
     * Gets the object id from an ObjectInfo string
     * 
     * @param info ObjectInfo string
     * @return the object id
     */
    public static final String extractId(String info) {
        if (!isCorrectlyFormatted(info)) {
            return "UNKNOWN";
        }
        int beginIndex = 1;  // the opening parenthesis of the object's id
        int endIndex = findClosingParenthesis(info, 1);
        String result = info.substring(beginIndex, endIndex + 1);
        // (B) and (b) are the same object so we force the use of (b) in order to correctly
        // associate the ball with a single field object in the HashMap.
        if (result.equals("(B)")) {
            result = "(b)";
        }
        return result; 
    }
    
    /**
     * Given a correctly-formatted `see` or `sense_body` message, returns the soccer server time
     * contained within the message.
     * 
     * @param s correctly-formatted `see` or `sense_body` message
     * @return soccer server time contained within the message
     */
    public static final int extractTime(String s) {
        if (!isCorrectlyFormatted(s)) {
            return -1;
        }
        if (!s.startsWith("(see ") && !s.startsWith("(sense_body ")) {
            Log.e("`extractTime` expected  a message starting with '(see' or '(sense_body', got: " + s);
            return -1;
        }
        String timeArg = s.split("\\s")[1];
        if (timeArg.endsWith(")")) {
            timeArg = timeArg.substring(0, timeArg.length() - 1);  // remove ')'
        }
        return Integer.parseInt(timeArg);
    }
    
    /**
     * Returns the index of the matching closing parenthesis, given a string and the index of the opening parenthesis to match.
     * 
     * @param s correctly-formatted string
     * @param beginIndex index of the opening parenthesis to match
     * @return index of the matching closing parenthesis
     */
    public static final int findClosingParenthesis(String s, int beginIndex) {
        if (!isCorrectlyFormatted(s)) {
            return -1;
        }
        if (s.charAt(beginIndex) != '(') {
            Log.e(s + " at index " + beginIndex + " is not '('.");
            return -1;
        }
        int numOpenParens = 0;
        int numOpenParensAtBeginIndex = -1;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                if (i == beginIndex) {
                    numOpenParensAtBeginIndex = numOpenParens;
                }
                numOpenParens++;
            }
            else if (s.charAt(i) == ')') {
                numOpenParens--;
                if (numOpenParens == numOpenParensAtBeginIndex) {
                    return i;
                }
            }
        }
        Log.e("Could not find matching closing ')' for the '(' at index " + beginIndex + " in '" + s + "'.");
        return -1;
    }
    
    /**
     * Returns true if an object is specific enough to identify a unique field object.
     * 
     * @param id the object id
     * @return true if the object is specific enough to identify a unique field object
     */
    public static final boolean isUniqueFieldObject(String id) {
        if (id.startsWith("(F") || id.startsWith("(G") || id.startsWith("(P") || id.startsWith("(l")) {
            return false;
        }
        return true;
    }
    
    /**
     * Tests that a string is correctly formatted--that is, it starts with an opening parenthesis,
     * ends with a closing parenthesis, has the same number of opening and closing parentheses, 
     * and that the number of currently-open parentheses is never less than 0.
     * 
     * @return true if the string is correctly-formatted
     */
    public static final boolean isCorrectlyFormatted(String s) {
        int numOpenParens = 0;
        if (s.charAt(0) != '(') {
            Log.e("String starts with '" + s.charAt(0) + "', not '('. String: " + s);
            return false;
        }
        if (s.charAt(s.length() - 1) != ')') {
            Log.e("String ends with '" + s.charAt(s.length() - 1) + "', not ')'. String: " + s);
            return false;
        }
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                numOpenParens++;
            }
            else if (s.charAt(i) == ')') {
                numOpenParens--;
                if (numOpenParens < 0) {
                    Log.e("String has less than 0 open parentheses at some index: " + s);
                    return false;
                }
            }
        }
        if (numOpenParens != 0) {
            Log.e("String has unequal number of parentheses: " + s);
        }
        return true;
    }
    
    public static final String sanitize(String s) {
        s = s.trim();
        int beginIndex = 0;
        int endIndex = s.length();
        while (s.charAt(endIndex - 1) != ')') {
            endIndex--;           
        }
        while (s.charAt(beginIndex) != '(') {
            beginIndex++;
        }
        String result = s.substring(beginIndex, endIndex);
        if (!Futil.isCorrectlyFormatted(result)) {
            return "";
        }
        return result;
    }
    
    /**
     * Turns a string of objects into a list of objects.
     * 
     * @param s string of objects
     */
    public static final LinkedList<String> stringToList(String s) {
        int numOpenParens = 0;
        int beginIndex = -1;  // beginning index of an object substring
        int endIndex = -1; // end index of an object substring
        LinkedList<String> objects = new LinkedList<String>();
        if (!isCorrectlyFormatted(s)) {
            return objects;
        }
        for (int i=0; i<s.length(); i++) {
            if (s.charAt(i) == '(') {
                numOpenParens++;
                if (numOpenParens == 1) {
                    beginIndex = i;
                }
            }
            else if (s.charAt(i) == ')') {
                numOpenParens--;
                if (numOpenParens == 0) {
                    endIndex = i + 1;
                    objects.add(s.substring(beginIndex, endIndex));
                }
            }
        }
        return objects;
    }
    
    /**
     * Simplifies an angle to within [-180, 180] degrees.
     * 
     * @param angle angle to simplify
     * @return an equivalent angle within [-180, 180] degrees
     */
    public static final double simplifyAngle(double angle) {
        while (angle > 180.0) {
            angle -= 360.0;
        }
        while (angle < -180.0) {
            angle += 360.0; 
        }
        return angle;
    }
    
    /**
     * Given a target angle, returns the closest similar angle within the minimum and maximum 
     * moment. Assumes maximum moment <= 180.0 degrees and minimum moment >= -180.0 degrees.
     * 
     * @param moment
     * @return
     */
    public static final double toValidMoment(double angle) {
        angle = simplifyAngle(angle);
        if (angle > Settings.PLAYER_PARAMS.MOMENT_MAX) {
            angle = Settings.PLAYER_PARAMS.MOMENT_MAX;
        }
        else if (angle < Settings.PLAYER_PARAMS.MOMENT_MIN) {
            angle = Settings.PLAYER_PARAMS.MOMENT_MIN;
        }
        return angle;
    }
}
