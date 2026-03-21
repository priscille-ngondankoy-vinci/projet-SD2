public class Arc {
    public Localisation pointOrigine;
    public Localisation pointArrivee;
    public double distance;
    public String rue;

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
