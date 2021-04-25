package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.lang.NonNull;
import org.bson.Document;

public class Utilisateurs {

    String _id,nom,prenom,role,identifiant,mdp;

    public Utilisateurs(@NonNull String nom,@NonNull String prenom,@NonNull String role,@NonNull String mdp,String identifiant) {
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.identifiant = identifiant;
        this.mdp = mdp;
    }

    public Utilisateurs(Document d){
        _id = d.get("_id").toString();
        nom = d.getString("nom");
        prenom = d.getString("prenom");
        role =  d.getString("role");
        identifiant = d.getString("identifiant");
        mdp = d.getString("mdp");
        
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public boolean isMdp(String mdpBDD) {
        return mdp.equals(mdpBDD);
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    @Override
    public String toString() {
        var sb = new StringBuilder();
        var format = "%-12s: %s%n";
        sb.append("=".repeat(35)).append("\r\n");
        sb.append(String.format(format,"nom", nom));
        sb.append(String.format(format,"prénom", prenom));
        sb.append(String.format(format,"role", role));
        sb.append(String.format(format,"identifiant", identifiant));
        sb.append("=".repeat(35)).append("\r\n");
        return sb.toString();
    }
}
