package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.nio.charset.StandardCharsets;
import java.util.Random;

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
    public Utilisateurs find(ObjectId id) {
        Document d = connect.find(eq( id)).first();
        return (d == null) ? null
                : new Utilisateurs(d);
    }


    @Override
    public Utilisateurs create(Utilisateurs obj) {
        Document d;
        d = new Document("nom", obj.getNom())
                .append("prenom", obj.getPrenom())
                .append("role", obj.getRole())
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
    public void delete(Utilisateurs obj) {
        //TODO
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
