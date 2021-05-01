package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.lang.NonNull;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.math.BigDecimal;

public class MatierePremiere {

    private ObjectId _id;
    private String nom;
    private BigDecimal quantitee;
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

        public static UNITE getInstance(String str) throws IllegalArgumentException {
            UNITE[] unites = UNITE.values();
            for (UNITE unite : unites){
                if (unite.str.equals(str)) return unite;
            }
            throw new IllegalArgumentException("L'unité que vous recherchez n'existe pas") ;
        }
    }

    public MatierePremiere(@NonNull String nom, @NonNull BigDecimal quantitee, @NonNull UNITE unite) throws IllegalArgumentException {
        if ( new BigDecimal(0).compareTo(quantitee) > 0 || quantitee.compareTo(new BigDecimal(Integer.MAX_VALUE)) > 0 || nom == null || unite == null) throw new IllegalArgumentException();
        this.nom = nom;
        this.quantitee = quantitee;
        this.unite = unite;
    }

    public MatierePremiere(Document document) throws IllegalArgumentException {
        _id = (ObjectId) document.get("_id");
        nom = document.getString("nom");
        quantitee = new BigDecimal(""+document.get("quantitee"));
        unite = UNITE.getInstance(document.getString("unite"));
    }

    public void ajouter(BigDecimal bigDecimal) {

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

    public BigDecimal getQuantitee() {
        return quantitee;
    }

    public void setQuantitee(BigDecimal quantitee) {
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
