import java.util.*;


public class Graph {

  private Map<Long, List<Arc>> listeAdjacence;
  private Map<Long, Localisation> localisations;
  private Localisation[] tableauNoeuds;
  private Map<Long, Integer> correspondanceIdVersIndice;
  private List<Integer>[] listeVoisins;
  private double[][] distancesArcs;

  public Graph(String localisations, String roads) {
    this.listeAdjacence = new HashMap<>();
    this.localisations = new HashMap<>();

    Lecteur lecteur = new Lecteur(this.listeAdjacence, this.localisations);
    lecteur.chargerLocalisations(localisations);
    lecteur.chargerArcs(roads);
    construireStructureOptimisee();
  }

  private void construireStructureOptimisee() {

    int nombreNoeuds = localisations.size();

    tableauNoeuds = new Localisation[nombreNoeuds];
    correspondanceIdVersIndice = new HashMap<>();

    int indice = 0;
    for (Localisation localisation : localisations.values()) {
      tableauNoeuds[indice] = localisation;
      correspondanceIdVersIndice.put(localisation.getId(), indice);
      indice++;
    }

    listeVoisins = new ArrayList[nombreNoeuds];
    for (int i = 0; i < nombreNoeuds; i++) {
      listeVoisins[i] = new ArrayList<>();
    }

    // Conversion des ids de type long en int
    for (Map.Entry<Long, List<Arc>> entree : listeAdjacence.entrySet()) {
      int indiceDepart = correspondanceIdVersIndice.get(entree.getKey());
      for (Arc arc : entree.getValue()) {
        int indiceArrivee = correspondanceIdVersIndice.get(arc.getPointArrivee().getId());
        listeVoisins[indiceDepart].add(indiceArrivee);
      }
    }
    distancesArcs = new double[nombreNoeuds][];
    for (int indiceNoeud = 0; indiceNoeud < nombreNoeuds; indiceNoeud++) {
      List<Arc> arcsDuNoeud = listeAdjacence.get(tableauNoeuds[indiceNoeud].getId());
      distancesArcs[indiceNoeud] = new double[arcsDuNoeud.size()];
      for (int i = 0; i < arcsDuNoeud.size(); i++) {
        distancesArcs[indiceNoeud][i] = arcsDuNoeud.get(i).getDistance();
      }
    }
  }

  public Localisation[] determinerZoneInondee(long[] idsOrigin, double epsilon) {
    Queue<Localisation> fileNoeudsAVisiter = new ArrayDeque<>();
    Set<Long> ensembleIdsDejaInondes = new HashSet<>();
    List<Localisation> listeOrdreInondation = new ArrayList<>();

    for (long identifiant : idsOrigin) {
      Localisation pointDepart = this.localisations.get(identifiant);
      fileNoeudsAVisiter.add(pointDepart);
      ensembleIdsDejaInondes.add(identifiant);
      listeOrdreInondation.add(pointDepart);
    }

    while (!fileNoeudsAVisiter.isEmpty()) {
      Localisation noeudCourant = fileNoeudsAVisiter.poll();
      List<Arc> arcsSortants = this.listeAdjacence.get(noeudCourant.getId());

      for (Arc arcCourant : arcsSortants) {
        Localisation noeudVoisin = arcCourant.getPointArrivee();

        boolean penteFavorable =
            noeudVoisin.getAltitude() <= (noeudCourant.getAltitude() + epsilon);

        if (penteFavorable && !ensembleIdsDejaInondes.contains(noeudVoisin.getId())) {
          fileNoeudsAVisiter.add(noeudVoisin);
          ensembleIdsDejaInondes.add(noeudVoisin.getId());
          listeOrdreInondation.add(noeudVoisin);
        }
      }
    }
    return listeOrdreInondation.toArray(new Localisation[0]);
  }

