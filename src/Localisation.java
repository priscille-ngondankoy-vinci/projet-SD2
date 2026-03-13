public class Localisation {
    private long id;
    private double altitude;

    public long getId() {
        return id;
    }

    public double getAltitude() {
        return altitude;
    }

    public Localisation(long id, double altitude) {
        this.id = id;
        this.altitude = altitude;
    }
}
