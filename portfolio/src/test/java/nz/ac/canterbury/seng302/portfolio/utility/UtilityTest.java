package nz.ac.canterbury.seng302.portfolio.utility;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests the static methods present within the Utility class.
 */
class UtilityTest {

    /**
     * Tests that when given a prod URL it returns a prod identifier.
     */
    @Test
    void GivenProdURL_ExpectProdReturn(){
        String sourceProd = "jdbc:mariadb://db2.csse.canterbury.ac.nz/seng302-2022-team100-portfolio-prod";
        String actual = Utility.getApplicationLocation(sourceProd);
        String expectedProd = "prod";
        assertEquals(expectedProd, actual);
    }

    /**
     * Tests that when given a test URL it returns a test identifier.
     */
    @Test
    void GivenTestURL_ExpectTestReturn(){
        String sourceTest = "jdbc:mariadb://db2.csse.canterbury.ac.nz/seng302-2022-team100-portfolio-test";
        String actual = Utility.getApplicationLocation(sourceTest);
        String expectedTest = "test";
        assertEquals(expectedTest, actual);
    }

    /**
     * Tests that when given a dev URL it returns a dev identifier.
     */
    @Test
    void GivenDevURL_ExpectDevReturn(){
        String sourceDev = "jdbc:mariadb://localhost:3306/jth141_portfolio-test";
        String actual = Utility.getApplicationLocation(sourceDev);
        String expectedDev = "dev";
        assertEquals(expectedDev, actual);
    }
}