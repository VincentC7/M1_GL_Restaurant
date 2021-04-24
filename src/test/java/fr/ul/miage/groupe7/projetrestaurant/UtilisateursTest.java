package fr.ul.miage.groupe7.projetrestaurant;


import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import fr.ul.miage.groupe7.projetrestaurant.Database.UtilisateursDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class UtilisateursTest {

    static UtilisateursDAO userDAO;

    @BeforeAll
    static void init(){
        userDAO = new UtilisateursDAO();
    }

    @Test
    @DisplayName("Cherche un utilisateur")
    void findUtilisateur(){
        Utilisateurs u = userDAO.find("Esion","FCSilmi");
        assertEquals("Sion",u.getNom());
        assertEquals("Eva",u.getPrenom());
        assertEquals("Directeur",u.getRole());
    }

    @Test
    @DisplayName("Ne trouve pas un utilisateur")
    void findUtilisateurNull(){
        Utilisateurs u = userDAO.find("test","test");
        assertNull(u);
    }
}
