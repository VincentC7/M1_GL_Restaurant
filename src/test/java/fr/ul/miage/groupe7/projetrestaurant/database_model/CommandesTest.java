package fr.ul.miage.groupe7.projetrestaurant.database_model;

import com.mongodb.lang.NonNull;
import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Test
    @DisplayName("Statistique sur le profit du déjeuner")
    void stat_dejeuner(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);

        commandes1.setDebut(LocalDateTime.parse("2021-05-24T12:06:08.629742800"));
        commandes1.setPrix(new BigDecimal(29.5));

        commandes2.setDebut(LocalDateTime.parse("2021-05-24T14:06:08.629742800"));
        commandes2.setPrix(new BigDecimal(50.9));

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);

        assertEquals(Commandes.recetteRepas(commandes, true), new BigDecimal("40.20"));
    }

    @Test
    @DisplayName("Statistique sur le profit du diner")
    void stat_diner(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);

        commandes1.setDebut(LocalDateTime.parse("2021-05-24T20:06:08.629742800"));
        commandes1.setPrix(new BigDecimal(76.2));

        commandes2.setDebut(LocalDateTime.parse("2021-05-24T19:06:08.629742800"));
        commandes2.setPrix(new BigDecimal(45.9));

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);

        assertEquals(Commandes.recetteRepas(commandes, false), new BigDecimal("61.05"));
    }

    @Test
    @DisplayName("Erreur quand il n'y a pas de stat")
    void stat_error(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);

        assertEquals(0, Commandes.recetteRepas(commandes, true).compareTo(new BigDecimal("0.0")));
    }

    @Test
    @DisplayName("Erreur division par 0")
    void stat_divide_0(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);

        commandes1.setDebut(LocalDateTime.parse("2021-05-24T20:06:08.629742800"));
        commandes1.setPrix(new BigDecimal(76.2));

        commandes2.setDebut(LocalDateTime.parse("2021-05-24T19:06:08.629742800"));
        commandes2.setPrix(new BigDecimal(45.9));

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);

        assertEquals(Commandes.recetteRepas(commandes, true), new BigDecimal("0.0"));
    }

    @Test
    @DisplayName("Statistique sur le profit quotidien sur 1 jour ")
    void stat_quotidien1(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);
        Commandes commandes3 = new Commandes(3);

        commandes1.setDebut(LocalDateTime.parse("2021-05-24T18:06:08.629742800"));
        commandes1.setPrix(new BigDecimal(76.2));

        commandes2.setDebut(LocalDateTime.parse("2021-05-24T19:06:08.629742800"));
        commandes2.setPrix(new BigDecimal(45.9));

        commandes3.setDebut(LocalDateTime.parse("2021-05-24T20:06:08.629742800"));
        commandes3.setPrix(new BigDecimal(38.4));

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);commandes.add(commandes3);

        assertEquals(Commandes.recetteQuotidienne(commandes), new BigDecimal("160.50"));
    }

    @Test
    @DisplayName("Statistique sur le profit quotidien sur 2 jours ")
    void stat_quotidien2(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);
        Commandes commandes3 = new Commandes(3);

        commandes1.setDebut(LocalDateTime.parse("2021-05-24T20:06:08.629742800"));
        commandes1.setPrix(new BigDecimal(76.2));

        commandes2.setDebut(LocalDateTime.parse("2021-05-24T19:06:08.629742800"));
        commandes2.setPrix(new BigDecimal(45.9));

        commandes3.setDebut(LocalDateTime.parse("2021-05-25T20:06:08.629742800"));
        commandes3.setPrix(new BigDecimal(38.4));

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);commandes.add(commandes3);

        assertEquals(Commandes.recetteQuotidienne(commandes), new BigDecimal("80.25"));
    }

    @Test
    @DisplayName("Erreur quand il n'y a pas de stat quotidienne")
    void stat_quotidienne_error(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);

        assertEquals(0, Commandes.recetteQuotidienne(commandes).compareTo(new BigDecimal("0.0")));
    }

    @Test
    @DisplayName("Statistique sur le profit hebdomadaire sur 1 semaine ")
    void stat_hebdomadaire1(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);
        Commandes commandes3 = new Commandes(3);

        commandes1.setDebut(LocalDateTime.parse("2021-05-24T20:06:08.629742800"));
        commandes1.setPrix(new BigDecimal(76.2));

        commandes2.setDebut(LocalDateTime.parse("2021-05-25T20:06:08.629742800"));
        commandes2.setPrix(new BigDecimal(45.9));

        commandes3.setDebut(LocalDateTime.parse("2021-05-26T20:06:08.629742800"));
        commandes3.setPrix(new BigDecimal(38.4));

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);commandes.add(commandes3);

        assertEquals(Commandes.recetteHebdomadaire(commandes), new BigDecimal("160.50"));
    }

    @Test
    @DisplayName("Statistique sur le profit hebdomadaire sur 2 semaines ")
    void stat_hebdomadaire2(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);
        Commandes commandes3 = new Commandes(3);

        commandes1.setDebut(LocalDateTime.parse("2021-05-24T20:06:08.629742800"));
        commandes1.setPrix(new BigDecimal(76.2));

        commandes2.setDebut(LocalDateTime.parse("2021-05-25T20:06:08.629742800"));
        commandes2.setPrix(new BigDecimal(45.9));

        commandes3.setDebut(LocalDateTime.parse("2021-05-22T20:06:08.629742800"));
        commandes3.setPrix(new BigDecimal(38.4));

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);commandes.add(commandes3);

        assertEquals(Commandes.recetteHebdomadaire(commandes), new BigDecimal("80.25"));
    }

    @Test
    @DisplayName("Statistique sur le profit mensuel sur 1 mois ")
    void stat_mensuelle1(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);
        Commandes commandes3 = new Commandes(3);

        commandes1.setDebut(LocalDateTime.parse("2021-05-02T20:06:08.629742800"));
        commandes1.setPrix(new BigDecimal(76.2));

        commandes2.setDebut(LocalDateTime.parse("2021-05-20T20:06:08.629742800"));
        commandes2.setPrix(new BigDecimal(45.9));

        commandes3.setDebut(LocalDateTime.parse("2021-05-24T20:06:08.629742800"));
        commandes3.setPrix(new BigDecimal(38.4));

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);commandes.add(commandes3);

        assertEquals(Commandes.recetteMensuelle(commandes), new BigDecimal("160.50"));
    }

    @Test
    @DisplayName("Statistique sur le profit mensuel sur 2 mois ")
    void stat_mensuel2(){
        Commandes commandes1 = new Commandes(1);
        Commandes commandes2 = new Commandes(2);
        Commandes commandes3 = new Commandes(3);

        commandes1.setDebut(LocalDateTime.parse("2021-05-24T20:06:08.629742800"));
        commandes1.setPrix(new BigDecimal(76.2));

        commandes2.setDebut(LocalDateTime.parse("2021-06-20T20:06:08.629742800"));
        commandes2.setPrix(new BigDecimal(45.9));

        commandes3.setDebut(LocalDateTime.parse("2021-06-24T20:06:08.629742800"));
        commandes3.setPrix(new BigDecimal(38.4));

        ArrayList<Commandes> commandes = new ArrayList<>();
        commandes.add(commandes1);commandes.add(commandes2);commandes.add(commandes3);

        assertEquals(Commandes.recetteMensuelle(commandes), new BigDecimal("80.25"));
    }

}
