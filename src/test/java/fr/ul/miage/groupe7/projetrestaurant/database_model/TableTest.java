package fr.ul.miage.groupe7.projetrestaurant.database_model;

import fr.ul.miage.groupe7.projetrestaurant.Database.Reservation;
import fr.ul.miage.groupe7.projetrestaurant.Database.Table;
import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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
        Utilisateurs u =  new Utilisateurs("Noirot","Quentin", Utilisateurs.ROLE.SERVEUR,"azerty","QNoirot");
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
            Table table = new Table(1, 1, null, new Utilisateurs("Noirot","Quentin", Utilisateurs.ROLE.CUISINIER,"azerty","QNoirot"));
        });
    }


    @Test
    @DisplayName("Vérfie qu'une reservation existe")
    void reservationExist(){
        Table table = new Table(1, 1, null, new Utilisateurs("Noirot","Quentin", Utilisateurs.ROLE.SERVEUR,"azerty","QNoirot"));
        table.addReservation( new Reservation(Reservation.CRENEAU.MATIN,"Luc", LocalDate.of(2021,6,17)));
        assertTrue(table.isReserved(LocalDate.of(2021,6,17),Reservation.CRENEAU.MATIN));
        assertFalse(table.isReserved(LocalDate.of(2021,6,17),Reservation.CRENEAU.SOIR));
    }

    @Test
    @DisplayName("Vérfie qu'une reservation est bien supprimé")
    void reservationDelete(){
        Table table = new Table(1, 1, null, new Utilisateurs("Noirot","Quentin", Utilisateurs.ROLE.SERVEUR,"azerty","QNoirot"));
        table.addReservation( new Reservation(Reservation.CRENEAU.MATIN,"Luc", LocalDate.of(2021,6,17)));
        assertEquals(1,table.getReservations().size());
        table.deleteReservation(LocalDate.of(2021,6,17),Reservation.CRENEAU.MATIN);
        assertEquals(0,table.getReservations().size());
    }

    @Test
    @DisplayName("Vérfie qu'une reservation est bien supprimé")
    void findReservation(){
        Table table = new Table(1, 1, null, new Utilisateurs("Noirot","Quentin", Utilisateurs.ROLE.SERVEUR,"azerty","QNoirot"));
        table.addReservation( new Reservation(Reservation.CRENEAU.MATIN,"Luc", LocalDate.of(2021,6,17)));
        assertNotNull(table.getReservation(LocalDate.of(2021,6,17),Reservation.CRENEAU.MATIN));
        assertNull(table.getReservation(LocalDate.of(2021,6,17),Reservation.CRENEAU.SOIR));
    }



    @Nested
    @DisplayName("Test sur les différents ETATs")
    class ETAT {

        private Table table;

        @Test
        @DisplayName("Changement d'état propre à occupée")
        void changeEtatPtoO(){
            Table table = new Table(1, 1, Table.ETAT.PROPRE, null);
            table.setEtat(Table.ETAT.OCCUPEE);
            assertEquals(Table.ETAT.OCCUPEE, table.getEtat());
        }

        @Test
        @DisplayName("Changement d'état occupée à sale")
        void changeEtatOtoS(){
            Table table = new Table(1, 1, Table.ETAT.OCCUPEE, null);
            table.setEtat(Table.ETAT.SALE);
            assertEquals(Table.ETAT.SALE, table.getEtat());
        }

        @Test
        @DisplayName("Changement d'état sale à propore")
        void changeEtatStoSS(){
            Table table = new Table(1, 1, Table.ETAT.SALE, null);
            table.setEtat(Table.ETAT.PROPRE);
            assertEquals(Table.ETAT.PROPRE, table.getEtat());
        }

        @Test
        @DisplayName("Réserver une table")
        void changeEtatR(){
            Table table = new Table(1, 1, Table.ETAT.PROPRE, null);
            table.setEtat(Table.ETAT.RESERVEE);
            assertEquals(Table.ETAT.RESERVEE, table.getEtat());
        }


        @Test
        @DisplayName("Occupée reste à occupée")
        void changeEtatOtoP(){
            Table table = new Table(1, 1, Table.ETAT.OCCUPEE, null);
            table.setEtat(Table.ETAT.PROPRE);
            assertEquals(Table.ETAT.OCCUPEE, table.getEtat());
        }

        @Test
        @DisplayName("Changement d'état non permit occupée à prore")
        void changeEtatOto0(){
            Table table = new Table(1, 1, Table.ETAT.OCCUPEE, null);
            table.setEtat(Table.ETAT.PROPRE);
            assertEquals(Table.ETAT.OCCUPEE, table.getEtat());
        }


    }


}
