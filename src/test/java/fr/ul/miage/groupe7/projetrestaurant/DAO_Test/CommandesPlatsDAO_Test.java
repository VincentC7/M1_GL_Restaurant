package fr.ul.miage.groupe7.projetrestaurant.DAO_Test;

import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CommandesPlatsDAO_Test {

    private CommandesPlats cp,cp2,cp3,cp4,cp5;
    private Commandes c;
    private static Plats p,p2;
    private static PlatsDAO pdao;
    private static CommandesDAO cdao;
    private static MatierePremiereDAO mdao;
    private static ObjectId id,id2;
    private static MatierePremiere mp;

    @BeforeAll
    static void beforeAll(){
        BDD_Connexion.setTest();
        pdao = new PlatsDAO();
        cdao = new CommandesDAO();
        mdao = new MatierePremiereDAO();
        mp = mdao.create(new MatierePremiere("mp",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
        Map<ObjectId,BigDecimal> hm = new HashMap<>();
        hm.put(mp.get_id(),new BigDecimal(150));
        p = new Plats("PlatTest",hm,new BigDecimal("15.50"));
        p = pdao.create(p);
        p2 = new Plats("PlatTest2",hm,new BigDecimal("15.50"));
        p2 = pdao.create(p2);
        id = p.get_id();
        id2 = p2.get_id();
    }

    @AfterAll
    static void afterAll(){
        mdao.delete(mp);
        pdao.delete(p);
        pdao.delete(p2);
    }

    @BeforeEach
    void init(){
        mp.setQuantitee(new BigDecimal(500));
        mdao.update(mp);
        cp = new CommandesPlats(id);
        cp2 = new CommandesPlats(id);
        cp3 = new CommandesPlats(new ObjectId(),id,5000L, CommandesPlats.ETAT_PLAT.SERVI
                , LocalDateTime.of(2021,05,05,12,12,10)
                , LocalDateTime.of(2021,05,05,12,12,15)
                , LocalDateTime.of(2021,05,05,12,12,20));
        cp4 = new CommandesPlats(new ObjectId(),id,115000L, CommandesPlats.ETAT_PLAT.SERVI
                , LocalDateTime.of(2021,05,05,12,12,10)
                , LocalDateTime.of(2021,05,05,12,12,15)
                , LocalDateTime.of(2021,05,05,12,14,10));
        cp5 = new CommandesPlats(new ObjectId(),id,60000L, CommandesPlats.ETAT_PLAT.SERVI
                , LocalDateTime.of(2021,05,05,12,12,10)
                , LocalDateTime.of(2021,05,05,12,12,15)
                , LocalDateTime.of(2021,05,05,12,13,15));
        c =new Commandes(10);
        c = cdao.create(c);
        c.addCommandes(cp);
        c.addCommandes(cp2);

    }

    @Test
    @DisplayName("test find id")
    void testFindById(){
        Commandes commandes = cdao.find(c.get_id());
        assertEquals(c.getNumeroTable(),commandes.getNumeroTable());
        assertFalse(commandes.isEtat());
    }

    @Test
    @DisplayName("test find by table")
    void testFindByTable(){
        Commandes commandes = cdao.findByTable(10);
        assertEquals(c.getNumeroTable(),commandes.getNumeroTable());
        assertFalse(commandes.isEtat());
    }

    @Test
    @DisplayName("Test trouve une commandes grâce à une commande interne ")
    void testfindCommandsFromCommand(){
        c = cdao.update(c);
        Commandes commandes = cdao.findCommandsFromCommand(cp.get_id());
        assertEquals(c.getNumeroTable(),commandes.getNumeroTable());
        assertFalse(commandes.isEtat());
    }

    @Test
    @DisplayName("Test trouve une CommandesPlats par rapport à son _id et son Etat")
    void testfindCommandesPlatsById(){
        c.change_etat_commande(0);
        c.change_etat_commande(1);
        c = cdao.update(c);
        List<CommandesPlats> commandes = cdao.findCommandesPlatsById(CommandesPlats.ETAT_PLAT.EN_PREPARATION);
        assertEquals(2,commandes.size());
    }

    @Test
    @DisplayName("Test update de matieres premieres")
    void testUpdateMatierePremiere(){
        assertEquals(0,new BigDecimal(500).compareTo(mdao.find(mp.get_id()).getQuantitee()));
        c.change_etat_commande(0);
        c.change_etat_commande(1);
        c = cdao.update(c);
        assertEquals(0,new BigDecimal(200).compareTo(mdao.find(mp.get_id()).getQuantitee()));
    }

    @Test
    @DisplayName("Test trouve une CommandesPlats par rapport à son _id et son Etat Fail")
    void testfindCommandesPlatsByIdFail(){
        List<CommandesPlats> commandes = cdao.findCommandesPlatsById(CommandesPlats.ETAT_PLAT.EN_PREPARATION);
        assertEquals(0,commandes.size());
    }

    @Test
    @DisplayName("Test statistiques temps de preparation pour chaque plats")
    void testStatTempsPreparation(){
        mp.setQuantitee(new BigDecimal(1500));
        mdao.update(mp);
        c.addCommandes(cp3);
        c.addCommandes(cp4);
        c = cdao.update(c);
        var map = cdao.getPreparationTimeByPlats();
        System.out.println(map);
        assertEquals(0,map.get("PlatTest").compareTo(new BigDecimal(1)));
    }

    @Test
    @DisplayName("Test statistiques temps de preparation pour tous les plats")
    void testStatTempsPreparationTotal(){
        mp.setQuantitee(new BigDecimal(1500));
        mdao.update(mp);
        c.addCommandes(cp3);
        c.addCommandes(cp4);
        c.addCommandes(cp5);
        c = cdao.update(c);
        var decimal = cdao.getPreparationTime();
        assertEquals(0,decimal.compareTo(new BigDecimal(1)));
    }

    @Test
    @DisplayName("Test update")
    void testUpdateSucces(){
        c.change_etat_commande(0);
        cdao.update(c);
        Commandes cmds = cdao.find(c.get_id());
        assertEquals(CommandesPlats.ETAT_PLAT.EN_PREPARATION,cmds.getCommandesPlats().get(0).getEtat());
    }

    @Test
    @DisplayName("Test update date")
    void testUpdateSuccesTimeCheck(){
        c.addCommandes(cp3);
        c = cdao.update(c);
        Commandes cmds = cdao.find(c.get_id());
        assertEquals(LocalDateTime.of(2021,05,05,12,12,10)
                ,cmds.getCommandesPlats().get(2).getCommande());
        assertEquals(LocalDateTime.of(2021,05,05,12,12,15)
                ,cmds.getCommandesPlats().get(2).getEn_preparation());
        assertEquals(LocalDateTime.of(2021,05,05,12,12,20)
                ,cmds.getCommandesPlats().get(2).getTermine());
    }

    @Test
    @DisplayName("Test update finir")
    void testUpdateSuccesFinir(){
        c.change_etat_commande(0);
        c.change_etat_commande(0);
        c.change_etat_commande(0);
        c.change_etat_commande(1);
        c.change_etat_commande(1);
        c.change_etat_commande(1);
        c = cdao.update(c);
        c.finir();
        c = cdao.update(c);
        Commandes cmds = cdao.find(c.get_id());
        assertNotEquals(0,cmds.getTime());
        assertTrue(cmds.isEtat());

    }


    @AfterEach
    void afterEach(){
        cdao.delete(c);
    }
}
