package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.push;
import static com.mongodb.client.model.Updates.set;

public class CommandesDAO extends DAO<Commandes>{


    public CommandesDAO() {
        super("Commandes");
    }

    @Override
    public Commandes find(ObjectId id) {
        Document d = connect.find(eq( id)).first();
        return (d == null) ? null
                : new Commandes(d);
    }

    public Commandes findByTable(int numero) {
        Document d = connect.find(and(eq( "numeroTable",numero),eq("état",false) )).first();
        return (d == null) ? null
                : new Commandes(d);
    }

    public Commandes findCommandsFromCommand(ObjectId id) {
        Document d = connect.find(eq("plats._id",id)).first();
        return (d == null) ? null
                : new Commandes(d);
    }

    public List<Commandes> findCommandByTableList(List<Integer> tables) {
        ArrayList<Document> documents = connect.find(and(in("numeroTable",tables),eq("état",false)))
                .into(new ArrayList<>());
        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(Commandes::new).collect(Collectors.toList());
    }

    public List<CommandesPlats> findCommandesPlatsById(CommandesPlats.ETAT_PLAT ep) {
        ArrayList<Document> documents = connect.aggregate(Arrays.asList(
            unwind("$plats"),
                replaceRoot("$plats"),
                match(eq("état",ep.name()))
        )).into(new ArrayList<>());
        return (documents.isEmpty()) ? Collections.emptyList()
                : documents.stream().map(CommandesPlats::new).collect(Collectors.toList());
    }

    @Override
    public Commandes create(Commandes obj) {
        Document d;
        List<Document> l = new ArrayList<>();
        for(var cp : obj.getCommandesPlats()){
            l.add(
                    new Document("_id",cp.get_id())
                            .append("idPlat",cp.getIdPlat())
                            .append("état",cp.getEtat().name())
                            .append("temps_preparation",cp.getPreparationTime())
                            .append("commandé",cp.getCommande())
                            .append("en_preparation",cp.getEn_preparation())
                            .append("servi",cp.getTermine())
            );
        }

        d = new Document("numeroTable", obj.getNumeroTable())
                .append("durée",obj.getTime())
                .append("plats", l)
                .append("debut",obj.getDebut())
                .append("fin",obj.getFin())
                .append("état",obj.isEtat())
                .append("prix",obj.getPrix().toString());

        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();
        return (insert.wasAcknowledged()) ? find(id) :  null;
    }

    @Override
    public Commandes update(Commandes obj) {
        if(obj.get_id() != null) {
            List<Bson> updates = new ArrayList<>();
            Commandes c = this.find(obj.get_id());

            ArrayList<CommandesPlats> cps = c.getCommandesPlats();
            ArrayList<CommandesPlats>  newCp = obj.getCommandesPlats().stream().filter(e -> !cps.contains(e))
                    .collect(Collectors.toCollection(ArrayList::new));
            if(!newCp.isEmpty()){
                for( CommandesPlats cp : newCp){
                    Document subdoc =new Document("_id",cp.get_id())
                            .append("idPlat",cp.getIdPlat())
                            .append("état",cp.getEtat().name())
                            .append("temps_preparation",cp.getPreparationTime())
                            .append("commandé",cp.getCommande())
                            .append("en_preparation",cp.getEn_preparation())
                            .append("servi",cp.getTermine());
                    Document d = connect.findOneAndUpdate(and(eq(obj.get_id()),eq("plats._id",cp.get_id())),
                            set("plats.$",subdoc));
                    if(d == null){
                        connect.updateOne(eq(obj.get_id()), push("plats", subdoc));
                        PlatsDAO platsDAO = new PlatsDAO();
                        MatierePremiereDAO matierePremiereDAO = new MatierePremiereDAO();
                        matierePremiereDAO.updateWithPlat(platsDAO.find(cp.getIdPlat()));
                    }
                }
            }

            if(!obj.getPrix().equals(c.getPrix())){
                updates.add(set("prix",obj.getPrix().toString()));
            }
            if(!Objects.equals(obj.getFin(),c.getFin())){
                updates.add(set("fin",obj.getFin()));
            }
            if( obj.getTime() != c.getTime()){
                updates.add(set("durée",obj.getTime()));
            }
            if( obj.isEtat() != c.isEtat()){
                updates.add(set("état",obj.isEtat()));
            }
            if (!(updates.isEmpty()))
                connect.updateOne(eq(obj.get_id()), updates);
            return find(obj.get_id());
        }
        System.err.println("La Commande que vous cherchez à mettre à jour n'existe pas");
        return obj;
    }

    @Override
    public boolean delete(Commandes obj) {
        return connect.findOneAndDelete(eq(obj.get_id())) != null;
    }
}
