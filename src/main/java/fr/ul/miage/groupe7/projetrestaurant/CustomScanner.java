package fr.ul.miage.groupe7.projetrestaurant;

import java.math.BigDecimal;
import java.util.Scanner;


public class CustomScanner {

    private Scanner scanner;

    public CustomScanner(){
        scanner = new Scanner(System.in);
    }

    public int get_int(){
        String user_action;
        while (true) {
            user_action = scanner.nextLine();
            while (!isInt(user_action)){
                System.out.println("Veuillez fournir le chiffre de l'action");
                user_action = scanner.nextLine();
            }
            return Integer.parseInt(user_action);
        }
    }

    public BigDecimal get_float(){
        String user_action;
        BigDecimal value;
        while (true) {
            user_action = scanner.nextLine();
            while (!isDouble(user_action)){
                System.out.println("Veuillez donner donner un prix correct");
                user_action = scanner.nextLine();
            }
            try {
                value = new BigDecimal(user_action);
                break;
            }catch (NumberFormatException e){
                System.out.println("Valeur invalide, entrez un chiffre correct s'il vous plait (1.5 par exemple)");
            }
        }
        return value;
    }

    public String get_simple(){
        return scanner.nextLine();
    }


    public static boolean isDouble(String s){
        return s.matches("[-+]?[0-9]*\\.?[0-9]+");
    }

    public static boolean isInt(String s) {
        boolean match = s.matches("-?\\d+");
        if (match) {
            if (s.length() > 10 || s.compareTo("2147483647") > 1) return false;
        }
        return match;
    }

}
