package nz.ac.canterbury.seng302.portfolio.utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper functions for additional functionality of the application.
 */
public class GeneralUtility {

    private GeneralUtility() {
        // Empty initializer
    }

    /**
     * Gets the location of which branch/vm the program is running on.
     * @param dataSource    This relates to the applications' property file being used.
     * @return 'dev', 'test' or 'prod', depending on the branch/vm.
     */
    public static String getApplicationLocation(String dataSource) {
        if (dataSource.contains("seng302-2022-team100")) {
            if (dataSource.contains("test")) {
                return "test";
            } else {
                return "prod";
            }
        }
        return "dev";
    }

    /**
     * Converts a JSON string to a Mapping of attribute names to their values.
     * @param request Request string.
     * @return Mapped list of attributes.
     */
    public static Map<String, String> requestBodyToHashMap(String request) {
        HashMap<String, String> map = new HashMap<>();
        String[] pairs = request.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx != -1) {
                map.put(pair.substring(0, idx), pair.substring(idx + 1).replace("%2F", "/").replace("+", " "));
            }
        }
        return map;
    }
}