  public Deque<Localisation> trouverCheminLePlusCourtPourContournerLaZoneInondee(
      long idOrigin,
      long idDestination,
      Localisation[] floodedZone) {

    Set<Long> ensembleIdsZoneInondee = new HashSet<>();
    for (Localisation localisation : floodedZone) {
      ensembleIdsZoneInondee.add(localisation.getId());
    }

    Queue<Localisation> fileNoeudsAVisiter = new ArrayDeque<>();
    Set<Long> ensembleIdsDejaVisites = new HashSet<>();
    Map<Long, Localisation> predecesseur = new HashMap<>();

    Localisation pointDepart = this.localisations.get(idOrigin);
    Localisation pointArrivee = this.localisations.get(idDestination);

    fileNoeudsAVisiter.add(pointDepart);
    ensembleIdsDejaVisites.add(idOrigin);

    // BFS
    while (!fileNoeudsAVisiter.isEmpty()) {
      Localisation noeudCourant = fileNoeudsAVisiter.poll();

      if (noeudCourant.getId() == idDestination) {
        return reconstruireCheminEmprunte(predecesseur, pointDepart, pointArrivee);
      }

      List<Arc> arcsSortants = this.listeAdjacence.get(noeudCourant.getId());
      if (arcsSortants == null) {
        continue;
      }

      for (Arc arcCourant : arcsSortants) {
        Localisation noeudVoisin = arcCourant.getPointArrivee();
        long idDuVoisin = noeudVoisin.getId();

        if (!ensembleIdsDejaVisites.contains(idDuVoisin) && !ensembleIdsZoneInondee.contains(
            idDuVoisin)) {
          ensembleIdsDejaVisites.add(idDuVoisin);
          predecesseur.put(idDuVoisin, noeudCourant);
          fileNoeudsAVisiter.add(noeudVoisin);
        }
      }
    }

    return new ArrayDeque<>();
  }

  private Deque<Localisation> reconstruireCheminEmprunte(
      Map<Long, Localisation> parent,
      Localisation depart,
      Localisation arrivee) {

    Deque<Localisation> cheminReconstruit = new ArrayDeque<>();
    Localisation noeudCourant = arrivee;

    while (noeudCourant != null && noeudCourant != depart) {
      cheminReconstruit.addFirst(noeudCourant);
      noeudCourant = parent.get(noeudCourant.getId());
    }

    cheminReconstruit.addFirst(depart);
    return cheminReconstruit;
  }

  public Map<Localisation, Double> determinerChronologieDeLaCrue(
      long[] idsOrigin,
      double vWaterInit,
      double k) {

    int nombreNoeuds = tableauNoeuds.length;

    double[] tempsArriveeEau = new double[nombreNoeuds];
    Arrays.fill(tempsArriveeEau, Double.POSITIVE_INFINITY);

    double[] vitesseEau = new double[nombreNoeuds];
    Arrays.fill(vitesseEau, 0);

    // Dijkstra
    PriorityQueue<Integer> filePriorite =
        new PriorityQueue<>(Comparator.comparingDouble(i -> tempsArriveeEau[i]));

    for (long identifiant : idsOrigin) {
      int indice = correspondanceIdVersIndice.get(identifiant);
      tempsArriveeEau[indice] = 0.0;
      vitesseEau[indice] = vWaterInit;
      filePriorite.add(indice);
    }

    while (!filePriorite.isEmpty()) {
      int indiceNoeudCourant = filePriorite.poll();

      List<Arc> arcsDuNoeud = listeAdjacence.get(tableauNoeuds[indiceNoeudCourant].getId());

      for (Arc arcCourant : arcsDuNoeud) {

        int indiceVoisin = correspondanceIdVersIndice.get(arcCourant.getPointArrivee().getId());
        double distanceArc = arcCourant.getDistance();

        // Calcul de la pente

        double pente = (tableauNoeuds[indiceNoeudCourant].getAltitude()
            - tableauNoeuds[indiceVoisin].getAltitude()) / distanceArc;
        // Calcul de la vitesse
        double nouvelleVitesse = vitesseEau[indiceNoeudCourant] + k * pente;
        // Si vitesse négative ou nulle, l'eau s'arrête
        if (nouvelleVitesse <= 0) {
          continue;
        }

        double tempsParcours = distanceArc / nouvelleVitesse;

        double nouveauTempsArrivee = tempsArriveeEau[indiceNoeudCourant] + tempsParcours;

        if (nouveauTempsArrivee < tempsArriveeEau[indiceVoisin]) {
          tempsArriveeEau[indiceVoisin] = nouveauTempsArrivee;
          vitesseEau[indiceVoisin] = nouvelleVitesse;
          filePriorite.add(indiceVoisin);
        }
      }
    }

    Localisation[] copieTriee = Arrays.copyOf(tableauNoeuds, nombreNoeuds);
    // Tri par temps pour la Map
    Arrays.sort(copieTriee, Comparator.comparingDouble(
        localisation -> tempsArriveeEau[correspondanceIdVersIndice.get(localisation.getId())]
    ));

    Map<Localisation, Double> chronologieCrue = new LinkedHashMap<>();

    for (Localisation localisation : copieTriee) {
      double tempsInondation = tempsArriveeEau[correspondanceIdVersIndice.get(
          localisation.getId())];
      if (tempsInondation < Double.POSITIVE_INFINITY) {
        chronologieCrue.put(localisation, tempsInondation);
      }
    }

    return chronologieCrue;
  }

