package fr.ul.miage.groupe7.projetrestaurant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestCustomScanner {

    // ==================== int =======================

    @Test
    @DisplayName("Chaine de caractère qui est un int")
    void verifInt(){
        boolean res = CustomScanner.isInt("12");
        assertTrue(res);
    }

    @Test
    @DisplayName("Chaine de caractère qui n'est pas un double")
    void verifNotTint(){
        boolean res = CustomScanner.isInt("Salut");
        assertFalse(res);
    }

    @Test
    @DisplayName("int str max")
    void verifMaxInt(){
        String max_int = Integer.MAX_VALUE + "";
        boolean res = CustomScanner.isInt(max_int);
        assertTrue(res);
    }

    @Test
    @DisplayName("int str qui dépasse la valeur max de int")
    void verifMoreMaxInt(){
        String bigint_str = (Integer.MAX_VALUE + 1) + "";
        boolean res = CustomScanner.isInt(bigint_str);
        assertFalse(res);
    }

}
