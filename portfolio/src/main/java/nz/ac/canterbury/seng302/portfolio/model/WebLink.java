package nz.ac.canterbury.seng302.portfolio.model;

import javax.persistence.*;

/**
 * WebLink is associated with a piece of evidence and contains a url.
 */
@Entity
public class WebLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int weblinkId;

    private String url;

    /**
     * Empty constructor for JPA.
     */
    public WebLink() {
    }

    /**
     * Constructor for WebLink.
     * @param url Url to set for the WebLink.
     */
    public WebLink(String url) {
        this.url = url;
    }

    public int getWeblinkId() {
        return weblinkId;
    }

    public void setWeblinkId(int weblinkId) {
        this.weblinkId = weblinkId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
