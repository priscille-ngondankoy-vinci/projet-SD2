public class Localisation {

  private final long id;
  private final double altitude;
  private final double longitude;
  private final double latitude;
  private final String nom;

  public Localisation(long id, double altitude, double longitude, double latitude, String nom) {
    this.id = id;
    this.altitude = altitude;
    this.longitude = longitude;
    this.latitude = latitude;
    this.nom = nom;
  }

  public long getId() {
    return id;
  }

  public double getAltitude() {
    return altitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public String getNom() {
    return nom;
  }
}
