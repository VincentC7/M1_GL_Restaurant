package fr.ul.miage.groupe7.projetrestaurant.Database;


import fr.ul.miage.groupe7.projetrestaurant.Main;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

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
                .map(c -> new CommandesPlats(c.get_id(),c.getIdPlat(),c.getPreparationTime(),c.getEtat(),c.getCommande(),c.getEn_preparation(),c.getTermine()))
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

    private static ArrayList<Commandes> filterRepas(ArrayList<Commandes> commandes, boolean midi){
        if(midi){
            return commandes.stream().filter(c -> c.getDebut().getHour() < 16).collect(toCollection(ArrayList::new));
        }else {
            return commandes.stream().filter(c -> c.getDebut().getHour() > 16).collect(toCollection(ArrayList::new));
        }
    }

    /**
     * Retourne la recette moyenne du déjeuner ou du diner
     *
     * @param commandes
     * @param midi, true pour la recette du midi, false pour la recette du soir
     * @return
     */
    public static BigDecimal recetteRepas(ArrayList<Commandes> commandes, boolean midi){
        BigDecimal prix = new BigDecimal("0");
        BigDecimal count = new BigDecimal("0");

        for (Commandes c : filterRepas(commandes, midi)){
            prix = prix.add(c.getPrix());
            count = count.add(new BigDecimal("1"));
        }

        if(count.equals(new BigDecimal("0"))){
            return new BigDecimal("0.0");
        }else {
            return prix.divide(count, 2, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * Recette quotidienne moyenne
     *
     * @param commandes
     * @return
     */
    public static BigDecimal recetteQuotidienne(ArrayList<Commandes> commandes){
        BigDecimal prix = commandes.get(0).getPrix();
        BigDecimal nb_jours = new BigDecimal("1");

        for (int i = 1; i < commandes.size(); i++){
            if(commandes.get(i-1).getDebut().getDayOfYear() != (commandes.get(i).getDebut().getDayOfYear())){
                nb_jours = nb_jours.add(new BigDecimal("1"));
            }
            prix = prix.add(commandes.get(i).getPrix());
        }

        return prix.divide(nb_jours, 2 , BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Recette hebdomadaire moyenne
     *
     * @param commandes
     * @return
     */
    public static BigDecimal recetteHebdomadaire(ArrayList<Commandes> commandes){
        BigDecimal prix = commandes.get(0).getPrix();
        BigDecimal nb_semaines = new BigDecimal("1");
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        for (int i = 1; i < commandes.size(); i++){
            if(commandes.get(i-1).getDebut().get(weekFields.weekOfWeekBasedYear()) != commandes.get(i).getDebut().get(weekFields.weekOfWeekBasedYear())){
                nb_semaines = nb_semaines.add(new BigDecimal("1"));
            }
            prix = prix.add(commandes.get(i).getPrix());
        }

        return prix.divide(nb_semaines, 2 , BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Recette mensuelle moyenne
     *
     * @param commandes
     * @return
     */
    public static BigDecimal recetteMensuelle(ArrayList<Commandes> commandes){
        BigDecimal prix = commandes.get(0).getPrix();
        BigDecimal nb_mois= new BigDecimal("1");

        for (int i = 1; i < commandes.size(); i++){
            if(commandes.get(i-1).getDebut().getMonthValue() != commandes.get(i).getDebut().getMonthValue()){
                nb_mois = nb_mois.add(new BigDecimal("1"));
            }
            prix = prix.add(commandes.get(i).getPrix());
        }

        return prix.divide(nb_mois, 2 , BigDecimal.ROUND_HALF_UP);
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

    public void setDebut(LocalDateTime debut) {
        this.debut = debut;
    }

    public void setPrix(BigDecimal prix) {
        this.prix = prix;
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
