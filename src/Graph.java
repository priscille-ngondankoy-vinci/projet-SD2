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

    chargerLocalisations(localisations);
    chargerArcs(roads);
  }

  private void chargerLocalisations(String pathLocalisations) {
    // try-with-resources pour fermer le fichier automatiquement à la fin
    try (BufferedReader br = new BufferedReader(new FileReader(pathLocalisations))) {
      String ligne;

      // Décommenter la ligne ci-dessous si votre fichier CSV a une ligne d'en-tête (titres des colonnes)
      // br.readLine();

      while ((ligne = br.readLine()) != null) {
        // On coupe la ligne à chaque virgule (ou point-virgule selon votre CSV)
        String[] valeurs = ligne.split(",");

        // ATTENTION: Adaptez les indices [0], [1]... selon l'ordre exact de vos colonnes dans le CSV !
        long id = Long.parseLong(valeurs[0]);
        double latitude = Double.parseDouble(valeurs[1]);
        double longitude = Double.parseDouble(valeurs[2]);
        String nom = valeurs[3];
        double altitude = Double.parseDouble(valeurs[4]);

        // Création du noeud [cite: 25-30]
        Localisation loc = new Localisation(id, altitude, longitude, latitude, nom);
        this.localisations.put(id, loc);

        // CRUCIAL : On prépare tout de suite une liste d'arcs vide pour ce noeud
        this.listeAdjacence.put(id, new ArrayList<>());
      }
    } catch (IOException e) {
      System.err.println("Erreur lors de la lecture du fichier des noeuds : " + e.getMessage());
    }
  }

  private void chargerArcs(String pathRoads) {
    try (BufferedReader br = new BufferedReader(new FileReader(pathRoads))) {
      String ligne;

      // br.readLine(); // Décommenter si présence d'une ligne d'en-tête

      while ((ligne = br.readLine()) != null) {
        String[] valeurs = ligne.split(",");

        // ATTENTION: Adaptez les indices selon votre CSV
        long idOrigine = Long.parseLong(valeurs[0]);
        long idArrivee = Long.parseLong(valeurs[1]);
        double distance = Double.parseDouble(valeurs[2]);
        String nomRue = valeurs[3];

        // On récupère les vrais objets Localisation créés précédemment
        Localisation origine = this.localisations.get(idOrigine);
        Localisation arrivee = this.localisations.get(idArrivee);

        // Sécurité : on vérifie que les noeuds existent bien dans notre Map
        if (origine != null && arrivee != null) {
          // Création de l'arc [cite: 31-35]
          Arc arc = new Arc(origine, arrivee, nomRue, distance);

          // On ajoute cet arc à la liste d'adjacence du point d'origine
          this.listeAdjacence.get(idOrigine).add(arc);
        }
      }
    } catch (IOException e) {
      System.err.println("Erreur lors de la lecture du fichier des routes : " + e.getMessage());
    }
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
