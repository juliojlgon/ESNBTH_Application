package se.ESNBTH.esnbth.RequestHelper;

/**
 * Created by JulioLopez on 21/1/15.
 */
public class Feed {
    private String title;
    private String description;
    private String createdAt;

    public Feed(){}


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
