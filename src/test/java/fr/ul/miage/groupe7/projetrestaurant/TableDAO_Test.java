package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.Table;
import fr.ul.miage.groupe7.projetrestaurant.Database.TableDAO;
import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import fr.ul.miage.groupe7.projetrestaurant.Database.UtilisateursDAO;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TableDAO_Test {

    private static TableDAO tableDAO;
    private static UtilisateursDAO utilisateursDAO;

    private static Table table;
    private static Utilisateurs utilisateur;

    @BeforeAll
    static void init(){
        tableDAO = new TableDAO();
        utilisateursDAO = new UtilisateursDAO();

        utilisateur = utilisateursDAO.create(new Utilisateurs("Luc","Tristan","Serveur","tmgerp",null));
        table = tableDAO.create(new Table(1, 1, Table.ETAT.PROPRE, utilisateur));
    }

    @AfterAll
    static void end(){
        utilisateursDAO.delete(utilisateur);
        tableDAO.delete(table);
    }


    @Test
    @DisplayName("Trouve une table par son id")
    void finTableById(){
        assertNotNull(tableDAO.find(table.get_id()));
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
    @DisplayName("Supprime une table")
    void deleteUser(){
        Table t = tableDAO.create(new Table(1, 2, Table.ETAT.PROPRE, utilisateur));
        Boolean res = tableDAO.delete(t);
        assertTrue(res);
    }

    @Test
    @DisplayName("Ne trouve pas la table à supprimer")
    void deleteUserFailed(){
        Table t = new Table(1, 2, Table.ETAT.PROPRE, utilisateur);
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
        Utilisateurs u = new Utilisateurs("Luc","Tristan","Directeur","tmgerp",null);

        assertThrows(IllegalArgumentException.class,() -> {
            tableDAO.findByServeur(u);
        });
    }

}
