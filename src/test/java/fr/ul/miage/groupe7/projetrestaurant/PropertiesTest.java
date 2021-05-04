package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.service.GeneralProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertiesTest {


    @Test
    @DisplayName("Récuparation de la bonne valeur")
    public void getValue(){
        assertEquals("restaurant", GeneralProperties.getPropertie("DATABASE_NAME"));
    }

    @Test
    @DisplayName("Exception quand le fichier n'existe pas")
    public void loadFailed(){
        assertThrows(Exception.class, () -> {
            GeneralProperties.getPropertie("DATABASE_NAME", "config");
        });
    }

    @Test
    @DisplayName("Retroune null quand la clé n'existe pas")
    public void missing_key(){
        assertNull(GeneralProperties.getPropertie("bidon"));
    }
}
