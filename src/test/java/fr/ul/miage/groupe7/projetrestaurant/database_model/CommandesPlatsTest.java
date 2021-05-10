package fr.ul.miage.groupe7.projetrestaurant.database_model;


import fr.ul.miage.groupe7.projetrestaurant.Database.CommandesPlats;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandesPlatsTest {

    private CommandesPlats cp;



    @BeforeEach
    void init(){
        cp = new CommandesPlats(new ObjectId("1".repeat(24)));
    }


    @Test
    @DisplayName("Constructeur avec un argument")
    void constructeur1argument(){
        assertEquals(CommandesPlats.ETAT_PLAT.COMMANDE,cp.getEtat());
        assertEquals(new ObjectId("1".repeat(24)),cp.getIdPlat());
    }

    @Test
    @DisplayName("Méthode next_etat => EN_PREPARATION")
    void methode_next_etat_1(){
        assertNull(cp.getEn_preparation());
        cp.next_etat();
        assertEquals(CommandesPlats.ETAT_PLAT.EN_PREPARATION,cp.getEtat());
        assertNotNull(cp.getEn_preparation());
    }

    @Test
    @DisplayName("Méthode next_etat => PRET")
    void methode_next_etat_2() throws InterruptedException {
        cp.next_etat();
        Thread.sleep(10);
        cp.next_etat();
        assertEquals(CommandesPlats.ETAT_PLAT.PRET,cp.getEtat());
        assertTrue(cp.getEn_preparation().isBefore(cp.getTermine()));
        assertTrue(cp.getPreparationTime() > 0);
    }

    @Test
    @DisplayName("Méthode next_etat => SERVI")
    void methode_next_etat_3() throws InterruptedException {
        cp.next_etat();
        Thread.sleep(10);
        cp.next_etat();
        Thread.sleep(10);
        cp.next_etat();
        assertEquals(CommandesPlats.ETAT_PLAT.SERVI,cp.getEtat());
    }

}
