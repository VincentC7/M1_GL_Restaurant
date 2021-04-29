package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class TableDAO extends DAO<Table> {

    TableDAO() {
        super("table");
    }

    @Override
    public Table find(ObjectId id){
        Document d = connect.find(eq( id)).first();
        return (d == null) ? null
                : new Table(d);
    }

    public Table findByNum(int numero){
        Document d = connect.find(eq("numero",numero)).first();
        return (d == null) ? null
                : new Table(d);
    }

    @Override
    public Table create(Table obj) {
        Document d;
        d = new Document("etage", obj.getEtage())
                .append("numero", obj.getNumero())
                .append("etat", obj.getEtat())
                .append("serveur", obj.getServeur().get_id());

        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();

        return (insert.wasAcknowledged()) ? find(id) :  null;
    }

    @Override
    public Table update(Table obj) {
        return null;
    }

    @Override
    public boolean delete(Table obj) {
        return false;
    }
}
