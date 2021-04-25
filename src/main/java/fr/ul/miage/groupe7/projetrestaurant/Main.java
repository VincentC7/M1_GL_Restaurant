package fr.ul.miage.groupe7.projetrestaurant;

public class Main {

    public static final String RETOUR_LIGNE = System.lineSeparator();

    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant();
        CustomScanner scanner = new CustomScanner();
        int user_action;
        System.out.println("Bienvenue !"+RETOUR_LIGNE);
        do {
            restaurant.afficher_actions();
            user_action = scanner.get_int();
            if (user_action != 0) restaurant.effectuer_action(user_action);
        } while (user_action !=0);

        System.out.println("A bientot !");
    }

}
