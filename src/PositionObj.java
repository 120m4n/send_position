import org.json.JSONObject;

public class PositionObj {
    private double lat;
    private double lon;
    private String fleet;
    private String userid;

    public PositionObj(double lat, double lon, String fleet, String userid) {
        this.lat = lat;
        this.lon = lon;
        this.fleet = fleet;
        this.userid = userid;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        JSONObject position = new JSONObject();
        position.put("lat", this.lat);
        position.put("lon", this.lon);
        obj.put("position", position);
        obj.put("fleet", this.fleet);
        obj.put("userid", this.userid);
        return obj;
    }

    public String toJSONString() {
        return this.toJSON().toString();
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getFleet() {
        return fleet;
    }

    public void setFleet(String fleet) {
        this.fleet = fleet;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
