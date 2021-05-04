package fr.ul.miage.groupe7.projetrestaurant.DAO_Test;

import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TableDAO_Test {

    private static TableDAO tableDAO;
    private static UtilisateursDAO utilisateursDAO;

    private static Table table,table2;
    private static Utilisateurs utilisateur;

    @BeforeEach
    void init(){
        BDD_Connexion.setTest();
        tableDAO = new TableDAO();
        utilisateursDAO = new UtilisateursDAO();

        utilisateur = utilisateursDAO.create(new Utilisateurs("Luc","Tristan", Utilisateurs.ROLE.SERVEUR,"tmgerp",null));
        table = tableDAO.create(new Table(1, 1, Table.ETAT.PROPRE, utilisateur));
    }

    @AfterEach
    void end(){
        utilisateursDAO.delete(utilisateur);
        tableDAO.delete(table);
        tableDAO.delete(table2);
    }


    @Test
    @DisplayName("Trouve une table par son id")
    void finTableById(){
        Table t = tableDAO.find(table.get_id());
        assertNotNull(t);
        assertNotNull(t.getServeur());

    }

    @Test
    @DisplayName("Ne trouve pas une table par son id")
    void finTableByIdNull(){
        Table t = tableDAO.create(new Table(1, 1, Table.ETAT.PROPRE, utilisateur));
        tableDAO.delete(t);
        assertNull(tableDAO.find(t.get_id()));
    }

    @Test
    @DisplayName("Trouve une table par son numéro")
    void finTableByNum(){
        assertNotNull(tableDAO.findByNum(table.getNumero()));
    }

    @Test
    @DisplayName("Ne trouve pas une table par son numéro")
    void finTableByNumNull(){
        assertNull(tableDAO.findByNum(-3));
    }

    @Test
    @DisplayName("Trouve les tables par serveur")
    void findTableByServeurNull(){
        var tables = tableDAO.findByServeurNull();
        assertEquals(0,tables.size());
    }

    @Test
    @DisplayName("Trouve toutes les tables")
    void findTableAll(){
        var tables = tableDAO.findAll();
        assertEquals(1,tables.size());
    }

    @Test
    @DisplayName("Supprime une table")
    void deleteTable(){
        Table t = tableDAO.create(new Table(1, 1, Table.ETAT.PROPRE, utilisateur));
        Boolean res = tableDAO.delete(t);
        assertTrue(res);
    }

    @Test
    @DisplayName("Ne trouve pas la table à supprimer")
    void deleteTableFailed(){
        Table t = new Table(1, 1, Table.ETAT.PROPRE, utilisateur);
        Boolean res = tableDAO.delete(t);
        assertFalse(res);
    }

    @Test
    @DisplayName("Trouve les tables d'un serveur")
    void findServeurTable(){
        Table t1 = tableDAO.create(new Table(2,1, Table.ETAT.PROPRE, utilisateur));
        Table t2 = tableDAO.create(new Table(2,2, Table.ETAT.PROPRE, utilisateur));

        List<Table> tables = tableDAO.findByServeur(utilisateur);
        assertEquals(3, tables.size());
        assertEquals(2, tables.get(1).getEtage());

        tableDAO.delete(t1);
        tableDAO.delete(t2);
    }

    @Test
    @DisplayName("Exception quand un utilisateur qui n'est pas un serveur essaye de voir ses tables")
    void findTableException(){
        Utilisateurs u = new Utilisateurs("Luc","Tristan",Utilisateurs.ROLE.DIRECTEUR,"tmgerp",null);

        assertThrows(IllegalArgumentException.class,() -> {
            tableDAO.findByServeur(u);
        });
    }

    @Test
    @DisplayName("Changer l'état d'une table")
    void changeEtat(){
        table.setEtat(Table.ETAT.OCUPEE);
        tableDAO.update(table);
        Table t2 = tableDAO.find(table.get_id());

        assertEquals(Table.ETAT.OCUPEE, t2.getEtat());
    }

    @Test
    @DisplayName("Ajoute une reservation")
    void ajoutTableReservation(){
        Table t = new Table(1, 1, Table.ETAT.PROPRE, utilisateur);
        t.addReservation( new Reservation(Reservation.CRENEAU.MATIN,"Luc", LocalDate.of(2021,6,17)));
        assertEquals(1,t.getReservations().size());

    }

    @Test
    @DisplayName("Ajoute une reservation")
    void ajoutTableReservationBDD(){
        table2 = new Table(1, 2, Table.ETAT.PROPRE);
        table2.addReservation( new Reservation(Reservation.CRENEAU.MATIN,"Luc", LocalDate.of(2021,6,17)));
        table2 = tableDAO.create(table2);
        assertEquals(1,table2.getReservations().size());
        assertTrue(table2.getReservations().stream().anyMatch(reservation -> reservation.getNom().equals("Luc")));
        assertTrue(table2.getReservations().stream().anyMatch(reservation -> reservation.getCreneau().equals("Matin")));
        assertTrue(table2.getReservations().stream().anyMatch(reservation -> reservation.getDate().equals(LocalDate.of(2021,6,17))));
    }

    @Test
    @DisplayName("Ajoute une reservation à la même date qu'une autre")
    void ajoutTableRéservationMemeDate(){
        table2 = new Table(1, 2, Table.ETAT.PROPRE);
        table2.addReservation( new Reservation(Reservation.CRENEAU.MATIN,"Luc", LocalDate.of(2021,6,17)));
        table2.addReservation( new Reservation(Reservation.CRENEAU.MATIN,"Noirot", LocalDate.of(2021,6,17)));
        assertEquals(1,table2.getReservations().size());
    }

    @Test
    @DisplayName("Ajoute une reservation à la même date qu'une autre")
    void ajoutTableRéservationMemeDatePasMemeCreneau(){
        table2 = new Table(1, 2, Table.ETAT.PROPRE);
        table2.addReservation( new Reservation(Reservation.CRENEAU.MATIN,"Luc", LocalDate.of(2021,6,17)));
        table2.addReservation( new Reservation(Reservation.CRENEAU.SOIR,"Noirot", LocalDate.of(2021,6,17)));
        assertEquals(2,table2.getReservations().size());
    }

    @Nested
    @DisplayName("Test sur le update")
    class UPDATE {


        private Utilisateurs utilisateur2 = new Utilisateurs("Noirot", "Quentin", Utilisateurs.ROLE.SERVEUR, "azerty", "QNoirot");

        
        @AfterEach
        void afterEach() {
            table.setServeur(utilisateur);
            tableDAO.update(table);
            utilisateursDAO.delete(utilisateur2);

        }

        @Test
        @DisplayName("Succes sur l'update du serveur")
        void updateServeurSucces() {
            utilisateur2 = utilisateursDAO.create(utilisateur2);
            table.setServeur(utilisateur2);
            Table t = tableDAO.update(table);
            assertEquals(utilisateur2, t.getServeur());
            assertEquals("Noirot", t.getServeur().getNom());
        }

        @Test
        @DisplayName("Echec sur l'update du serveur car inexistant dans la base")
        void updateServeurFailServeurInexistant() {
            table.setServeur(utilisateur2);
            Table t = tableDAO.update(table);
            assertNotEquals(utilisateur2, t.getServeur());
            assertEquals(utilisateur, t.getServeur());
            assertEquals("Luc", t.getServeur().getNom());
        }

        @Test
        @DisplayName("Succes sur l'update du serveur avec null")
        void updateServeurSuccesNull() {
            table.setServeur(null);
            Table t = tableDAO.update(table);
            assertNull(t.getServeur());
        }

        @Test
        @DisplayName("Ajoute d'une reservation sur table existante")
        void ajoutTableReservationBDD(){
            table.addReservation( new Reservation(Reservation.CRENEAU.MATIN,"Luc", LocalDate.of(2021,6,17)));
            table = tableDAO.update(table);
            assertEquals(1,table.getReservations().size());
            assertTrue(table.getReservations().stream().anyMatch(reservation -> reservation.getNom().equals("Luc")));
            assertTrue(table.getReservations().stream().anyMatch(reservation -> reservation.getCreneau().equals("Matin")));
            assertTrue(table.getReservations().stream().anyMatch(reservation -> reservation.getDate().equals(LocalDate.of(2021,6,17))));
        }

    }


}
