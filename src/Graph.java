import java.util.*;


public class Graph {

  private Map<Long, List<Arc>> listeAdjacence;
  private Map<Long, Localisation> localisations;
    private Localisation[] nodes;          // index -> Localisation
    private Map<Long, Integer> idToIndex;  // id -> index
    private List<Integer>[] adj;
    private double[][] dist;
    private Map<Localisation, Double> cacheCrue = null;
// dist[u][i] = distance de u à adj[u].get(i)

    //ATTRIBUT ?
  //TODO

  public Graph(String localisations, String roads) {
    this.listeAdjacence = new HashMap<>();
    this.localisations = new HashMap<>();

    Lecteur l = new Lecteur(this.listeAdjacence, this.localisations);
    l.chargerLocalisations(localisations);
    l.chargerArcs(roads);
    construireStructureOptimisee();
  }
    @SuppressWarnings("unchecked")
    private void construireStructureOptimisee() {

        int n = localisations.size();

        nodes = new Localisation[n];
        idToIndex = new HashMap<>();

        int idx = 0;
        for (Localisation loc : localisations.values()) {
            nodes[idx] = loc;
            idToIndex.put(loc.getId(), idx);
            idx++;
        }

        adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();

        // Remplir adj[] avec les indices
        for (Map.Entry<Long, List<Arc>> entry : listeAdjacence.entrySet()) {
            int from = idToIndex.get(entry.getKey());
            for (Arc arc : entry.getValue()) {
                int to = idToIndex.get(arc.getPointArrivee().getId());
                adj[from].add(to);
            }
        }

        // Construire dist[][] en parallèle
        dist = new double[n][];
        for (int u = 0; u < n; u++) {
            List<Arc> arcs = listeAdjacence.get(nodes[u].getId());
            dist[u] = new double[arcs.size()];
            for (int i = 0; i < arcs.size(); i++) {
                dist[u][i] = arcs.get(i).getDistance();
            }
        }
    }



    public Localisation[] determinerZoneInondee(long[] idsOrigin, double epsilon) {
    Queue<Localisation> aVisiter = new ArrayDeque<>();
    Set<Long> dejaInondes = new HashSet<>();
    List<Localisation> ordreInondation = new ArrayList<>();

    for (long id : idsOrigin) {
      Localisation depart = this.localisations.get(id);
      aVisiter.add(depart);
      dejaInondes.add(id);
      ordreInondation.add(depart);
    }

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

    public Deque<Localisation> trouverCheminLePlusCourtPourContournerLaZoneInondee(
            long idOrigin,
            long idDestination,
            Localisation[] floodedZone) {

        Set<Long> zoneInondeSet = new HashSet<>();
        for (Localisation loc : floodedZone) {
            zoneInondeSet.add(loc.getId());
        }

        Queue<Localisation> aVisiter = new ArrayDeque<>();
        Set<Long> dejaVisites = new HashSet<>();
        Map<Long, Localisation> parent = new HashMap<>();

        Localisation depart = this.localisations.get(idOrigin);
        Localisation arrivee = this.localisations.get(idDestination);

        aVisiter.add(depart);
        dejaVisites.add(idOrigin);

        // BFS
        while (!aVisiter.isEmpty()) {
            Localisation courant = aVisiter.poll();

            if (courant.getId() == idDestination) {
                return reconstruireCheminEmprunte(parent, depart, arrivee);
            }

            List<Arc> arcsSortants = this.listeAdjacence.get(courant.getId());
            if (arcsSortants == null) continue;

            for (Arc arc : arcsSortants) {
                Localisation voisin = arc.getPointArrivee();
                long idVoisin = voisin.getId();

                if (!dejaVisites.contains(idVoisin) && !zoneInondeSet.contains(idVoisin)) {
                    dejaVisites.add(idVoisin);
                    parent.put(idVoisin, courant);
                    aVisiter.add(voisin);
                }
            }
        }

        return new ArrayDeque<>();
    }


    private Deque<Localisation> reconstruireCheminEmprunte(
            Map<Long, Localisation> parent,
            Localisation depart,
            Localisation arrivee) {

        Deque<Localisation> chemin = new ArrayDeque<>();
        Localisation courant = arrivee;

        while (courant != null && courant != depart) {
            chemin.addFirst(courant);
            courant = parent.get(courant.getId());
        }

        chemin.addFirst(depart);
        return chemin;
    }

    public Map<Localisation, Double> determinerChronologieDeLaCrue(
            long[] idsOrigin,
            double vWaterInit,
            double k) {

        int n = nodes.length;

        double[] temps = new double[n];
        Arrays.fill(temps, Double.POSITIVE_INFINITY);

        double[] vitesse = new double[n];
        Arrays.fill(vitesse, 0);

        PriorityQueue<Integer> pq =
                new PriorityQueue<>(Comparator.comparingDouble(i -> temps[i]));

        for (long id : idsOrigin) {
            int idx = idToIndex.get(id);
            temps[idx] = 0.0;
            vitesse[idx] = vWaterInit;
            pq.add(idx);
        }

        while (!pq.isEmpty()) {
            int u = pq.poll();

            List<Arc> arcs = listeAdjacence.get(nodes[u].getId());

            for (Arc arc : arcs) {

                int v = idToIndex.get(arc.getPointArrivee().getId());
                double dist = arc.getDistance();


                double pente = (nodes[u].getAltitude() - nodes[v].getAltitude()) / dist;

                double newVitesse = vitesse[u] + k * pente;
                if (newVitesse <= 0) continue;
                double vitesseMoyenne = (vitesse[u] + newVitesse) / 2;

                double tempsArc = dist / vitesseMoyenne;

                double nouveauTemps = temps[u] + tempsArc;

                if (nouveauTemps < temps[v]) {
                    temps[v] = nouveauTemps;
                    vitesse[v] = newVitesse;
                    pq.add(v);
                }
            }
        }

        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (temps[i] < Double.POSITIVE_INFINITY) {
                indices.add(i);
            }
        }

        indices.sort(Comparator.comparingDouble(i -> temps[i]));

        Map<Localisation, Double> resultat = new LinkedHashMap<>();

        for (int idx : indices) {
            resultat.put(nodes[idx], temps[idx]);
        }

        return resultat;
    }











    public Deque<Localisation> trouverCheminDEvacuationLePlusCourt(long idOrigin, long idEvacuation,
      double vVehicule, Map<Localisation, Double> tFlood) {
    //TODO
    return null;
  }


}
