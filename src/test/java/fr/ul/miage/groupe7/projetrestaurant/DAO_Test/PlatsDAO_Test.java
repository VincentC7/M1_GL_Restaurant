package fr.ul.miage.groupe7.projetrestaurant.DAO_Test;

import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PlatsDAO_Test {

    static Map<ObjectId,BigDecimal> hm;
    static List<String> categories;
    static Plats p,ps;
    static PlatsDAO dao;
    static MatierePremiereDAO matierePremiereDAO;
    static MatierePremiere mp,mp1,mp2,mp3;

    @BeforeAll
    static void init(){
        BDD_Connexion.setTest();
        matierePremiereDAO = new MatierePremiereDAO();
        mp1 = matierePremiereDAO.create(new MatierePremiere("mp1",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
        mp2 = matierePremiereDAO.create(new MatierePremiere("mp2",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
        mp3 = matierePremiereDAO.create(new MatierePremiere("mp3",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
        hm = new HashMap<>();
        hm.put(mp1.get_id(),new BigDecimal(50));
        hm.put(mp2.get_id(),new BigDecimal(100));
        hm.put(mp3.get_id(),new BigDecimal(150));
        categories = new ArrayList<>();
        categories.add("categories1");
        categories.add("categories2");
        p = new Plats("PlatTest",hm,new BigDecimal("15.50"),categories,false);
        dao = new PlatsDAO();
        p = dao.create(p);


    }

    @Nested
    @DisplayName("Test sur le find")
    class FIND {

        @Test
        @DisplayName("Trouver un Plat par son nom")
        void TrouverPlatSuccesParNom(){
            var plats = dao.find("PlatTest");
            Plats plat = plats.get(0);
            assertEquals("PlatTest",plat.getNom());
            assertEquals(3, plat.getMatieres_premieres().keySet().size());
            assertEquals(new BigDecimal(150),plat.getMatieres_premieres().get(mp3.get_id()));
            assertEquals(0,plat.getPrix().compareTo(new BigDecimal("15.50")));
            assertEquals(2,plat.getCategories().size());
            assertTrue(plat.getCategories().contains("categories1"));
            assertFalse(plat.isEnfant());

        }

        @Test
        @DisplayName("Trouver un Plat par son _id")
        void TrouverPlatSuccesParID(){
            var plat = dao.find(p.get_id());
            assertEquals("PlatTest",plat.getNom());
            assertEquals(3, plat.getMatieres_premieres().keySet().size());
            assertEquals(new BigDecimal(150),plat.getMatieres_premieres().get(mp3.get_id()));
            assertEquals(0,plat.getPrix().compareTo(new BigDecimal("15.50")));
            assertEquals(2,plat.getCategories().size());
            assertTrue(plat.getCategories().contains("categories1"));
            assertFalse(plat.isEnfant());

        }

        @Test
        @DisplayName("Ne pas Trouver de plat par nom")
        void TrouverPlatFailParNom(){
            var plats = dao.find("platdeTest");
            assertTrue(plats.isEmpty());

        }

        @Test
        @DisplayName("Ne pas Trouver de plat par _id")
        void TrouverPlatFailParId(){
            var plat = dao.find(new ObjectId());
            assertNull(plat);

        }

        @Test
        @DisplayName("Ne pas Trouver de plat par _id")
        void TrouverPlatSuccesByMenu(){
            var plats = dao.findByMenu();
            assertEquals(1,plats.size());

        }

        @Test
        @DisplayName("Trouver les plats par disponibilité avec un plat pas disponible")
        void TrouverPlatSuccesByMenuAndDisponibilityFalse(){
            var plats = dao.findByMenuAndDisponibility();
            mp = matierePremiereDAO.create(new MatierePremiere("mp",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
            HashMap<ObjectId,BigDecimal> hm2 = new HashMap<>();
            hm2.put(mp.get_id(),new BigDecimal(1000));
            ps =  dao.create(new Plats("PlatTest",hm2,new BigDecimal("15.50"),null,false));

            assertEquals(1,plats.size());

        }

        @Test
        @DisplayName("Trouver les plats par disponibilité avec tous les plats disponible")
        void TrouverPlatSuccesByMenuAndDisponibilityTrue(){
            mp = matierePremiereDAO.create(new MatierePremiere("mp",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
            HashMap<ObjectId,BigDecimal> hm2 = new HashMap<>();
            hm2.put(mp.get_id(),new BigDecimal(50));
            ps =  dao.create(new Plats("PlatTest",hm2,new BigDecimal("15.50"),null,false));
            var plats = dao.findByMenuAndDisponibility();
            assertEquals(2,plats.size());

        }

        @Test
        @DisplayName("Baisser la quantité de matiere premiere")
        void MatierePremiereDiminue(){
            mp = matierePremiereDAO.create(new MatierePremiere("mp",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
            HashMap<ObjectId,BigDecimal> hm2 = new HashMap<>();
            hm2.put(mp.get_id(),new BigDecimal(50));
            ps =  dao.create(new Plats("PlatTest",hm2,new BigDecimal("15.50"),null,false));
            assertTrue(matierePremiereDAO.updateWithPlat(ps));
            assertEquals(0,matierePremiereDAO.find(mp.get_id()).getQuantitee().compareTo(new BigDecimal(450)));

        }

        @Test
        @DisplayName("Baisser la quantité de matiere premiere jusqu'a indisponibilité")
        void MatierePremiereDiminueSousZero(){
            mp = matierePremiereDAO.create(new MatierePremiere("mp",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
            HashMap<ObjectId,BigDecimal> hm2 = new HashMap<>();
            hm2.put(mp.get_id(),new BigDecimal(1000));
            ps =  dao.create(new Plats("PlatTest",hm2,new BigDecimal("15.50"),null,false));
            assertFalse(matierePremiereDAO.updateWithPlat(ps));
            assertEquals(0,matierePremiereDAO.find(mp.get_id()).getQuantitee().compareTo(new BigDecimal(500)));

        }

        @Test
        @DisplayName("Test la dispnobilité d'un plat disponible")
        void MatierePremiereIsDisponibleTrue(){
            mp = matierePremiereDAO.create(new MatierePremiere("mp",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
            HashMap<ObjectId,BigDecimal> hm2 = new HashMap<>();
            hm2.put(mp.get_id(),new BigDecimal(50));
            ps =  dao.create(new Plats("PlatTest",hm2,new BigDecimal("15.50"),null,false));
            assertTrue(matierePremiereDAO.isDisponible(ps));

        }

        @Test
        @DisplayName("Test la dispnobilité d'un plat indsiponible")
        void MatierePremiereIsDisponibleFalse(){
            mp = matierePremiereDAO.create(new MatierePremiere("mp",new BigDecimal(500), MatierePremiere.UNITE.GRAMME));
            HashMap<ObjectId,BigDecimal> hm2 = new HashMap<>();
            hm2.put(mp.get_id(),new BigDecimal(1000));
            ps =  dao.create(new Plats("PlatTest",hm2,new BigDecimal("15.50"),null,false));
            assertFalse(matierePremiereDAO.isDisponible(ps));

        }
    }

    @Nested
    @DisplayName("Test sur le create")
    class CREATE {

        Plats plat;

        @AfterEach
        void init(){
            dao.delete(plat);
        }

        @Test
        @DisplayName("Creer un Plats avec 3 arguments")
        void CreerUnPlat3Arguments(){
            plat = dao.create(new Plats("PlatTest",hm,new BigDecimal("15.50")));
            assertTrue(plat.getCategories().isEmpty());
            assertFalse(plat.isEnfant());
        }

        @Test
        @DisplayName("Creer un Plats avec 4 arguments")
        void CreerUnPlat4Arguments(){
            plat = dao.create(new Plats("PlatTest",hm,new BigDecimal("15.50"),categories));
            assertFalse(plat.getCategories().isEmpty());
            assertFalse(plat.isEnfant());
        }
    }


    @AfterAll
    static void delete(){
        dao.delete(p);

        matierePremiereDAO.delete(mp1);
        matierePremiereDAO.delete(mp2);
        matierePremiereDAO.delete(mp3);
    }

    @AfterEach
    void afterEach(){

        matierePremiereDAO.delete(mp);
        dao.delete(ps);
    }

}
