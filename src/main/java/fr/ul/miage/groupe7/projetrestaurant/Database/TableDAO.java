package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

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

    public List<Table> findByServeurNull(){
        var d = connect.find(eq("serveur",null))
                .into(new ArrayList<>());
        return (d.isEmpty()) ? Collections.emptyList()
                : d.stream().map(Table::new).collect(Collectors.toList());
    }

    public List<Table> findByEtat(Table.ETAT etat){
        var d = connect.find(eq("etat",etat.name()))
                .into(new ArrayList<>());
        return (d.isEmpty()) ? Collections.emptyList()
                : d.stream().map(Table::new).collect(Collectors.toList());
    }

    public List<Table> findAll() {
        var d = connect.find()
                .into(new ArrayList<>());
        return (d.isEmpty()) ? Collections.emptyList()
                : d.stream().map(Table::new).collect(Collectors.toList());
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
                .append("serveur", (obj.getServeur() != null) ? obj.getServeur().get_id() : null);

        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();

        return (insert.wasAcknowledged()) ? find(id) :  null;
    }

    @Override
    public Table update(Table obj) {
        if(obj.get_id() != null) {
            List<Bson> updates = new ArrayList<>();
            //UPDATE du serveur de la table
            Utilisateurs serveur = this.find(obj.get_id()).getServeur();
            if ( !(obj.getServeur() != null && obj.getServeur().get_id() == null) && !Objects.equals(serveur, obj.getServeur())) {
                updates.add((obj.getServeur() == null) ? set("serveur", null)
                        : set("serveur", obj.getServeur().get_id()));
            }
            int etage = this.find(obj.get_id()).getEtage();
            if ( obj.getEtage() != etage) {
                updates.add(set("etage", etage));
            }
            Table.ETAT etat = this.find(obj.get_id()).getEtat();
            if ( obj.getEtat() != etat) {
                updates.add(set("etat", etat.name()));
            }
            if (!(updates.isEmpty()))
                connect.updateOne(eq(obj.get_id()), updates);
            return find(obj.get_id());
        }
        System.err.println("La table que vous cherchez à mettre à jour n'existe pas");
        return obj;
    }

    @Override
    public boolean delete(Table obj) {
        if(obj != null) {
            DeleteResult res = connect.deleteOne(eq("_id", obj.get_id()));

            return res.getDeletedCount() != 0;
        }
        return false;
    }
}
