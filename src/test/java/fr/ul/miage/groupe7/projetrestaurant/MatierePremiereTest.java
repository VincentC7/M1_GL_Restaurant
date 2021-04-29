package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.MatierePremiere;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatierePremiereTest {

    @Test
    @DisplayName("Creation d'une matiere premiere sans erreur")
    void matierePremiereSimpleTest(){
        MatierePremiere mp = new MatierePremiere("Carotte", 0, MatierePremiere.UNITE.KILOGRAMME);
        assertEquals("Carotte",mp.getNom());
        assertEquals(0,mp.getQuantitee());
        assertEquals(MatierePremiere.UNITE.KILOGRAMME,mp.getUnite());
    }

    @Test
    @DisplayName("Creation d'une matiere premiere sans nom")
    void matierePremierNomNull() throws IllegalArgumentException {
        assertThrows(IllegalArgumentException.class, () -> {
            MatierePremiere mp = new MatierePremiere(null, 0, MatierePremiere.UNITE.KILOGRAMME);
        });
    }


    @Test
    @DisplayName("Creation d'une matiere premiere avec une quantitée négative")
    void matierePremierQuentiteeNegative() throws IllegalArgumentException {
        assertThrows(IllegalArgumentException.class, () -> {
            MatierePremiere mp = new MatierePremiere("Carotte", -1, MatierePremiere.UNITE.KILOGRAMME);
        });
    }

    @Test
    @DisplayName("Creation d'une matiere premiere avec une quantitée trop grande")
    void matierePremierQuentiteeTropGrande()throws IllegalArgumentException {
        assertThrows(IllegalArgumentException.class, () -> {
            MatierePremiere mp = new MatierePremiere("Carotte", Integer.MAX_VALUE + 1, MatierePremiere.UNITE.KILOGRAMME);
        });
    }

    @Test
    @DisplayName("Creation d'une matiere premiere avec une unitée nulle")
    void matierePremierQuentiteeUniteNull()throws IllegalArgumentException {
        assertThrows(IllegalArgumentException.class, () -> {
            MatierePremiere mp = new MatierePremiere("Carotte", 0, null);
        });
    }

}