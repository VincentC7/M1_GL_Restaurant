package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.BDD_Connexion;
import fr.ul.miage.groupe7.projetrestaurant.Database.MatierePremiere;
import fr.ul.miage.groupe7.projetrestaurant.Database.MatierePremiereDAO;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class MatierePremierDAO_Test {


    static MatierePremiereDAO matierePremiereDAO;

    @BeforeAll
    static void beforeAll(){
        BDD_Connexion.setTest();
        matierePremiereDAO = new MatierePremiereDAO();
        HashSet<MatierePremiere> matierePremieres = matierePremiereDAO.findAll();
        assertEquals(0,matierePremieres.size());
        MatierePremiere matierePremiere = new MatierePremiere("Salade",new BigDecimal( 2), MatierePremiere.UNITE.SIMPLE_UNITE);
        matierePremiere.set_id(new ObjectId("608d568601f9ee6a1eec44c7"));
        matierePremiereDAO.create(matierePremiere);
        matierePremiere = new MatierePremiere("Supprimer",new BigDecimal( 2), MatierePremiere.UNITE.SIMPLE_UNITE);
        matierePremiereDAO.create(matierePremiere);
        matierePremiere = new MatierePremiere("Modifier",new BigDecimal( 2), MatierePremiere.UNITE.SIMPLE_UNITE);
        matierePremiereDAO.create(matierePremiere);
    }

    @Test
    @DisplayName("Chercher une matiere premiere avec un ID")
    void findMatierePremierById(){
        ObjectId id = new ObjectId("608d568601f9ee6a1eec44c7");
        MatierePremiere matierePremiere = matierePremiereDAO.find(id);
        assertEquals("Salade", matierePremiere.getNom());
        assertEquals(new BigDecimal(2), matierePremiere.getQuantitee());
        assertEquals(MatierePremiere.UNITE.SIMPLE_UNITE, matierePremiere.getUnite());
    }

    @Test
    @DisplayName("Chercher une matiere premiere avec un ID inexistant")
    void findMatierePremierByIdInconnu(){
        ObjectId id = new ObjectId("111111111111111111111111");
        MatierePremiere matierePremiere = matierePremiereDAO.find(id);
        assertNull(matierePremiere);
    }

    @Test
    @DisplayName("Chercher une matiere premiere avec un nom")
    void findMatierePremierimpleByNom(){
        MatierePremiere matierePremiere = matierePremiereDAO.findByName("Salade");
        assertEquals("Salade", matierePremiere.getNom());
    }

    @Test
    @DisplayName("Chercher une matiere premiere avec un nom inexistant")
    void findMatierePremierimpleByNomInexistant(){
        MatierePremiere matierePremiere = matierePremiereDAO.findByName("lolilol");
        assertNull(matierePremiere);
    }

    @Test
    @DisplayName("Chercher tous les éléments de la collection (devrait en avoir 3 {Salade, Modfier, Supprimer} pour les tests")
    void findAllMatierePremiere(){
        HashSet<MatierePremiere> matierePremiere = matierePremiereDAO.findAll();
        assertEquals(3,matierePremiere.size());
    }

    @Test
    @DisplayName("Creation d'une matiere premiere que ne devrait pas lever d'erreurs")
    void insererMatierePremiereSimple() {
        MatierePremiere matierePremiere = matierePremiereDAO.create(
                new MatierePremiere("Poisson",new BigDecimal( 2.00), MatierePremiere.UNITE.SIMPLE_UNITE)
        );
        assertNotNull(matierePremiere);
        assertEquals("Poisson", matierePremiere.getNom());
        assertEquals(new BigDecimal(2), matierePremiere.getQuantitee());
        assertEquals(MatierePremiere.UNITE.SIMPLE_UNITE, matierePremiere.getUnite());
    }

    @Test
    @DisplayName("Creation d'une matiere premiere avec des param faux")
    void insererMatierePremiereErronne()throws IllegalArgumentException {
        assertThrows(IllegalArgumentException.class, () -> {
            MatierePremiere matierePremiere = matierePremiereDAO.create(
                    new MatierePremiere("Poisson",new BigDecimal( -1.00), MatierePremiere.UNITE.SIMPLE_UNITE)
            );
        });
    }

    @Test
    @DisplayName("Creation d'une matiere premiere avec un nom déjà existant")
    void insererMatierePremiereNomDejaExistant() {
        MatierePremiere matierePremiere = matierePremiereDAO.create(
                new MatierePremiere("Poisson",new BigDecimal( 2), MatierePremiere.UNITE.SIMPLE_UNITE)
        );
        assertNull(matierePremiere);
    }

    @Test
    @DisplayName("Récupérer une unité à partir d'une chaine existante")
    void getUniteExistant() {
        MatierePremiere.UNITE unite = MatierePremiere.UNITE.getInstance("Kg");
        assertEquals(MatierePremiere.UNITE.KILOGRAMME, unite);
    }

    @Test
    @DisplayName("Récupérer une unité à partir d'une chaine non existante")
    void getUniteNonExistant(){
        assertThrows(IllegalArgumentException.class, () -> {
            MatierePremiere.UNITE unite = MatierePremiere.UNITE.getInstance("blablabla");
        });
    }

    @Test
    @DisplayName("Supprimer une matiere premiere existante")
    void supprimerMatierePremiereSimple(){
        MatierePremiere matierePremiere = matierePremiereDAO.findByName("Supprimer");
        boolean res = matierePremiereDAO.delete(matierePremiere);
        assertTrue(res);
    }

    @Test
    @DisplayName("Supprimer une matiere premiere non existante")
    void supprimerMatierePremiereNonExistante(){
        boolean res = matierePremiereDAO.delete(null);
        assertFalse(res);
    }

    @Test
    @DisplayName("Mettre à jour une matiere premiere existante")
    void updateMatierePremiereSimple(){
        MatierePremiere matierePremiere = matierePremiereDAO.findByName("Modifier");
        assertEquals(new BigDecimal(2), matierePremiere.getQuantitee());
        matierePremiere.setQuantitee(new BigDecimal(10));
        matierePremiereDAO.update(matierePremiere);
        //Verification de l'élément en base
        matierePremiere = matierePremiereDAO.findByName("Modifier");
        assertEquals(new BigDecimal(10), matierePremiere.getQuantitee());
    }

    @Test
    @DisplayName("Mettre à jour une matiere premiere avec une valeur invalide (negative)")
    void updateMatierePremiereValeurNegative(){
        assertThrows(IllegalArgumentException.class, () -> {
            MatierePremiere matierePremiere = matierePremiereDAO.find(new ObjectId("608d568601f9ee6a1eec44c7"));
            matierePremiere.ajouter(new BigDecimal(-10));
            matierePremiereDAO.update(matierePremiere);
        });

    }

    @Test
    @DisplayName("Mettre à jour une matiere premiere existante")
    void updateMatierePremiereNull(){
        assertNull(matierePremiereDAO.update(null));
    }

    @AfterAll
    static void afeterAll(){
        MatierePremiere matierePremiere = matierePremiereDAO.find(new ObjectId("608d568601f9ee6a1eec44c7"));
        matierePremiereDAO.delete(matierePremiere);
        matierePremiere = matierePremiereDAO.findByName("Poisson");
        matierePremiereDAO.delete(matierePremiere);
        matierePremiere = matierePremiereDAO.findByName("Modifier");
        matierePremiereDAO.delete(matierePremiere);
    }
}
