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

    private CommandesPlats cp,cp2,cp3;
    private Commandes c;
    private static Plats p;
    private static PlatsDAO pdao;
    private static CommandesDAO cdao;
    private static ObjectId id;

    @BeforeAll
    static void beforeAll(){
        BDD_Connexion.setTest();
        pdao = new PlatsDAO();
        cdao = new CommandesDAO();
        Map<ObjectId,Integer> hm = new HashMap<>();
        String hex3 ="c".repeat(24);
        hm.put(new ObjectId(hex3),150);
        p = new Plats("PlatTest",hm,new BigDecimal("15.50"));
        p = pdao.create(p);
        id = p.get_id();
    }

    @AfterAll
    static void afterAll(){
        pdao.delete(p);
    }

    @BeforeEach
    void init(){
        cp = new CommandesPlats(id);
        cp2 = new CommandesPlats(id);
        cp3 = new CommandesPlats(id,new ObjectId(),10000L, CommandesPlats.ETAT_PLAT.SERVI
                , LocalDateTime.of(2021,05,05,12,12,10)
                , LocalDateTime.of(2021,05,05,12,12,15)
                , LocalDateTime.of(2021,05,05,12,12,20));
        c =new Commandes(10);
        c.addCommandes(cp);
        c.addCommandes(cp2);

    }

    @Test
    @DisplayName("test find id")
    void testFindById(){
        c = cdao.create(c);
        Commandes commandes = cdao.find(c.get_id());
        assertEquals(c.getNumeroTable(),commandes.getNumeroTable());
        assertFalse(commandes.isEtat());
    }

    @Test
    @DisplayName("test find by table")
    void testFindByTable(){
        c = cdao.create(c);
        Commandes commandes = cdao.findByTable(10);
        assertEquals(c.getNumeroTable(),commandes.getNumeroTable());
        assertFalse(commandes.isEtat());
    }

    @Test
    @DisplayName("Test trouve une commandes grâce à une commande interne ")
    void testfindCommandsFromCommand(){
        c = cdao.create(c);
        Commandes commandes = cdao.findCommandsFromCommand(cp.get_id());
        assertEquals(c.getNumeroTable(),commandes.getNumeroTable());
        assertFalse(commandes.isEtat());
    }

    @Test
    @DisplayName("Test trouve une CommandesPlats par rapport à son _id et son Etat")
    void testfindCommandesPlatsById(){
        c.change_etat_commande(0);
        c.change_etat_commande(1);
        c = cdao.create(c);
        List<CommandesPlats> commandes = cdao.findCommandesPlatsById(CommandesPlats.ETAT_PLAT.EN_PREPARATION);
        assertEquals(2,commandes.size());
    }

    @Test
    @DisplayName("Test trouve une CommandesPlats par rapport à son _id et son Etat Fail")
    void testfindCommandesPlatsByIdFail(){
        c = cdao.create(c);
        List<CommandesPlats> commandes = cdao.findCommandesPlatsById(CommandesPlats.ETAT_PLAT.EN_PREPARATION);
        assertEquals(0,commandes.size());
    }

    @Test
    @DisplayName("Test update")
    void testUpdateSucces(){
        c = cdao.create(c);
        c.change_etat_commande(0);
        cdao.update(c);
        Commandes cmds = cdao.find(c.get_id());
        assertEquals(CommandesPlats.ETAT_PLAT.EN_PREPARATION,cmds.getCommandesPlats().get(0).getEtat());
    }

    @Test
    @DisplayName("Test update date")
    void testUpdateSuccesTimeCheck(){
        c.addCommandes(cp3);
        c = cdao.create(c);
        cdao.update(c);
        Commandes cmds = cdao.find(c.get_id());
        assertEquals(LocalDateTime.of(2021,05,05,12,12,10)
                ,cmds.getCommandesPlats().get(2).getCommande());
        assertEquals(LocalDateTime.of(2021,05,05,12,12,15)
                ,cmds.getCommandesPlats().get(2).getEn_preparation());
        assertEquals(LocalDateTime.of(2021,05,05,12,12,20)
                ,cmds.getCommandesPlats().get(2).getServi());
    }

    @Test
    @DisplayName("Test update finir")
    void testUpdateSuccesFinir(){
        c = cdao.create(c);
        c.change_etat_commande(0);
        c.change_etat_commande(0);
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
