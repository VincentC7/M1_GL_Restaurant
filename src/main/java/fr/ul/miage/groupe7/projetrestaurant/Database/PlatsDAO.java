package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;
import org.bson.types.ObjectId;

public class PlatsDAO extends DAO<Plats>{

    PlatsDAO(){
        super("Plats");
    }

    @Override
    public Plats find(ObjectId id) {
        return null;
    }

    @Override
    public Plats create(Plats obj) {
        Document d;
        Document catégories = new Document();
        for(String s : obj.getCategories()){
            catégories.a
        }

        d = new Document("nom", obj.getNom())
                .append("prenom", obj.getCategories())
                .append("role", obj.getMatieres_premieres())
                .append("identifiant",obj.isEnfant());

        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();
        return (insert.wasAcknowledged()) ? find(id) :  null;
    }

    @Override
    public Plats update(Plats obj) {
        return null;
    }

    @Override
    public boolean delete(Plats obj) {
        return false;
    }
}
