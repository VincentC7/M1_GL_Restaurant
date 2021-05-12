package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class PlatsDAO extends DAO<Plats>{

    public PlatsDAO(){
        super("Plats");
    }

    @Override
    public Plats find(ObjectId id) {
        Document d = connect.find(eq( id)).first();
        return (d == null) ? null
                : new Plats(d);
    }

    public List<Plats> find(String nom) {
        var d = connect.find(eq("nom",nom)).into(new ArrayList<>());

        return (d.isEmpty()) ? Collections.emptyList()
                : d.stream().map(Plats::new).collect(Collectors.toList());
    }

    public List<Plats> findByMenu() {
        var d = connect.find(eq("menu",true)).into(new ArrayList<>());

        return (d.isEmpty()) ? Collections.emptyList()
                : d.stream().map(Plats::new).collect(Collectors.toList());
    }

    @Override
    public Plats create(Plats obj) {
        Document d;
        List<Document> l = new ArrayList<>();
        for(var mp : obj.getMatieres_premieres().entrySet()){
            l.add(
                    new Document("_id",mp.getKey())
                            .append("quantité",mp.getValue().toString())
            );
        }

        d = new Document("nom", obj.getNom())
                .append("catégories",obj.getCategories())
                .append("matières_premières", l)
                .append("enfant",obj.isEnfant())
                .append("prix",obj.getPrix().toString())
                .append("menu",obj.isMenu());

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
        if(obj != null){
            DeleteResult res = connect.deleteOne(eq("_id", obj.get_id()));
            return res.getDeletedCount() != 0;
        }
        System.err.println("Le plat que vous cherchez à supprimer n'existe pas");
        return false;
    }
}
