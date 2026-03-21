import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Graph {

  private Map<Long, List<Arc>> listeAdjacence;
  private Map<Long, Localisation> localisations;
  //ATTRIBUT ?
  //TODO

  public Graph(String localisations, String roads) {
    this.listeAdjacence = new HashMap<>();
    this.localisations = new HashMap<>();

    Lecteur l = new Lecteur(this.listeAdjacence, this.localisations);
    l.chargerLocalisations(localisations);
    l.chargerArcs(roads);
  }

  public Localisation[] determinerZoneInondee(long[] idsOrigin, double epsilon) {
    // garde l'ordre d'ajout //
    ArrayList<Localisation> listeLocalisationInondee = new ArrayList<>();

    // points qui sont déjà passé dans l'ago //
    Set<Long> parcouru = new HashSet<>();

    Queue<Localisation> queue = new ArrayDeque<>();
    for (Long id : idsOrigin) {
      queue.add(localisations.get(id));
      parcouru.add(id);
    }

    // TODO

    return listeLocalisationInondee.toArray(new Localisation[0]);
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
