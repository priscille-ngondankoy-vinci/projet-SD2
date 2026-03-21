import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

    Lecteur l = new Lecteur(this.listeAdjacence, this.localisations);
    l.chargerLocalisations(localisations);
    l.chargerArcs(roads);
  }

  public Localisation[] determinerZoneInondee(long[] idsOrigin, double epsilon) {
      Localisation[] loc = new Localisation[idsOrigin.length];
      int index = 0;
      for(Long l : idsOrigin) {
           if (this.localisations.containsKey(l)) {
               List<Arc> liste = this.listeAdjacence.get(l);
               for(Arc a : liste) {
                   if (a.getPointOrigine().getNom().equals(a.getPointArrivee().getNom()) &&
                           a.getPointArrivee().getAltitude() < (a.getPointOrigine().getAltitude())+epsilon) {
                       loc[index] = this.localisations.get(l);
                       index++;

                   }
               }

           }
      }
    //TODO
    return loc;
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
