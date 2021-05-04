package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashSet;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class MatierePremiereDAO extends DAO<MatierePremiere> {

    public MatierePremiereDAO() {
        super("MatierePremiere");
    }

    @Override
    public MatierePremiere find(ObjectId id) {
        Document document = connect.find(eq(id)).first();
        return (document == null) ? null : new MatierePremiere(document);
    }

    public MatierePremiere findByName(String nom) {
        Document document = connect.find(eq("nom",nom)).first();
        return (document == null) ? null : new MatierePremiere(document);
    }

    public HashSet<MatierePremiere> findAll(){
        HashSet<MatierePremiere> matierePremieres = new HashSet<>();
        for (Document document : connect.find()){
            matierePremieres.add(new MatierePremiere(document));
        }
        return matierePremieres;
    }

    @Override
    public MatierePremiere create(MatierePremiere obj) {
        MatierePremiere testexist = findByName(obj.getNom());
        if (testexist != null) return null;
        Document document = new Document(
                "nom", obj.getNom())
                .append("quantitee",obj.getQuantitee())
                .append("unite",obj.getUnite().toString());
        if (obj.get_id() != null) document.append("_id",obj.get_id());
        var insert = connect.insertOne(document);
        ObjectId id = Objects.requireNonNull(insert.getInsertedId()).asObjectId().getValue();
        return (insert.wasAcknowledged()) ? find(id) : null;
    }

    @Override
    public MatierePremiere update(MatierePremiere obj) {
        if (obj != null){
            connect.updateOne(eq("_id", obj.get_id()), set("quantitee",obj.getQuantitee()));
            return obj;
        }
        return null;
    }

    @Override
    public boolean delete(MatierePremiere obj) {
        if(obj != null){
            DeleteResult res = connect.deleteOne(eq("_id", obj.get_id()));
            return res.getDeletedCount() != 0;
        }
        return false; //exite pas
    }

}
