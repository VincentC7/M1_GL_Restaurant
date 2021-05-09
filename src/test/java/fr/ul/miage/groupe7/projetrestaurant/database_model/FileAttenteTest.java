package fr.ul.miage.groupe7.projetrestaurant.database_model;

import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileAttenteTest {

    private CommandesPlats cp,cp2,cp3;
    private Commandes c, c2;
    private Plats p, p2, p3, p4;
    private PlatsDAO pdao;
    private CommandesDAO cdao;

    @BeforeEach
     void init(){
        BDD_Connexion.setTest();
        pdao = new PlatsDAO();
        cdao = new CommandesDAO();
        Map<ObjectId,Integer> hm = new HashMap<>();
        String hex3 ="c".repeat(24);
        hm.put(new ObjectId(hex3),150);
        p = new Plats("Plat 1",hm,new BigDecimal("15.50"), null, false);
        p = pdao.create(p);
        p2 = new Plats("Plat 2",hm,new BigDecimal("16.50"), null, false);
        p2 = pdao.create(p2);
        p3 = new Plats("Plat 3",hm,new BigDecimal("13.50"), null, true);
        p3 = pdao.create(p3);

        c = new Commandes(1);
        cp = new CommandesPlats(p.get_id());
        cp2 = new CommandesPlats(p2.get_id());
        cp3 = new CommandesPlats(p3.get_id());
        c.addCommandes(cp);
        c.addCommandes(cp2);
        c.addCommandes(cp3);
        c = cdao.create(c);
    }

    @AfterEach
    void afterEach(){
        pdao.delete(p);
        pdao.delete(p2);
        pdao.delete(p3);
        cdao.delete(c);
    }

    @Test
    @DisplayName("Création de la file d'attente / vérification de l'ordre des éléments")
    void testCreateFile(){
        FileAttente fileAttente = new FileAttente();
        LinkedList<CommandesPlats> file = fileAttente.getCommandes();

        assertEquals(cp3.get_id(), file.getFirst().get_id());
        assertEquals(cp2.get_id(), file.getLast().get_id());
        assertEquals(cp.get_id(), file.get(1).get_id());
    }

    @Test
    @DisplayName("Ajout d'une nouvelle commande avec un plat classique")
    void testAddNewCommand(){
        c2 = new Commandes(2);
        Map<ObjectId, Integer> hm = new HashMap<>();
        String hex3 ="c".repeat(24);
        hm.put(new ObjectId(hex3),150);
        p4 = new Plats("Plat 4",hm,new BigDecimal("15.50"), null, false);
        p4 = pdao.create(p4);
        CommandesPlats cp4 = new CommandesPlats(p4.get_id());
        c2.addCommandes(cp4);
        c2 = cdao.create(c2);

        FileAttente fileAttente = new FileAttente();
        LinkedList<CommandesPlats> file = fileAttente.getCommandes();
        assertEquals(cp4.get_id(), file.getLast().get_id());

        cdao.delete(c2);
        pdao.delete(p4);
    }

    @Test
    @DisplayName("Ajout d'une nouvelle commande avec un plat Enfant")
    void testAddNewCommandEnfant(){
        c2 = new Commandes(2);
        Map<ObjectId, Integer> hm = new HashMap<>();
        String hex3 ="c".repeat(24);
        hm.put(new ObjectId(hex3),150);
        p4 = new Plats("Plat 4",hm,new BigDecimal("15.50"), null, true);
        p4 = pdao.create(p4);
        CommandesPlats cp4 = new CommandesPlats(p4.get_id());
        c2.addCommandes(cp4);
        c2 = cdao.create(c2);

        FileAttente fileAttente = new FileAttente();
        LinkedList<CommandesPlats> file = fileAttente.getCommandes();
        assertEquals(cp4.get_id(), file.get(1).get_id());

        cdao.delete(c2);
        pdao.delete(p4);
    }

    @Test
    @DisplayName("Traiter une commande")
    void testTraiterCommand(){
        FileAttente fileAttente = new FileAttente();

        CommandesPlats commandesPlats = fileAttente.getCommandes().getFirst();
        fileAttente.traiterCommande();

        Commandes commandes = cdao.findCommandsFromCommand(commandesPlats.get_id());
        assertEquals(CommandesPlats.ETAT_PLAT.EN_PREPARATION, commandes.getCommandePlat(commandesPlats.get_id()).getEtat());
    }

    @Test
    @DisplayName("Traiter une commande et actualiser la liste")
    void testTraiterCommandSupp(){
        FileAttente fileAttente = new FileAttente();

        CommandesPlats commandesPlats = fileAttente.traiterCommande();
        assertEquals(CommandesPlats.ETAT_PLAT.EN_PREPARATION, commandesPlats.getEtat());

        LinkedList<CommandesPlats> file = fileAttente.getCommandes();
        assertEquals(2, file.size());
    }
}
