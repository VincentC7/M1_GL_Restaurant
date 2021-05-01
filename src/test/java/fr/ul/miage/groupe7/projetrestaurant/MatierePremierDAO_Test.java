package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.BDD_Connexion;
import fr.ul.miage.groupe7.projetrestaurant.Database.MatierePremiere;
import fr.ul.miage.groupe7.projetrestaurant.Database.MatierePremiereDAO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatierePremierDAO_Test {

    static MatierePremiereDAO matierePremiereDAO;

    @BeforeAll
    static void beforeAll(){
        BDD_Connexion.setTest();
        matierePremiereDAO = new MatierePremiereDAO();
    }

    @Test
    @DisplayName("Creation d'une matiere premiere que ne devrait pas lever d'erreurs")
    void insererMatierePremierSimple() {
        MatierePremiere matierePremiere = matierePremiereDAO.create(
                new MatierePremiere("Poisson",2, MatierePremiere.UNITE.SIMPLE_UNITE)
        );
        assertNotNull(matierePremiere);
        assertEquals("Poisson", matierePremiere.getNom());
        assertEquals(2, matierePremiere.getQuantitee());
        assertEquals(MatierePremiere.UNITE.SIMPLE_UNITE, matierePremiere.getUnite());
    }

    @Test
    @DisplayName("Creation d'une matiere premiere avec un utilisateur erroné")
    void insererMatierePremierUitilisateurErronne()throws IllegalArgumentException {
        assertThrows(IllegalArgumentException.class, () -> {
            MatierePremiere matierePremiere = matierePremiereDAO.create(
                    new MatierePremiere("Poisson",-1, MatierePremiere.UNITE.SIMPLE_UNITE)
            );
        });
    }

}
