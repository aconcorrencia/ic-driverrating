package android.usuario.driverrating.domain;

/**
 * Created by Sillas on 21/09/2016.
 */
public class ListViewObj {
    private String title;
    private String description;

    public ListViewObj() {
    }

    public ListViewObj(String title, String description) {
        this.description = description;
        this.title = title;
    }

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

    @Override
    public String toString() {
        return "ListViewObj{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
