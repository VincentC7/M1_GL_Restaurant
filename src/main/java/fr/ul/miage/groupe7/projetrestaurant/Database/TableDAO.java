package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

public class TableDAO extends DAO<Table> {

    public TableDAO() {
        super("Tables");
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

    public List<Table> findByServeur(Utilisateurs obj){
        if(obj.role.equals("Serveur")){
            ArrayList<Document> list = connect.find(eq("serveur",obj.get_id())).into(new ArrayList<>());
            return (list.isEmpty()) ? Collections.emptyList()
                    : list.stream().map(Table::new).collect(Collectors.toList());
        }else{
            throw new IllegalArgumentException();
        }

    }

    @Override
    public Table create(Table obj) {
        Document d;
        d = new Document("etage", obj.getEtage())
                .append("numero", obj.getNumero())
                .append("etat", obj.getEtat().name())
                .append("serveur", (obj.getServeur().get_id() == null) ? null: obj.getServeur().get_id() );

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
        DeleteResult res = connect.deleteOne(eq("_id", obj.get_id()));
        return res.getDeletedCount() != 0;
    }
}
