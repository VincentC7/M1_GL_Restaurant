package fr.ul.miage.groupe7.projetrestaurant.database_model;

import com.mongodb.lang.NonNull;
import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CommandesTest {

    private CommandesPlats cp,cp2;
    private Commandes c;
    private static Plats p;
    private static PlatsDAO pdao;
    private static ObjectId id;

    @BeforeAll
    static void beforeAll(){
        BDD_Connexion.setTest();
        pdao = new PlatsDAO();
        Map<ObjectId,BigDecimal> hm = new HashMap<>();
        String hex3 ="c".repeat(24);
        hm.put(new ObjectId(hex3),new BigDecimal(150));
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
        c =new Commandes(10);

    }

    @Test
    @DisplayName("Ajout Commandes")
    void méthode_ajout_commandes(){
        assertEquals(0,c.getCommandesPlats().size());
        c.addCommandes(cp);
        assertEquals(1,c.getCommandesPlats().size());
        c.addCommandes(cp2);
        assertEquals(2,c.getCommandesPlats().size());
    }

    @Test
    @DisplayName("Change etat commande index")
    void méthode_change_etat_commande_index(){
        c.addCommandes(cp);
        c.addCommandes(cp2);
        c.change_etat_commande(1);
        assertEquals(CommandesPlats.ETAT_PLAT.EN_PREPARATION,c.getCommandesPlats().get(1).getEtat());
    }

    @Test
    @DisplayName("Change etat commande false index")
    void méthode_change_etat_commande_false_index(){
        c.addCommandes(cp);
        c.addCommandes(cp2);
        assertFalse(c.change_etat_commande(2));

    }

    @Test
    @DisplayName("Change etat commande id")
    void méthode_change_etat_commande_id(){
        c.addCommandes(cp);
        c.addCommandes(cp2);
        c.change_etat_commande(cp.get_id());
        assertEquals(CommandesPlats.ETAT_PLAT.EN_PREPARATION,c.getCommandesPlats().get(0).getEtat());
    }

    @Test
    @DisplayName("Change etat commande false id")
    void méthode_change_etat_commande_false_id(){
        c.addCommandes(cp);
        c.addCommandes(cp2);
        assertFalse(c.change_etat_commande(new ObjectId("1".repeat(24))));

    }

    @Test
    @DisplayName("Commandes fini")
    void méthode_finir(){
        c.addCommandes(cp);
        c.addCommandes(cp2);
        c.change_etat_commande(0);
        c.change_etat_commande(0);
        c.change_etat_commande(0);
        c.change_etat_commande(1);
        c.change_etat_commande(1);
        c.change_etat_commande(1);
        c.finir();
        assertTrue(c.isEtat());
        assertEquals(31,c.getPrix().doubleValue());

    }

    @Test
    @DisplayName("Commandes fini fail")
    void méthode_fini_fail(){
        c.addCommandes(cp);
        c.addCommandes(cp2);
        c.change_etat_commande(0);
        c.change_etat_commande(0);
        c.change_etat_commande(1);
        c.finir();
        assertFalse(c.isEtat());
    }

    @Test
    @DisplayName("Editer la facture")
    void edit_facture(){
        c.addCommandes(cp);
        c.addCommandes(cp2);

        c.change_etat_commande(0);
        c.change_etat_commande(0);
        c.change_etat_commande(0);
        c.change_etat_commande(1);
        c.change_etat_commande(1);
        c.change_etat_commande(1);
        c.finir();
        String facture = c.genererFacture();
        String line_separator = System.lineSeparator();
        String expected =
                "================================" + line_separator +
                "= PlatTest              15.50€ =" + line_separator +
                "= PlatTest              15.50€ =" + line_separator +
                "=                              =" + line_separator +
                "= Total à payer         31.00€ =" + line_separator +
                "================================" + line_separator;
        assertEquals(expected, facture);
    }

    @Test
    @DisplayName("Editer la facture avant que la commande soit terminée")
    void edit_facture_avant_la_fin(){
        c.addCommandes(cp);
        String facture = c.genererFacture();
        String expected = "La commande n'est pas terminée, la facture ne peut pas être éditer";
        assertEquals(expected, facture);
    }
}
