package fr.ul.miage.groupe7.projetrestaurant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;

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

    // ==================== double =======================

    @Test
    @DisplayName("Chaine de caractère qui est un double")
    void verifDouble(){
        boolean res = CustomScanner.isDouble("12.34");
        assertTrue(res);
    }

    @Test
    @DisplayName("Chaine de caractère non double")
    void verifNotDouble(){
        boolean res = CustomScanner.isDouble("Salut");
        assertFalse(res);
    }


    // ==================== test Scanner =======================

    @Test
    @DisplayName("verifScannerInt")
    void verifScannerInt(){
        ByteArrayInputStream in = new ByteArrayInputStream("10".getBytes());
        System.setIn(in);
        CustomScanner customScanner = new CustomScanner();
        int res = customScanner.get_int();
        assertEquals(10,res);
    }

    @Test
    @DisplayName("verifScannerFloat")
    void verifScannerDouble(){
        ByteArrayInputStream in = new ByteArrayInputStream("1.23".getBytes());
        System.setIn(in);
        CustomScanner customScanner = new CustomScanner();
        BigDecimal actual = customScanner.get_float();
        BigDecimal expected = new BigDecimal("1.23");
        assertEquals(expected,actual);
    }

}
