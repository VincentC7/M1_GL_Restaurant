package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

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
    public Utilisateurs find(ObjectId id) {
        Document d = connect.find(eq( id)).first();
        return (d == null) ? null
                : new Utilisateurs(d);
    }


    public List<Utilisateurs> findAllServeur() {
        var d = connect.find(eq("role", Utilisateurs.ROLE.SERVEUR.name()))
                .into(new ArrayList<>())
                ;
        return (d.isEmpty()) ? Collections.emptyList()
                : d.stream().map(Utilisateurs::new).collect(Collectors.toList());
    }


    @Override
    public Utilisateurs create(Utilisateurs obj) {
        Document d;
        d = new Document("nom", obj.getNom())
                .append("prenom", obj.getPrenom())
                .append("role", obj.getRole().name())
                .append("identifiant",
                        (obj.getIdentifiant() != null) ? obj.getIdentifiant() : generateIdentifiant(obj.getNom(), obj.getPrenom()));
        do {
            d.remove("mdp");
            d.append("mdp",((obj.getMdp()) != null) ? obj.getMdp() : generateMdp());
        }while (find(d.getString("identifiant"),d.getString("mdp") )   != null );

        var insert =  connect.insertOne(d);
        ObjectId id = insert.getInsertedId().asObjectId().getValue();
        return (insert.wasAcknowledged()) ? find(id) :  null;

    }

    @Override
    public Utilisateurs update(Utilisateurs obj) {
        return null;
    }

    @Override
    public boolean delete(Utilisateurs obj) {
        if(obj != null){
            DeleteResult res = connect.deleteOne(eq("_id", obj.get_id()));
            return res.getDeletedCount() != 0;
        }
        System.err.println("L'utilisateur que vous cherchez à supprimer n'existe pas");
        return false;

    }

    private String generateIdentifiant(String nom,String prenom){
        return prenom.charAt(0) + nom.toLowerCase();
    }

    private String generateMdp(){
        var leftLimit = 48; // numeral '0'
        var rightLimit = 122; // letter 'z'
        var targetStringLength = 7;
        var random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }


}
