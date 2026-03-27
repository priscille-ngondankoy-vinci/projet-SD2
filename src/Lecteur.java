import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Lecteur {

  Map<Long, List<Arc>> listeAdjadences;
  Map<Long, Localisation> localisations;

  public Lecteur(Map<Long, List<Arc>> listeAdjadences, Map<Long, Localisation> localisations) {
    this.listeAdjadences = listeAdjadences;
    this.localisations = localisations;
  }

  public void chargerLocalisations(String pathLocalisations) {
    try (BufferedReader br = new BufferedReader(new FileReader(pathLocalisations))) {
      String ligne;

      br.readLine();

      while ((ligne = br.readLine()) != null) {
        String[] valeurs = ligne.split(",");

        long id = Long.parseLong(valeurs[0]);
        String nom = valeurs[1];
        double latitude = Double.parseDouble(valeurs[2]);
        double longitude = Double.parseDouble(valeurs[3]);
        double altitude = Double.parseDouble(valeurs[4]);

        Localisation loc = new Localisation(id, altitude, longitude, latitude, nom);
        localisations.put(id, loc);

        listeAdjadences.put(id, new ArrayList<>());
      }
    } catch (IOException e) {
      System.err.println("Erreur lors de la lecture du fichier des noeuds : " + e.getMessage());
    }
  }

  public void chargerArcs(String pathRoads) {
    try (BufferedReader br = new BufferedReader(new FileReader(pathRoads))) {
      String ligne;

      br.readLine();

      while ((ligne = br.readLine()) != null) {
        String[] valeurs = ligne.split(",");

        long idOrigine = Long.parseLong(valeurs[0]);
        long idArrivee = Long.parseLong(valeurs[1]);
        double distance = Double.parseDouble(valeurs[2]);
        String nomRue = valeurs[3];

        Localisation origine = localisations.get(idOrigine);
        Localisation arrivee = localisations.get(idArrivee);

        if (origine != null && arrivee != null) {
          Arc arc = new Arc(origine, arrivee, nomRue, distance);

          listeAdjadences.get(idOrigine).add(arc);
        }
      }
    } catch (IOException e) {
      System.err.println("Erreur lors de la lecture du fichier des routes : " + e.getMessage());
    }
    // Trier permet de rendre les tests 1 000 000 complètement correct mais rend incorrect les résultats 10 et 1 000b.
    // Il semble y avoir des incohérence entre le calcul des résultats de ces tests et ceux des 1 000 000 et 1 000a

    // Ici, on trie une seule fois à la fin du chargement des données pour ne pas
    // avoir à trier dans nos algorithmes ce qui couterait beaucoup de temps.
    // for (List<Arc> arcs : this.listeAdjadences.values()) {
    //   arcs.sort(Comparator.comparingDouble((Arc arc) -> arc.getPointArrivee().getAltitude()));
    // }
  }
}
