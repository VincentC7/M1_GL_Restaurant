package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;

public class Utilisateurs {

    String nom,prenom,role,identifiant,mdp;

    public Utilisateurs(String nom, String prenom, String role, String identifiant, String mdp) {
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
        this.identifiant = identifiant;
        this.mdp = mdp;
    }

    public Utilisateurs(Document d){
        nom = (String)d.get("nom");
        prenom = (String)d.get("prenom");
        role = (String) d.get("role");
        identifiant = (String)d.get("identifiant");
        mdp = (String)d.get("mdp");
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

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }
}
