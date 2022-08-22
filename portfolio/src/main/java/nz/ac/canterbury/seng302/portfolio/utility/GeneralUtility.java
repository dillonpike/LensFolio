package nz.ac.canterbury.seng302.portfolio.utility;

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
}
