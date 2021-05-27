package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;

public class Table implements Comparable<Table>{

    private ObjectId _id;
    private int etage;
    private int numero;
    private ETAT etat;
    private Utilisateurs serveur;
    private HashSet<Reservation> reservations;

    public enum ETAT{

        PROPRE("Propre"),
        SALE("Sale"),
        SECOND_SERVICE("A dresser pour un second service"),
        OCCUPEE("Occupée"),
        RESERVEE("Réservée");

        private final String etat;

        ETAT(final String etat) {
            this.etat = etat;
        }

        public String code(){
            switch (this){
                case SALE:
                    return "S";
                case OCCUPEE:
                    return "O";
                case PROPRE:
                    return "P";
                case RESERVEE:
                    return "R";
                case SECOND_SERVICE:
                    return "SS";
                default:
                    return "/";
            }
        }

        @Override
        public String toString() {
            return etat;
        }
    }

    public Table(int etage, int numero, ETAT etat) {
        this(etage,numero,etat,null);
    }


    public Table(int etage, int numero, ETAT etat, Utilisateurs serveur) {
        setEtage(etage);
        setNumero(numero);
        setEtat(etat);
        this.reservations = new HashSet<>();
        setServeur(serveur);
    }

    public Table(Document d){
        _id = (ObjectId) d.get("_id");
        etage = d.getInteger("etage");
        numero = d.getInteger("numero");
        etat = ETAT.valueOf(d.getString("etat"));
        UtilisateursDAO uti = new UtilisateursDAO();
        serveur = uti.find((ObjectId) d.get("serveur"));
        reservations = new HashSet<>();
        var reservationsList = d.getList("reservations",Document.class);
        for(Document doc : reservationsList){
            reservations.add(new Reservation(
                    doc.getString("creneau"),
                    doc.getString("nom"),
                    Instant.ofEpochMilli(doc.getDate("date").getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()

            ));
        }
    }


    public Utilisateurs getServeur() {
        return serveur;
    }

    public void setServeur(Utilisateurs serveur) {
        if( serveur != null && !serveur.getRole().equals(Utilisateurs.ROLE.SERVEUR))
            throw new IllegalArgumentException();
        this.serveur = serveur;
    }

    public void addReservation(Reservation r) {
        this.reservations.add(r);
    }

    public HashSet<Reservation> getReservations() {
        return reservations;
    }

    public Reservation getReservation(LocalDate l, Reservation.CRENEAU c){
        Optional<Reservation> res = reservations.stream().filter(r -> l.equals(r.getDate()) && c.equals(r.getCRENAU()) )
                .findFirst();
        return res.orElse(null);
    }

    public void deleteReservation(LocalDate l, Reservation.CRENEAU c){
        reservations.remove(new Reservation(c,null,l));
    }

    public boolean isReserved(LocalDate l, Reservation.CRENEAU c){
        return reservations.contains(new Reservation(c,null,l));
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public int getEtage() {
        return etage;
    }

    public void setEtage(int etage) {
        if(etage >= 1){
            this.etage = etage;
        }else{
            throw new IllegalArgumentException();
        }
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        if(numero >= 1){
            this.numero = numero;
        }else{
            throw new IllegalArgumentException();
        }
    }

    public ETAT getEtat() {
        return etat;
    }

    public void setEtat(ETAT etat) {
        if (etat == null) {
            this.etat = ETAT.PROPRE;
        } else if(etat == ETAT.RESERVEE || this.etat == null) {
            this.etat = etat;
        } else{
            // l'état propre peut seulement passer à l'état occupée
            if(this.etat == ETAT.PROPRE || this.etat == ETAT.RESERVEE){
                if(etat == ETAT.OCCUPEE){
                    this.etat = etat;
                }else{
                    throw new IllegalArgumentException();
                }
                // l'état occupée peut seulement passer à l'état sale
            }else if(this.etat == ETAT.OCCUPEE){
                if(etat == ETAT.SALE){
                    this.etat = etat;
                }else{
                    throw new IllegalArgumentException();
                }
                // l'état sale peut seulement passer à l'état second service
            }else if(this.etat == ETAT.SALE){
                if(etat == ETAT.SECOND_SERVICE){
                    this.etat = etat;
                }else{
                    throw new IllegalArgumentException();
                }
                // l'état seconde service peut seulement passer à l'état propre
            } else if(this.etat == ETAT.SECOND_SERVICE){
                if(etat == ETAT.PROPRE){
                    this.etat = etat;
                }else{
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    @Override
    public int compareTo(Table o) {
        return  getNumero() - o.getNumero();
    }

    @Override
    public String toString() {
         return "["+this.etat.code()+"]";
    }

    public String toStringServeur() {
        var sb = new StringBuilder();
        var format = "%-7s: %s%n";
        sb.append("=".repeat(10)).append("\r\n");
        sb.append(String.format(format,"étage", etage));
        sb.append(String.format(format,"numéro", numero));
        sb.append(String.format(format,"etat", etat.code()));
        sb.append("=".repeat(10)).append("\r\n");
        return sb.toString();
    }
}
