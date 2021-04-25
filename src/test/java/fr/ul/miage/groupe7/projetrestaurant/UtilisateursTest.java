package fr.ul.miage.groupe7.projetrestaurant;


import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilisateursTest {



    @Test
    @DisplayName("Créer un Utilisateur")
    void CreateUserSucces() throws IllegalArgumentException  {

        Utilisateurs u = new Utilisateurs("Noirot","Quentin","Serveur","azerty","QNoirot");
        assertEquals("Noirot",u.getNom());
        assertEquals("Quentin",u.getPrenom());
        assertEquals("Serveur",u.getRole());
        assertEquals("azerty",u.getMdp());
        assertEquals("QNoirot",u.getIdentifiant());
    }

    @Test
    @DisplayName("Créer un Utilisateur avec param NonNull à null")
    void CreateUserParamNull() throws IllegalArgumentException  {
        assertThrows(NullPointerException.class,() -> {
            Utilisateurs u = new Utilisateurs(null,"Quentin","Serveur","azerty","QNoirot");
        });
    }

    @Test
    @DisplayName("Créer un Utilisateur avec un nom trop court")
    void CreateUserParamNomTooShort() throws IllegalArgumentException  {
        assertThrows(IllegalArgumentException.class,() -> {
            Utilisateurs u = new Utilisateurs("a","Quentin","Serveur","azerty","QNoirot");
        });
    }

    @Test
    @DisplayName("Créer un Utilisateur avec un nom de la taille minimale")
    void CreateUserParamNomExactSize() throws IllegalArgumentException  {
        assertDoesNotThrow(() -> {
            Utilisateurs u = new Utilisateurs("Li","Quentin","Serveur","azerty","QNoirot");
        });
    }

    @Test
    @DisplayName("Créer un Utilisateur avec un prenom trop court")
    void CreateUserParamPrenomTooShort() throws IllegalArgumentException  {
        assertThrows(IllegalArgumentException.class,() -> {
            Utilisateurs u = new Utilisateurs("Noirot","s","Serveur","azerty","QNoirot");
        });
    }

    @Test
    @DisplayName("Créer un Utilisateur avec un prenom de la taille minimale")
    void CreateUserParamPrenomExactSize() throws IllegalArgumentException  {
        assertDoesNotThrow(() -> {
            Utilisateurs u = new Utilisateurs("Noirot","Si","Serveur","azerty","QNoirot");
        });
    }



    @Nested
    @DisplayName("Test sur les différents Rôle")
    class ROLE {

        @Test
        @DisplayName("Créer un Utilisateur avec un role inexistant")
        void CreateUserParamRoleNonExisantant() throws IllegalArgumentException  {
            assertThrows(IllegalArgumentException.class,() -> {
                Utilisateurs u = new Utilisateurs("Noirot","Quentin","Danseur","azerty","QNoirot");
            });
        }

        @Test
        @DisplayName("Créer un Utilisateur avec un role serveur")
        void CreateUserParamRoleServeur() throws IllegalArgumentException  {
            assertDoesNotThrow(() -> {
                Utilisateurs u = new Utilisateurs("Noirot","Quentin","Serveur","azerty","QNoirot");
            });
        }

        @Test
        @DisplayName("Créer un Utilisateur avec un role directeur")
        void CreateUserParamRoleDirecteur() throws IllegalArgumentException  {
            assertDoesNotThrow(() -> {
                Utilisateurs u = new Utilisateurs("Noirot","Quentin","Directeur","azerty","QNoirot");
            });
        }

        @Test
        @DisplayName("Créer un Utilisateur avec un role cuisinier")
        void CreateUserParamRoleCuisinier() throws IllegalArgumentException  {
            assertDoesNotThrow(() -> {
                Utilisateurs u = new Utilisateurs("Noirot","Quentin","Cuisinier","azerty","QNoirot");
            });
        }

        @Test
        @DisplayName("Créer un Utilisateur avec un role assistant service")
        void CreateUserParamRoleAssistantService() throws IllegalArgumentException  {
            assertDoesNotThrow(() -> {
                Utilisateurs u = new Utilisateurs("Noirot","Quentin","Assistant service","azerty","QNoirot");
            });
        }

        @Test
        @DisplayName("Créer un Utilisateur avec un role maître d'hôtel")
        void CreateUserParamRoleMaitreHotel() throws IllegalArgumentException  {
            assertDoesNotThrow(() -> {
                Utilisateurs u = new Utilisateurs("Noirot","Quentin","Maître d'hôtel","azerty","QNoirot");
            });
        }

    }

    @Test
    @DisplayName("Créer un Utilisateur avec mdp null")
    void CreateUserWithMdpNull() throws IllegalArgumentException  {
        assertDoesNotThrow(() -> {
            Utilisateurs u = new Utilisateurs("Noirot","Quentin","Maître d'hôtel",null,"QNoirot");
        });
    }

    @Test
    @DisplayName("Créer un Utilisateur avec mdp trop court")
    void CreateUserWithMdpTooShort() throws IllegalArgumentException  {
        assertThrows(IllegalArgumentException.class,() -> {
            Utilisateurs u = new Utilisateurs("Noirot","Quentin","Maître d'hôtel","azert","QNoirot");
        });
    }

    @Test
    @DisplayName("Créer un Utilisateur avec identifiant null")
    void CreateUserWithIdentifiantNull() throws IllegalArgumentException  {
        assertDoesNotThrow(() -> {
            Utilisateurs u = new Utilisateurs("Noirot","Quentin","Maître d'hôtel","azerty",null);
        });
    }

    @Test
    @DisplayName("Créer un Utilisateur avec Identifiant de tailel trop petite")
    void CreateUserWithIdentifiantTooShort() throws IllegalArgumentException  {
        assertThrows(IllegalArgumentException.class,() -> {
            Utilisateurs u = new Utilisateurs("Li","Si","Maître d'hôtel","azerty","li");
        });
    }

    @Test
    @DisplayName("Créer un Utilisateur avec Identifiant de taille minimal")
    void CreateUserWithIdentifiantExactSize() throws IllegalArgumentException  {
        assertThrows(IllegalArgumentException.class,() -> {
            Utilisateurs u = new Utilisateurs("Li","Si","Maître d'hôtel","azerty","Sli");
        });
    }

    @Test
    @DisplayName("Check l'affichage toString")
    void checkUtilisateur_toString(){
        Utilisateurs u = new Utilisateurs("Sion","Eva","Directeur","FCSilmi","Esion");
        var stringReference =
                "===================================\r\n" +
                        "nom         : Sion\r\n" +
                        "prénom      : Eva\r\n" +
                        "role        : Directeur\r\n" +
                        "identifiant : Esion\r\n"+
                        "===================================\r\n";
        assertEquals(stringReference,u.toString());
    }

}





