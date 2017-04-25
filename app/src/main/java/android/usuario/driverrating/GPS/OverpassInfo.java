package android.usuario.driverrating.GPS;

/**
 * Created by Jorge on 09/03/2017.
 */

public class OverpassInfo {
    private String name;
    private String maxspeed;
    private String highway;
    private String lanes;
    private String surface;

    public OverpassInfo() {
        name = "unknown";
        maxspeed = "unknown";;
        highway = "unknown";
        lanes = "unknown";
        surface = "unknown";
    }
    public void setName(String name) {
        this.name=name;
    }
    public String getName() {
        return name;
    }
    public void setMaxspeed(String maxspeed) {
            this.maxspeed=maxspeed;
    }
    public String getMaxspeed() {
        return maxspeed;
    }
    public void setHighway(String highway) {
        this.highway=highway;
    }
    public String getHighway() {
        return highway;
    }
    public void setLanes(String lanes) {
            this.lanes=lanes;
    }
    public String getLanes() {
        return lanes;
    }
    public void setSurface(String surface) {
        this.surface=surface;
    }
    public String getSurface() {
        return surface;
    }
}
