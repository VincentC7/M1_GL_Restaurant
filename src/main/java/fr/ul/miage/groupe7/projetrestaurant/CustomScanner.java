package fr.ul.miage.groupe7.projetrestaurant;

import java.math.BigDecimal;
import java.util.Scanner;


public class CustomScanner {

    private Scanner scanner;

    public CustomScanner(){
        scanner = new Scanner(System.in);
    }

    public int get_int(){
        return 0;
    }

    public BigDecimal get_float(){
        return new BigDecimal(0);
    }


    public static boolean isDouble(String s){
        return true;
    }

    public static boolean isInt(String s) {
        return true;
    }

}
