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

    /**
     * Creates a URL title that can be displayed to the user, rather than the full URL .
     * @return The URL to be displayed.
     */
    public String getFormattedUrl() {
        String stringUrl = this.url;
        String[] urlContent = stringUrl.split("://");

        if (urlContent.length < 2) {
            // When this is return to the HTMl page through Thymeleaf the link will not be displayed.
            stringUrl = "InvalidURL";
        } else {
            stringUrl = urlContent[1];
        }

        if (stringUrl.length() > 50) {
            stringUrl = stringUrl.substring(0, 50) + "...";
        }
        return stringUrl;
    }

    /**
     * Returns if the URL has an HTTPS start. Meaning it is secure.
     * @return If the URL is secure.
     */
    public boolean isSecure() {
        return this.url.startsWith("https");
    }
}
