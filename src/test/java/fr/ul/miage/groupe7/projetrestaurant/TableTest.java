package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TableTest {

    @Test
    @DisplayName("Créer une table")
    void create(){
        Table table = new Table(1, 1, Table.ETAT.PROPRE, null);

        assertEquals(1, table.getEtage());
        assertEquals(1, table.getNumero());
        assertEquals(Table.ETAT.PROPRE, table.getEtat());
        assertNull(table.getServeur());
    }

    @Test
    @DisplayName("Créer une table avec un serveur")
    void createwithServeur(){
        Utilisateurs u =  new Utilisateurs("Noirot","Quentin","Serveur","azerty","QNoirot");
        Table table = new Table(1, 1, null, u);
        assertEquals(u.getRole(), table.getServeur().getRole());
    }

    @Test
    @DisplayName("Exception pour un numéro d'étage inférieur à 1")
    void createTablewithIllegalEtage(){
        assertThrows(IllegalArgumentException.class,() -> {
            Table table = new Table(-2, 1, null, null);
        });
    }

    @Test
    @DisplayName("Exception pour un numéro de table inférieur à 1")
    void createTablewithIllegalNum(){
        assertThrows(IllegalArgumentException.class,() -> {
            Table table = new Table(1, -1, null, null);
        });
    }

    @Test
    @DisplayName("Etat par defaut")
    void createTablewithoutEtat(){
        Table table = new Table(1, 1, null, null);
        assertEquals(Table.ETAT.PROPRE, table.getEtat());
    }

    @Test
    @DisplayName("Exception quand l'utilisateur qui n'est pas un serveur")
    void createTablewithoutServeur(){
        assertThrows(IllegalArgumentException.class,() -> {
            Table table = new Table(1, 1, null, new Utilisateurs("Noirot","Quentin","Cuisinier","azerty","QNoirot"));
        });
    }

    @Nested
    @DisplayName("Test sur les différents ETATs")
    class ETAT {

        private Table table;


        @Test
        @DisplayName("Changement d'état propre à occupée")
        void changeEtatPtoO(){
            Table table = new Table(1, 1, Table.ETAT.PROPRE, null);
            table.setEtat(Table.ETAT.OCUPEE);
            assertEquals(Table.ETAT.OCUPEE, table.getEtat());
        }

        @Test
        @DisplayName("Changement d'état ocupée à sale")
        void changeEtatOtoS(){
            Table table = new Table(1, 1, Table.ETAT.OCUPEE, null);
            table.setEtat(Table.ETAT.SALE);
            assertEquals(Table.ETAT.SALE, table.getEtat());
        }

        @Test
        @DisplayName("Changement d'état sale à second service")
        void changeEtatStoSS(){
            Table table = new Table(1, 1, Table.ETAT.SALE, null);
            table.setEtat(Table.ETAT.SECOND_SERVICE);
            assertEquals(Table.ETAT.SECOND_SERVICE, table.getEtat());
        }

        @Test
        @DisplayName("Changement d'état seconde service à prore")
        void changeEtatSStoP(){
            Table table = new Table(1, 1, Table.ETAT.SECOND_SERVICE, null);
            table.setEtat(Table.ETAT.PROPRE);
            assertEquals(Table.ETAT.PROPRE, table.getEtat());
        }

        @Test
        @DisplayName("Réserver une table")
        void changeEtatR(){
            Table table = new Table(1, 1, Table.ETAT.SECOND_SERVICE, null);
            table.setEtat(Table.ETAT.RESERVEE);
            assertEquals(Table.ETAT.RESERVEE, table.getEtat());
        }


        @Test
        @DisplayName("Exception changement d'état non permit ocupée à propre")
        void changeEtatOtoP(){
            Table table = new Table(1, 1, Table.ETAT.OCUPEE, null);
            assertThrows(IllegalArgumentException.class,() -> {
                table.setEtat(Table.ETAT.PROPRE);
            });
        }

        @Test
        @DisplayName("Exception changement d'état non permit ocupée à ocupée")
        void changeEtatOto0(){
            Table table = new Table(1, 1, Table.ETAT.OCUPEE, null);
            assertThrows(IllegalArgumentException.class,() -> {
                table.setEtat(Table.ETAT.OCUPEE);
            });
        }

        @Test
        @DisplayName("Exception changement d'état non permit ocupée à second service")
        void changeEtatOtoSS(){
            Table table = new Table(1, 1, Table.ETAT.OCUPEE, null);
            assertThrows(IllegalArgumentException.class,() -> {
                table.setEtat(Table.ETAT.SECOND_SERVICE);
            });
        }


    }


}
