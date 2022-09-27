package nz.ac.canterbury.seng302.portfolio.model;

/**
 * This class is a model which used to replicate the user(s) who has high five to a certain piece of evidence
 */
public class HighFivers {
    private String name;
    private Integer userId;

    /**
     * Basic constructor for high fivers object
     * @param name the name of the user, this consists of first name of the user followed by last name of the user
     * @param id the id of that user
     */
    public HighFivers(String name, int id) {
        this.name = name;
        this.userId = id;
    }

    /**
     * get the name of high fiver
     * @return the name of the high fiver
     */
    public String getName() {
        return name;
    }

    /**
     * set the name of the high fiver
     * @param name new/initial name of the high fiver
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * return the id of the high fiver
     * @return an integer which indicates the id of the high fiver
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * set the new/initial id of the high fiver, This is the id of the user that stored in idp
     * @param userId new/initial id of the high fiver
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
