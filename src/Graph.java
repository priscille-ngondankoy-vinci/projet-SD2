import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {

  private Map<Long, List<Arc>> listeAdjacence;
  private Map<Long, Localisation> localisations;
  //ATTRIBUT ?
  //TODO

  public Graph(String localisations, String roads) {
    this.listeAdjacence = new HashMap<>();
    this.localisations = new HashMap<>();
  }

  public Localisation[] determinerZoneInondee(long[] idsOrigin, double epsilon) {
    //TODO
    return null;
  }

  public Deque<Localisation> trouverCheminLePlusCourtPourContournerLaZoneInondee(long idOrigin,
      long idDestination, Localisation[] floodedZone) {
    //TODO
    return null;
  }

  public Map<Localisation, Double> determinerChronologieDeLaCrue(long[] idsOrigin,
      double vWaterInit, double k) {
    //TODO
    return null;
  }

  public Deque<Localisation> trouverCheminDEvacuationLePlusCourt(long idOrigin, long idEvacuation,
      double vVehicule, Map<Localisation, Double> tFlood) {
    //TODO
    return null;
  }


}
