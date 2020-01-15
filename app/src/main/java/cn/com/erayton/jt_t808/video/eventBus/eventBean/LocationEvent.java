package cn.com.erayton.jt_t808.video.eventBus.eventBean;

/**
 * Created by Administrator on 2018/12/29.
 */

public class LocationEvent {
    private int type;
    private double lat;
    private double lon;

    public LocationEvent( int type,double lat,double lon){
        this.type=type;
        this.lat=lat;
        this.lon=lon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
}
