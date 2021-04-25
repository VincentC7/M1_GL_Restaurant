package fr.ul.miage.groupe7.projetrestaurant;

import com.mongodb.client.MongoDatabase;
import fr.ul.miage.groupe7.projetrestaurant.Database.BDD_Connexion;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

public class TestBDD_Connexion {

    private static MongoDatabase bdd_connexion;

    @BeforeAll
    static void beforeAll(){
        bdd_connexion = BDD_Connexion.getInstance();
    }

    @Test
    void verifyConnexion() {
        assertEquals("restaurant",bdd_connexion.getName());
    }

    @Test
    void verifySingleton(){
        MongoDatabase db = BDD_Connexion.getInstance();
        assertSame(db,bdd_connexion);
    }

}
