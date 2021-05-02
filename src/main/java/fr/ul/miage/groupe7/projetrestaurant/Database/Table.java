package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;
import org.bson.types.ObjectId;

public class Table implements Comparable<Table>{

    private ObjectId _id;
    private int etage;
    private int numero;
    private ETAT etat;
    private Utilisateurs serveur;




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
        this.etage = etage;
        this.numero = numero;
        this.etat = etat;
        setServeur(serveur);
    }

    public Table(Document d){
        _id = (ObjectId) d.get("_id");
        etage = d.getInteger("etage");
        numero = d.getInteger("numero");
        etat = ETAT.valueOf(d.getString("etat"));
        UtilisateursDAO uti = new UtilisateursDAO();
        serveur = uti.find((ObjectId) d.get("serveur"));
    }


    public Utilisateurs getServeur() {
        return serveur;
    }

    public void setServeur(Utilisateurs serveur) {
        if( serveur != null && !serveur.getRole().equals(Utilisateurs.ROLE.SERVEUR.toString())   )
            throw new IllegalArgumentException();
        this.serveur = serveur;
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
        this.etage = etage;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public ETAT getEtat() {
        return etat;
    }

    public void setEtat(ETAT etat) {
        this.etat = etat;
    }

    @Override
    public int compareTo(Table o) {
        return  getNumero() - o.getNumero();
    }

    @Override
    public String toString() {
         return "["+this.etat.code()+"]";
    }
}
