package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.lang.NonNull;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

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
        OCUPEE("Ocupée"),
        RESERVEE("Réservée");

        private final String etat;

        ETAT(final String etat) {
            this.etat = etat;
        }

        public String code(){
            switch (this){
                case SALE:
                    return "S";
                case OCUPEE:
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
        if( serveur != null && !serveur.getRole().equals(Utilisateurs.ROLE.SERVEUR.toString())   )
            throw new IllegalArgumentException();
        this.serveur = serveur;
    }

    public void addReservation(Reservation r) {
        this.reservations.add(r);
    }

    public HashSet<Reservation> getReservations() {
        return reservations;
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
        if(etat == null){
            this.etat = ETAT.PROPRE;
        }else{
            this.etat = etat;
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
