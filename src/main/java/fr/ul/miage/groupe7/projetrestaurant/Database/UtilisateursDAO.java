package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;

import static com.mongodb.client.model.Filters.*;

public class UtilisateursDAO extends DAO<Utilisateurs> {


    public UtilisateursDAO(){
        super("Utilisateurs");
    }


    public Utilisateurs find(String identifiant,String mdp) {
        Document d = connect.find(and(eq("identifiant",identifiant),eq("mdp",mdp))).first();
        return (d == null) ? null
                : new Utilisateurs(d);
    }

    @Override
    public Utilisateurs find(String id) {
        Document d = connect.find(eq(id)).first();
        return (d == null) ? null
                : new Utilisateurs(d);
    }


    @Override
    public Utilisateurs create(Utilisateurs obj) {
        return null;
    }

    @Override
    public Utilisateurs update(Utilisateurs obj) {
        return null;
    }

    @Override
    public void delete(Utilisateurs obj) {
        //TODO
    }
}
