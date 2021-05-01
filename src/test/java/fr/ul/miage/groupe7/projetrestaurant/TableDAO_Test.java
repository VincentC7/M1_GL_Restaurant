package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.Table;
import fr.ul.miage.groupe7.projetrestaurant.Database.TableDAO;
import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import fr.ul.miage.groupe7.projetrestaurant.Database.UtilisateursDAO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

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
        Table t = tableDAO.create(new Table(1, 1, Table.ETAT.PROPRE, utilisateur));
        Boolean res = tableDAO.delete(t);
        assertTrue(res);
    }

    @Test
    @DisplayName("Ne trouve pas la table à supprimer")
    void deleteUserFailed(){
        Table t = new Table(1, 1, Table.ETAT.PROPRE, utilisateur);
        Boolean res = tableDAO.delete(t);
        assertFalse(res);
    }

}
