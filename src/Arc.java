public class Arc {
    private final Localisation pointOrigine;
    private final Localisation pointArrivee;
    private final double distance;
    private final String rue;

    public Arc(Localisation pointOrigine, Localisation pointArrivee, String rue, double distance) {
        this.pointOrigine = pointOrigine;
        this.pointArrivee = pointArrivee;
        this.rue = rue;
        this.distance = distance;
    }

    public Localisation getPointOrigine() {
        return pointOrigine;
    }

    public Localisation getPointArrivee() {
        return pointArrivee;
    }

    public double getDistance() {
        return distance;
    }

    public String getRue() {
        return rue;
    }
}
