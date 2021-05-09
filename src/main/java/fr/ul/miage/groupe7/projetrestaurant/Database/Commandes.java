package fr.ul.miage.groupe7.projetrestaurant.Database;


import fr.ul.miage.groupe7.projetrestaurant.Main;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static java.util.stream.Collectors.toCollection;

public class Commandes {

    private ObjectId _id;
    private int numeroTable;
    private long time;
    private LocalDateTime debut,fin;
    private boolean etat;
    private BigDecimal prix;
    private ArrayList<CommandesPlats> plats;

    public Commandes(int numeroTable){
        this.numeroTable = numeroTable;
        debut = LocalDateTime.now();
        etat = false;
        plats = new ArrayList<>();
        prix = new BigDecimal(0);
    }

    public Commandes(Document d){
        this._id = d.getObjectId("_id");
        this.numeroTable = d.getInteger("numeroTable");
        this.time = d.getLong("durée");
        this.debut = Instant.ofEpochMilli(d.getDate("debut").getTime())
                .atZone(ZoneId.of("GMT"))
                .toLocalDateTime();
        Date fin = d.getDate("fin");
        this.fin = (fin != null) ? Instant.ofEpochMilli(fin.getTime())
                .atZone(ZoneId.of("GMT"))
                .toLocalDateTime() : null;
        etat = d.getBoolean("état");
        prix = new BigDecimal(d.getString("prix"));
        var platsList = d.getList("plats",Document.class);
        plats = new ArrayList<>();
        for(Document doc : platsList){
            Date commande,prepa,servi;
            commande = doc.getDate("commandé");
            prepa = doc.getDate("en_preparation");
            servi = doc.getDate("servi");
            plats.add(new CommandesPlats(
                    doc.getObjectId("_id"),
                    doc.getObjectId("idPlat"),
                    doc.getLong("temps_preparation"),
                    CommandesPlats.ETAT_PLAT.valueOf(doc.getString("état")),
                    (commande != null) ?
                    commande.toInstant()
                            .atZone(ZoneId.of("GMT"))
                            .toLocalDateTime()
                            : null,
                    (prepa != null) ?
                    Instant.ofEpochMilli(prepa.getTime())
                            .atZone(ZoneId.of("GMT"))
                            .toLocalDateTime()
                            : null ,
                    (servi != null) ?
                    Instant.ofEpochMilli(servi.getTime())
                            .atZone(ZoneId.of("GMT"))
                            .toLocalDateTime()
                            : null

            ));
        }
    }

    public void addCommandes(CommandesPlats cp){
        if(!etat)
            plats.add(cp);
    }

    public ArrayList<CommandesPlats> getCommandesPlats(){
        return this.plats.stream()
                .map(c -> new CommandesPlats(c.get_id(),c.getIdPlat(),c.getPreparationTime(),c.getEtat(),c.getCommande(),c.getEn_preparation(),c.getServi()))
                .collect(toCollection(ArrayList::new));

    }

    public boolean change_etat_commande(int index){
        if(!etat && index < plats.size())
            plats.get(index).next_etat();
        return !etat && index < plats.size();
    }

    public boolean change_etat_commande(ObjectId id){
        if(!etat)
            for(CommandesPlats cp : plats){
                if(cp.get_id().equals(id)) {
                    cp.next_etat();
                    return true;
                }

            }
        return false;
    }

    public void finir(){
        if(plats.stream()
                .allMatch(p -> p.getEtat().equals(CommandesPlats.ETAT_PLAT.SERVI)
                        || p.getEtat().equals(CommandesPlats.ETAT_PLAT.ANNULE))) {
            etat = true;
            fin = LocalDateTime.now();
            time = Duration.between(debut,fin).toMillis();
            var dao = new PlatsDAO();
            for(CommandesPlats cp : plats){
                prix = prix.add(dao.find(cp.getIdPlat()).getPrix());
            }

        }

    }

    public CommandesPlats getCommandePlat(ObjectId id){
        return getCommandesPlats().stream().filter(commandesPlats ->
           commandesPlats.get_id().equals(id)).findFirst().get();
    }

    public int getNumeroTable() {
        return numeroTable;
    }

    public long getTime() {
        return time;
    }

    public LocalDateTime getDebut() {
        return debut;
    }

    public LocalDateTime getFin() {
        return fin;
    }

    public boolean isEtat() {
        return etat;
    }

    public BigDecimal getPrix() {
        return prix;
    }

    public ObjectId get_id() {
        return _id;
    }

    @Override
    public String toString() {
        return "Commandes{" +
                "numeroTable=" + numeroTable +
                ", plats=" + plats +
                '}';
    }

    public String genererFacture() {
        if (etat){
            PlatsDAO platsDAO = new PlatsDAO();
            StringBuilder stringBuilder = new StringBuilder("================================").append(Main.RETOUR_LIGNE);
            for (CommandesPlats plat : plats) {
                Plats current_plat = platsDAO.find(plat.getIdPlat());
                String nom = current_plat.getNom();
                if (nom.length() > 21) {
                    nom = nom.substring(0,18)+"...";
                }
                stringBuilder
                        .append("= ")
                        .append(nom)
                        .append(" ".repeat(22 - nom.length()))
                        .append(current_plat.getPrix())
                        .append("€ =").append(Main.RETOUR_LIGNE);
            }
            String prix = this.prix+"€";
            stringBuilder.append("=                              =").append(Main.RETOUR_LIGNE);
            stringBuilder.append("= Total à payer ").append(" ".repeat(14-prix.length())).append(prix).append(" =").append(Main.RETOUR_LIGNE);
            stringBuilder.append("================================").append(Main.RETOUR_LIGNE);
            return stringBuilder.toString();
        }
        return "La commande n'est pas terminée, la facture ne peut pas être éditer";
    }
}
