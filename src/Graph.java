import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
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
    Queue<Localisation>  aVisiter = new ArrayDeque<>();
    Set<Long> dejaInondes = new HashSet<>();
    List<Localisation> ordreInondation = new ArrayList<>();

    // Initialisation avec les points de départs de l'inondation
    for (long id: idsOrigin){
      Localisation depart = this.localisations.get(id);
      aVisiter.add(depart);
      dejaInondes.add(id);
      ordreInondation.add(depart);
    }

    // Boucle du BFS
    while (!aVisiter.isEmpty()){
      Localisation noeudCourant = aVisiter.poll();
      List<Arc> arcsSortants = this.listeAdjacence.get(noeudCourant.getId());

      if (arcsSortants != null){
        for (Arc arc: arcsSortants){
          Localisation voisin = arc.getPointArrivee();

          boolean penteFavorable = voisin.getAltitude() < (noeudCourant.getAltitude() + epsilon);

          if (penteFavorable && !dejaInondes.contains(voisin.getId())){
            aVisiter.add(voisin);
            dejaInondes.add(voisin.getId());
            ordreInondation.add(voisin);
          }
        }
      }
    }
    return ordreInondation.toArray(new Localisation[0]);
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
