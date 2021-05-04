package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.lang.NonNull;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Plats {


    private String nom;
    private ObjectId _id;
    private Map<ObjectId,Integer> matieres_premieres;
    private List<String> categories;

    private BigDecimal prix;
    private boolean enfant;

    public Plats(@NonNull String nom,@NonNull Map<ObjectId,Integer> matieres_premieres,@NonNull BigDecimal prix,List<String> categories, boolean enfant) {
        if( nom.length() < 2 || matieres_premieres.keySet().isEmpty() || prix.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException();

        this.nom = nom;
        this.matieres_premieres = matieres_premieres;
        this.categories = (categories != null)? categories : Collections.emptyList();
        this.enfant = enfant;
        this.prix = prix;
    }

    public Plats(@NonNull String nom,@NonNull Map<ObjectId,Integer> matieres_premieres,@NonNull BigDecimal prix,List<String> categories){
        this(nom,matieres_premieres,prix,categories,false);
    }

    public Plats(@NonNull String nom,@NonNull Map<ObjectId,Integer> matieres_premieres,@NonNull BigDecimal prix){
        this(nom,matieres_premieres,prix,null);
    }

    public Plats(Document d) {
        matieres_premieres = new HashMap<>();
        _id = d.getObjectId("_id");
        this.nom = d.getString("nom");
        var ingredients = d.getList("matières_premières",Document.class);
        for(Document ingredient : ingredients){
            matieres_premieres.put(ingredient.getObjectId("_id"),ingredient.getInteger("quantité"));
        }
        this.prix = new BigDecimal(d.getString("prix"));
        this.categories = d.getList("catégories",String.class);
        this.enfant = d.getBoolean("enfant");
    }

    public String getNom() {
        return nom;
    }

    public ObjectId get_id() {
        return _id;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Map<ObjectId,Integer> getMatieres_premieres() {
        return matieres_premieres;
    }

    public void setMatieres_premieres(Map<ObjectId,Integer> matieres_premieres) {
        this.matieres_premieres = matieres_premieres;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public boolean isEnfant() {
        return enfant;
    }

    public void setEnfant(boolean enfant) {
        this.enfant = enfant;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder();
        var format = "%-12s: %s%n";
        sb.append("=".repeat(35)).append("\r\n");
        sb.append(String.format(format,"Plat ", nom));
        MatierePremiereDAO dao = new MatierePremiereDAO();
        for(var entrySet : matieres_premieres.entrySet()){
            MatierePremiere mp = dao.find(entrySet.getKey());
            sb.append(String.format("\t %-12s : %d%n",mp.getNom(), entrySet.getValue()));
        }

        sb.append(String.format(format,"Catégories", categories));
        if (enfant)
            sb.append(String.format("%s %n","Plat pour enfant"));
        sb.append("=".repeat(35)).append("\r\n");
        return sb.toString();
    }
}
