package fr.ul.miage.groupe7.projetrestaurant.database_model;

import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatPlatTest {

    private static Commandes c;
    private static Plats p,p2,p3;
    private static PlatsDAO pdao;
    private static CommandesDAO commandesDAO;

    @BeforeAll
    static void beforeAll(){
        BDD_Connexion.setTest();
        pdao = new PlatsDAO();
        commandesDAO = new CommandesDAO();
        Map<ObjectId, BigDecimal> hm = new HashMap<>();
        String hex3 ="c".repeat(24);
        hm.put(new ObjectId(hex3),new BigDecimal(150));
        p = new Plats("PlatTest1",hm,new BigDecimal("15.50"));
        p2 = new Plats("PlatTest2",hm,new BigDecimal("15.50"));
        p3 = new Plats("PlatTest3",hm,new BigDecimal("15.50"));
        p = pdao.create(p);
        p2 = pdao.create(p2);
        p3 = pdao.create(p3);
        CommandesPlats cp = new CommandesPlats(p.get_id());
        CommandesPlats cp2 = new CommandesPlats(p2.get_id());
        CommandesPlats cp3 = new CommandesPlats(p3.get_id());
        c = new Commandes(10);
        c.addCommandes(cp);
        c.addCommandes(cp2);
        c.addCommandes(cp3);
        for (int i = 0; i < 3; i++) {
            c.change_etat_commande(i);
            c.change_etat_commande(i);
            c.change_etat_commande(i);
        }
        c = commandesDAO.create(c);
    }

    @AfterAll
    static void afterAll(){
        pdao.delete(p);
        pdao.delete(p2);
        pdao.delete(p3);
        commandesDAO.delete(c);
    }

    @Test
    @DisplayName("Statistiques sur la popularité des plats")
    void stat_popularite_plats(){
        String seperator = System.lineSeparator();
        String expected =
                "Statistiques sur la popularité des plats" + seperator +
                        "\tPlatTest1 : 33.33% des commandes"+ seperator +
                        "\tPlatTest2 : 33.33% des commandes"+ seperator +
                        "\tPlatTest3 : 33.33% des commandes"+ seperator;
        assertEquals(expected, Plats.statistiquesPlats());
    }
}
