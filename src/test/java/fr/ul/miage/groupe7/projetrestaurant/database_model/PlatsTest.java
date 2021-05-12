package fr.ul.miage.groupe7.projetrestaurant.database_model;

import fr.ul.miage.groupe7.projetrestaurant.Database.BDD_Connexion;
import fr.ul.miage.groupe7.projetrestaurant.Database.Plats;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PlatsTest {

    static Map<ObjectId,BigDecimal> hm;
    static List<String> categories;
    static String hex1,hex2,hex3;

    @BeforeAll
    static void init(){
        BDD_Connexion.setTest();
        hm = new HashMap<>();
        hex1 = "a".repeat(24);
        hex2 = "b".repeat(24);
        hex3 ="c".repeat(24);
        hm.put(new ObjectId(hex1),new BigDecimal(50));
        hm.put(new ObjectId(hex2),new BigDecimal(100));
        hm.put(new ObjectId(hex3),new BigDecimal(150));
        categories = new ArrayList<>();
        categories.add("categories1");
        categories.add("categories2");
    }

    @Nested
    @DisplayName("Test sur le constructeur")
    class CONSTRUCTOR {

        @Test
        @DisplayName("Créer un Plat")
        void CreerPlatSucces() throws IllegalArgumentException  {

            Plats p = new Plats("PlatTest",hm,new BigDecimal("15.50"),categories,true);
            assertEquals("PlatTest",p.getNom());
            assertEquals(3, p.getMatieres_premieres().keySet().size());
            assertEquals(new BigDecimal(150),p.getMatieres_premieres().get(new ObjectId(hex3)));
            assertEquals(0,p.getPrix().compareTo(new BigDecimal("15.50")));
            assertEquals(2,p.getCategories().size());
            assertTrue(p.getCategories().contains("categories1"));
            assertTrue(p.isEnfant());
        }

        @Test
        @DisplayName("Créer un Plat avec 4 arguments")
        void CreerPlatConstructeur4Arguments() throws IllegalArgumentException  {
            Plats p = new Plats("PlatTest",hm,new BigDecimal("15.50"),categories);
            assertFalse(p.isEnfant());
        }

        @Test
        @DisplayName("Créer un Plat avec 3 arguments")
        void CreerPlatConstructeur3Arguments() throws IllegalArgumentException  {

            Plats p = new Plats("PlatTest",hm,new BigDecimal("15.50"));
            assertTrue(p.getCategories().isEmpty());
            assertFalse(p.isEnfant());
        }

        @Test
        @DisplayName("Nom trop petit exception")
        void CreerPlatFailNomTropPetit() throws IllegalArgumentException  {
            assertThrows(IllegalArgumentException.class,() -> {
                Plats p = new Plats("P",hm,new BigDecimal("15.50"),categories,false);
            });
        }

        @Test
        @DisplayName("Nom assez grand")
        void CreerPlatSuccesNomAssezGrand() throws IllegalArgumentException  {
            assertDoesNotThrow(() -> {
                Plats p = new Plats("To",hm,new BigDecimal("15.50"),categories,false);
            });
        }

        @Test
        @DisplayName("Pas assez de matieres premiere")
        void CreerPlatFailTropPeuMatieresPremieres() throws IllegalArgumentException  {
            assertThrows(IllegalArgumentException.class,() -> {
                Plats p = new Plats("PlatTest",new HashMap<>(),new BigDecimal("15.50"),categories,false);
            });
        }

        @Test
        @DisplayName("Assez de matieres premiere")
        void CreerPlatSuccesAssezMatierePremiere() throws IllegalArgumentException  {
            Map<ObjectId,BigDecimal> hm2 = new HashMap<>();
            hm2.put(new ObjectId(hex1),new BigDecimal(50));
            assertDoesNotThrow(() -> {
                Plats p = new Plats("PlatTest",hm2,new BigDecimal("15.50"),categories,false);
            });
        }

        @Test
        @DisplayName("Prix trop petit")
        void CreerPlatFailPrixTropPetit() throws IllegalArgumentException  {
            assertThrows(IllegalArgumentException.class,() -> {
                Plats p = new Plats("PlatTest",hm,new BigDecimal("0"),categories,false);
            });
        }

        @Test
        @DisplayName("Assez de matieres premiere")
        void CreerPlatSuccesPrixSuffisant() throws IllegalArgumentException  {

            assertDoesNotThrow(() -> {
                Plats p = new Plats("PlatTest",hm,new BigDecimal("0.01"),categories,false);
            });
        }
    }


}
