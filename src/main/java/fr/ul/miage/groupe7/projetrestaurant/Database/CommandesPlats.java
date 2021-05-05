package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

public class CommandesPlats {


    public enum ETAT_PLAT{
        COMMANDE("Commandé"),
        EN_PREPARATION("En cours de préparation"),
        SERVI("Est servi"),
        ANNULE("Annulé");

        private final String text;

        /**
         * @param text
         */
        ETAT_PLAT(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    private ObjectId idPlat,_id;
    private long preparationTime;
    private ETAT_PLAT etat;
    private LocalDateTime commande,en_preparation,servi;


    public CommandesPlats(ObjectId id){
        _id = new ObjectId();
        idPlat = id;
        etat = ETAT_PLAT.COMMANDE;
        preparationTime = 0L;
        commande = LocalDateTime.now();
    }

    public CommandesPlats(ObjectId _id,ObjectId id,long preparationTime,ETAT_PLAT etat, LocalDateTime commande,LocalDateTime en_preparation, LocalDateTime servi){
        this._id =_id;
        this.idPlat =id;
        this.preparationTime = preparationTime;
        this.etat = etat;
        this.commande =commande;
        this.en_preparation = en_preparation;
        this.servi = servi;
    }

    public CommandesPlats(Document d){
        Date commande,prepa,servi;
        commande = d.getDate("commandé");
        prepa = d.getDate("en_preparation");
        servi = d.getDate("servi");
        this._id = d.getObjectId("_id");
        this.idPlat = d.getObjectId("idPlat");
        this.preparationTime = d.getLong("temps_preparation");
        this.etat = CommandesPlats.ETAT_PLAT.valueOf(d.getString("état"));
        this.commande = (commande != null) ?
                        commande.toInstant()
                                .atZone(ZoneId.of("GMT"))
                                .toLocalDateTime()
                        : null;
        this.en_preparation = (prepa != null) ?
                        Instant.ofEpochMilli(prepa.getTime())
                                .atZone(ZoneId.of("GMT"))
                                .toLocalDateTime()
                        : null ;
        this.servi = (servi != null) ?
                        Instant.ofEpochMilli(servi.getTime())
                                .atZone(ZoneId.of("GMT"))
                                .toLocalDateTime()
                        : null;
    }

    public void next_etat(){
        switch (etat){
            case COMMANDE:
                etat = ETAT_PLAT.EN_PREPARATION;
                en_preparation = LocalDateTime.now();
                break;
            case EN_PREPARATION:
                etat = ETAT_PLAT.SERVI;
                servi = LocalDateTime.now();
                preparationTime = Duration.between(en_preparation,servi).toMillis();
                break;
            case SERVI:
                annulation();
                break;
            case ANNULE:
                break;
        }
    }

    public void annulation(){
        etat = ETAT_PLAT.ANNULE;
    }

    public ObjectId getIdPlat() {
        return idPlat;
    }

    public long getPreparationTime() {
        return preparationTime;
    }

    public ETAT_PLAT getEtat() {
        return etat;
    }

    public LocalDateTime getCommande() {
        return commande;
    }

    public LocalDateTime getEn_preparation() {
        return en_preparation;
    }

    public LocalDateTime getServi() {
        return servi;
    }

    public ObjectId get_id() {
        return _id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandesPlats that = (CommandesPlats) o;
        return Objects.equals(idPlat, that.idPlat) && Objects.equals(_id, that._id) && etat == that.etat;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPlat, _id, etat);
    }

    @Override
    public String toString() {
        PlatsDAO pdao = new PlatsDAO();
        return "CommandesPlats{" +
                "idPlat=" + pdao.find(idPlat) +
                ", etat=" + etat +
                '}';
    }
}
