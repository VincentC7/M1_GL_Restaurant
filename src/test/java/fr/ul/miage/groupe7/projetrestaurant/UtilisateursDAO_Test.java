package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import fr.ul.miage.groupe7.projetrestaurant.Database.UtilisateursDAO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UtilisateursDAO_Test {

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
    @DisplayName("Ne trouve pas un utilisateur par identifiant mdp")
    void findUtilisateurNull(){
        Utilisateurs u = userDAO.find("test","test");
        assertNull(u);
    }

    @Test
    @DisplayName("Ne trouve pas un utilisateur par id")
    void findUtilisateurByIdNull(){
        Utilisateurs u = userDAO.find(new ObjectId());
        assertNull(u);
    }


    @Test
    @DisplayName("Trouve un utilisateur par son _id")
    void findUtilisateurById(){
        //Utilisateurs u = userDAO.find("_id de l'utilisateur");
        assertTrue(false);
    }

    @Nested
    @DisplayName("Test sur les différents Rôle")
    class CREATE {

        Utilisateurs user;

        @AfterEach
        void init(){
            userDAO.delete(user);
        }

        @Test
        @DisplayName("Crée un utilisateur")
        void CreateUtilisateur(){
            Utilisateurs u = userDAO.create(new Utilisateurs("Noirot","Quentin","Serveur","azerty","Testing"));
            assertNotNull(u);
            assertEquals("Noirot",u.getNom());
            assertEquals("Quentin",u.getPrenom());
            assertEquals("Serveur",u.getRole());
        }

        @Test
        @DisplayName("Crée un utilisateur sans mdp")
        void CreateUtilisateurWithoutMdp(){
            Utilisateurs u = userDAO.create(new Utilisateurs("Noirot","Quentin","Serveur",null,"QNoirot"));
            assertNotNull(u);
            assertNotNull(u.getMdp());
            assertTrue(u.getMdp().matches("^[a-zA-Z0-9]*$"));
        }

        @Test
        @DisplayName("Crée un utilisateur sans identifiant")
        void CreateUtilisateurWithoutIdentifiant(){
            Utilisateurs u = userDAO.create(new Utilisateurs("Noirot","Quentin","Serveur","azerty",null));
            assertNotNull(u);
            assertNotNull(u.getIdentifiant());

        }
    }


}
