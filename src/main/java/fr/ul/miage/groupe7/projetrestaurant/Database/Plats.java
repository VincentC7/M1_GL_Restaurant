package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;

public class Plats {


    String nom;
    ObjectId _id;
    HashMap<MatierePremiere,Integer> matieres_premieres;
    List<String> categories;
    boolean enfant;

    public Plats(String nom, HashMap<MatierePremiere,Integer> matieres_premieres, List<String> categories, boolean enfant) {
        this.nom = nom;
        this.matieres_premieres = matieres_premieres;
        this.categories = categories;
        this.enfant = enfant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public HashMap<MatierePremiere,Integer> getMatieres_premieres() {
        return matieres_premieres;
    }

    public void setMatieres_premieres(HashMap<MatierePremiere,Integer> matieres_premieres) {
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
        sb.append(String.format(format,"Ingrédients", matieres_premieres));
        sb.append(String.format(format,"Catégories", categories));
        if (enfant)
            sb.append(String.format("%s %n","Plat pour enfant"));
        sb.append("=".repeat(35)).append("\r\n");
        return sb.toString();
    }
}
