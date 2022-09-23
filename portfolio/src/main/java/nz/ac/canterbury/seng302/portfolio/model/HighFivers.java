package nz.ac.canterbury.seng302.portfolio.model;


public class HighFivers {
    private String name;
    private Integer userId;

    public HighFivers(String name, int id) {
        this.name = name;
        this.userId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
