package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.lang.NonNull;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Objects;

public class Utilisateurs {

    ObjectId _id;
    String nom,prenom,identifiant,mdp;
    ROLE role;

    public enum ROLE{
        DIRECTEUR("Directeur"),
        SERVEUR("Serveur"),
        CUISINIER("Cuisinier"),
        ASSISTANT_SERVICE("Assistant service"),
        MAITRE_HOTEL("Maître d'hôtel");

        private final String text;

        public ROLE stringToRole(String role){
            switch (role){
                case "Directeur" : return DIRECTEUR;
                case "Cuisinier" : return CUISINIER;
                case "Assistant service" : return ASSISTANT_SERVICE;
                case "Maître d'hôtel" : return MAITRE_HOTEL;
                default: return SERVEUR;
            }
        }

        /**
         * @param text
         */
        ROLE(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    public Utilisateurs(@NonNull String nom,@NonNull String prenom,@NonNull ROLE role, String mdp,String identifiant) throws IllegalArgumentException {
        setNom(nom);
        setPrenom(prenom);
        setRole(role);
        setIdentifiant(identifiant);
        setMdp(mdp);
    }

    public Utilisateurs(Document d){
        _id = (ObjectId) d.get("_id");
        nom = d.getString("nom");
        prenom = d.getString("prenom");
        role =  ROLE.valueOf(d.getString("role"));
        identifiant = d.getString("identifiant");
        mdp = d.getString("mdp");
        
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if(nom.length() < 2)
            throw new IllegalArgumentException ();
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        if(prenom.length() < 2)
            throw new IllegalArgumentException();
        this.prenom = prenom;
    }

    public ROLE getRole() {
        return role;
    }

    public void setRole(ROLE role) {
        this.role = role;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        if(identifiant != null && identifiant.length() < 4)
            throw new IllegalArgumentException();
        this.identifiant = identifiant;
    }

    public boolean isMdp(String mdpBDD) {
        return mdp.equals(mdpBDD);
    }

    public void setMdp(String mdp) {
        if((mdp != null && mdp.length() < 6)) {
            System.out.println(mdp.length());
            throw new IllegalArgumentException();
        }
        this.mdp = mdp;
    }

    public String getMdp() {
        return mdp;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }


    @Override
    public String toString() {
        var sb = new StringBuilder();
        var format = "%-12s: %s%n";
        sb.append("=".repeat(35)).append("\r\n");
        sb.append(String.format(format,"nom", nom));
        sb.append(String.format(format,"prénom", prenom));
        sb.append(String.format(format,"role", role.toString()));
        sb.append(String.format(format,"identifiant", identifiant));
        sb.append("=".repeat(35)).append("\r\n");
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateurs that = (Utilisateurs) o;
        return Objects.equals(_id, that._id);
    }
}
