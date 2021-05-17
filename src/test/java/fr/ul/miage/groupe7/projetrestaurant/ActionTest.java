package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionTest {

    private static Action action, action2;
    private static Utilisateurs utilisateur, directeur;

    @BeforeAll
    static void setup(){
        utilisateur = new Utilisateurs("TestUser","TestUser", Utilisateurs.ROLE.CUISINIER,"TestUser","TestUser");
        directeur = new Utilisateurs("TestUserD","TestUserD", Utilisateurs.ROLE.DIRECTEUR,"TestUserD","TestUserD");

        action = new Action(1,"preparer les plats",new Utilisateurs.ROLE[]{Utilisateurs.ROLE.CUISINIER, Utilisateurs.ROLE.CUISINIER});

        HashSet<Utilisateurs.ROLE> droits2 = new HashSet<>();
        droits2.add(Utilisateurs.ROLE.SERVEUR);
        action2 = new Action(2,"servir un plat",new Utilisateurs.ROLE[]{Utilisateurs.ROLE.SERVEUR});
    }

    @Test
    @DisplayName("Vérification que l'utilisateur à le droits de faire une action")
    void verifierAleDroit(){
        assertTrue(action.peutFaire(utilisateur.getRole()));
    }

    @Test
    @DisplayName("Vérification que l'utilisateur n'a pas le droits de faire une action")
    void verifierPasLeDroit(){
        assertFalse(action2.peutFaire(utilisateur.getRole()));
    }

    @Test
    @DisplayName("Vérification qu'un directeur peut faire une action même s'il ne possède pas les droits")
    void verifierDirecteurDroit(){
        assertTrue(action2.peutFaire(directeur.getRole()));
    }

}