  public Deque<Localisation> trouverCheminDEvacuationLePlusCourt(
      long idOrigin,
      long idEvacuation,
      double vVehicule,
      Map<Localisation, Double> tFlood) {

    int nombreNoeuds = tableauNoeuds.length;

    double[] tempsTrajet = new double[nombreNoeuds];
    Arrays.fill(tempsTrajet, Double.POSITIVE_INFINITY);

    int[] predecesseur = new int[nombreNoeuds];
    Arrays.fill(predecesseur, -1);
    // Prise des ids des points de depart et d'arrivée
    int indiceOrigine = correspondanceIdVersIndice.get(idOrigin);
    int indiceEvacuation = correspondanceIdVersIndice.get(idEvacuation);

    tempsTrajet[indiceOrigine] = 0.0;

    // Dijkstra

    PriorityQueue<Integer> filePriorite =
        new PriorityQueue<>(Comparator.comparingDouble(i -> tempsTrajet[i]));

    filePriorite.add(indiceOrigine);

    while (!filePriorite.isEmpty()) {

      int indiceNoeudCourant = filePriorite.poll();

      if (indiceNoeudCourant == indiceEvacuation) {
        break;
      }

      List<Arc> arcsDuNoeud = listeAdjacence.get(tableauNoeuds[indiceNoeudCourant].getId());

      for (Arc arcCourant : arcsDuNoeud) {

        int indiceVoisin = correspondanceIdVersIndice.get(arcCourant.getPointArrivee().getId());
        double distanceArc = arcCourant.getDistance();
        // Calcul du temps
        double tempsParcours = distanceArc / vVehicule;
        double tempsArriveeVoisin = tempsTrajet[indiceNoeudCourant] + tempsParcours;

        Localisation localisationVoisin = tableauNoeuds[indiceVoisin];
        Double tempsInondationVoisin = tFlood.get(localisationVoisin);

        if (tempsInondationVoisin != null && tempsArriveeVoisin > tempsInondationVoisin) {
          continue;
        }

        if (tempsArriveeVoisin < tempsTrajet[indiceVoisin]) {
          tempsTrajet[indiceVoisin] = tempsArriveeVoisin;
          predecesseur[indiceVoisin] = indiceNoeudCourant;
          filePriorite.add(indiceVoisin);
        }
      }
    }

    Deque<Localisation> cheminEvacuation = new ArrayDeque<>();

    if (tempsTrajet[indiceEvacuation] == Double.POSITIVE_INFINITY) {
      return cheminEvacuation;
    }

    for (int indiceCourant = indiceEvacuation; indiceCourant != -1;
        indiceCourant = predecesseur[indiceCourant]) {
      cheminEvacuation.addFirst(tableauNoeuds[indiceCourant]);
    }

    return cheminEvacuation;
  }
}
