package fr.ul.miage.groupe7.projetrestaurant;

import com.mongodb.client.MongoDatabase;
import fr.ul.miage.groupe7.projetrestaurant.Database.BDD_Connexion;
import fr.ul.miage.groupe7.projetrestaurant.service.GeneralProperties;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TestBDD_Connexion {

    private static MongoDatabase bdd_connexion;

    @BeforeEach
    void init(){
        bdd_connexion = BDD_Connexion.getInstance();
    }

    @Test
    void verifyConnexion() {
        assertEquals("restaurant",bdd_connexion.getName());
    }

    @Test
    void verifyConnexionTest() throws NoSuchFieldException, IllegalAccessException {
        Field instance = BDD_Connexion.class.getDeclaredField("bdd_connexion");
        instance.setAccessible(true);
        instance.set(null,null);
        BDD_Connexion.setTest();
        String DatabaseTestName = GeneralProperties.getPropertie(BDD_Connexion.DATABASE_TEST);
        bdd_connexion = BDD_Connexion.getInstance();
        assertEquals(DatabaseTestName,bdd_connexion.getName());
        instance.setAccessible(true);
        instance.set(null,null);
        BDD_Connexion.setDev();
    }

    @Test
    void verifySingleton(){
        MongoDatabase db = BDD_Connexion.getInstance();
        assertSame(db,bdd_connexion);
    }

    @AfterAll
    static void end() throws NoSuchFieldException, IllegalAccessException{
        Field instance = BDD_Connexion.class.getDeclaredField("bdd_connexion");
        instance.setAccessible(true);
        instance.set(null,null);
        BDD_Connexion.setTest();
    }

}
