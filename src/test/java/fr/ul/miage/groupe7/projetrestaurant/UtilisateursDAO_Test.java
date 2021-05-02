package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.BDD_Connexion;
import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import fr.ul.miage.groupe7.projetrestaurant.Database.UtilisateursDAO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UtilisateursDAO_Test {

    static UtilisateursDAO userDAO;

    @BeforeAll
    static void init(){
        BDD_Connexion.setTest();
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
        Utilisateurs u1 = userDAO.find("Esion","FCSilmi");
        Utilisateurs u2 = userDAO.find(u1.get_id());
        assertNotNull(u2);
    }

    @Test
    @DisplayName("Trouve tous les serveur")
    void findServeurs(){
        var utlisateurs = userDAO.findAllServeur();
        assertEquals(1,utlisateurs.size());
    }

    @Test
    @DisplayName("Supprime un utilisateur")
    void deleteUser(){
        Utilisateurs user = userDAO.create(new Utilisateurs("Luc","Tristan","Serveur","tmgerp",null));
        Boolean res = userDAO.delete(user);
        assertTrue(res);
    }

    @Test
    @DisplayName("Supprime un utilisateur echec")
    void deleteUserFailed(){
        Utilisateurs user = new Utilisateurs("Luc","Tristan","Serveur","tmgerp",null);
        Boolean res = userDAO.delete(user);
        assertFalse(res);
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
            user = userDAO.create(new Utilisateurs("Noirot","Quentin","Serveur","azerty","Testing"));
            assertNotNull(user);
            assertEquals("Noirot",user.getNom());
            assertEquals("Quentin",user.getPrenom());
            assertEquals("Serveur",user.getRole());
        }

        @Test
        @DisplayName("Crée un utilisateur sans mdp")
        void CreateUtilisateurWithoutMdp(){
            user = userDAO.create(new Utilisateurs("Noirot","Quentin","Serveur",null,"QNoirot"));
            assertNotNull(user);
            assertNotNull(user.getMdp());
            assertTrue(user.getMdp().matches("^[a-zA-Z0-9]*$"));
        }

        @Test
        @DisplayName("Crée un utilisateur sans identifiant")
        void CreateUtilisateurWithoutIdentifiant(){
            user = userDAO.create(new Utilisateurs("Noirot","Quentin","Serveur","azerty",null));
            assertNotNull(user);
            assertNotNull(user.getIdentifiant());

        }
    }


}
