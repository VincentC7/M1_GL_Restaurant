package fr.ul.miage.groupe7.projetrestaurant;


import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import fr.ul.miage.groupe7.projetrestaurant.Database.UtilisateursDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilisateursTest {

    static UtilisateursDAO userDAO;

    @BeforeAll
    static void init(){
        userDAO = new UtilisateursDAO();
    }

    @Test
    @DisplayName("Cherche un utilisateur par son identifiant et mdp")
    void findUtilisateurByIdentifiantAndMdp(){
        Utilisateurs u = userDAO.find("Esion","FCSilmi");
        assertEquals("Sion",u.getNom());
        assertEquals("Eva",u.getPrenom());
        assertEquals("Directeur",u.getRole());
        assertTrue(u.isMdp("FCSilmi"));
    }

    @Test
    @DisplayName("Ne trouve pas un utilisateur")
    void findUtilisateurNull(){
        Utilisateurs u = userDAO.find("test");
        assertNull(u);
    }

    @Test
    @DisplayName("Check l'affichage toString")
    void checkUtilisateur_toString(){
        Utilisateurs u = userDAO.find("Esion","FCSilmi");
        var stringReference =
                "===================================\r\n" +
                "nom         : Sion\r\n" +
                "prénom      : Eva\r\n" +
                "role        : Directeur\r\n" +
                "identifiant : Esion\r\n"+
                "===================================\r\n";
        assertEquals(stringReference,u.toString());
    }

    @Test
    @DisplayName("Trouve un utilisateur par son _id")
    void findUtilisateurById(){
        assertTrue(true);
        //TODO
    }


}
