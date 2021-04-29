package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.lang.NonNull;
import org.bson.types.ObjectId;

public class MatierePremiere {

    private ObjectId _id;
    private String nom;
    private int quantitee;
    private UNITE unite;

    public enum UNITE {
        KILOGRAMME("Kg"),
        GRAMME("g"),
        SIMPLE_UNITE("Unite"),
        LITRE("L");

        private final String str;

        /**
         * @param str valeur en chaine de caracteres
         */
        UNITE(final String str) {
            this.str = str;
        }

        @Override
        public String toString() {
            return str;
        }
    }

    public MatierePremiere(@NonNull String nom,@NonNull int quantitee,@NonNull UNITE unite) {
        this.nom = nom;
        this.quantitee = quantitee;
        this.unite = unite;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getQuantitee() {
        return quantitee;
    }

    public void setQuantitee(int quantitee) {
        this.quantitee = quantitee;
    }

    public UNITE getUnite() {
        return unite;
    }

    public void setUnite(UNITE unite) {
        this.unite = unite;
    }

    @Override
    public String toString() {
        return "nom :" + nom +
                ", quantitee=" + quantitee +
                ", unite=" + unite;
    }
}
