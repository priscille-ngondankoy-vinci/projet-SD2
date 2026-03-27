import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
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
    Queue<Localisation> aVisiter = new ArrayDeque<>();
    Set<Long> dejaInondes = new HashSet<>();
    List<Localisation> ordreInondation = new ArrayList<>();

    // Initialisation avec les points de départs de l'inondation
    for (long id : idsOrigin) {
      Localisation depart = this.localisations.get(id);
      aVisiter.add(depart);
      dejaInondes.add(id);
      ordreInondation.add(depart);
    }

    // Boucle du BFS
    while (!aVisiter.isEmpty()) {
      Localisation noeudCourant = aVisiter.poll();
      List<Arc> arcsSortants = this.listeAdjacence.get(noeudCourant.getId());

      for (Arc arc : arcsSortants) {
        Localisation voisin = arc.getPointArrivee();

        boolean penteFavorable = voisin.getAltitude() <= (noeudCourant.getAltitude() + epsilon);

        if (penteFavorable && !dejaInondes.contains(voisin.getId())) {
          aVisiter.add(voisin);
          dejaInondes.add(voisin.getId());
          ordreInondation.add(voisin);
        }
      }
    }
    return ordreInondation.toArray(new Localisation[0]);
  }

  public Deque<Localisation> trouverCheminLePlusCourtPourContournerLaZoneInondee(long idOrigin,
      long idDestination, Localisation[] floodedZone) {
    //TODO

    // Plus opti de vérifier dans un set si le noeud fait partie de la zone inondée.
    // On doit parcourir la liste dans sa totalité uniquement lors de la transformation en set
    // plutôt qu'à chaque vérification dans le BFS.
    Set<Long> zoneInondeSet = new HashSet<>();
    for (Localisation loc : floodedZone) {
        zoneInondeSet.add(loc.getId());
    }

    Queue<Localisation> aVisiter = new ArrayDeque<>();
    Set<Long> dejaVisites = new HashSet<>();
    Map<Long, Localisation> cheminEmprunte = new HashMap<>();

    Localisation depart = this.localisations.get(idOrigin);
    aVisiter.add(depart);
    dejaVisites.add(idOrigin);

    // BFS
    while (!aVisiter.isEmpty()) {
      Localisation noeudCourant = aVisiter.poll();

      // Fin du parcours
      if (noeudCourant.getId() == idDestination) {
        return reconstruireCheminEmprunte(cheminEmprunte, depart, noeudCourant);
      }

      // Arrivée non atteinte -> on continue la recherche
      List<Arc> arcsSortants = this.listeAdjacence.get(noeudCourant.getId());
      for (Arc arc : arcsSortants){
        Long idVoisin = arc.getPointArrivee().getId();

        if (!dejaVisites.contains(idVoisin) && !zoneInondeSet.contains(idVoisin)){
          aVisiter.add(arc.getPointArrivee());
          dejaVisites.add(idVoisin);
          cheminEmprunte.put(idVoisin, noeudCourant);
        }
      }
    }

    return new LinkedList<>();
  }

  private Deque<Localisation> reconstruireCheminEmprunte(Map<Long, Localisation> cheminEmprunte,
      Localisation depart, Localisation arrivee) {
      Deque<Localisation> chemin = new LinkedList<>();
      Localisation noeud = arrivee;

      while ((noeud != null) && noeud.getId() != depart.getId()){
        chemin.addFirst(noeud);
        noeud = cheminEmprunte.get(noeud.getId());
      }

      chemin.addFirst(depart);
      return chemin;
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
