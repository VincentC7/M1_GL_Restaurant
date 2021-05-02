package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.Table;
import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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


}
