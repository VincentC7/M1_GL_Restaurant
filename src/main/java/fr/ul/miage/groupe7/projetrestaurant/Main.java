package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;
import fr.ul.miage.groupe7.projetrestaurant.Database.UtilisateursDAO;

public class Main {

    public static final String RETOUR_LIGNE = System.lineSeparator();

    public static void main(String[] args) {
        Restaurant restaurant = new Restaurant();
        CustomScanner scanner = new CustomScanner();
        UtilisateursDAO utilisateursDAO = new UtilisateursDAO();
        int user_action;
        System.out.println("Bienvenue !"+RETOUR_LIGNE);
        String login, passwd;
        do {
            System.out.println("Connectez vous pour accéder à l'application (tapez quit pour quitter l'application)");
            System.out.println("Quel est votre login ?");
            login = scanner.get_simple();
            if (login.equals("quit"))break;
            System.out.println("Quel est votre mot de passe ?");
            passwd = scanner.get_simple();
            if (passwd.equals("quit"))break;
            Utilisateurs utilisateur = utilisateursDAO.find(login,passwd);
            if (utilisateur != null){
                restaurant.setUtilisateur(utilisateur);
                do {
                    restaurant.afficher_actions();
                    user_action = scanner.get_int();
                    if (user_action != 0) restaurant.effectuer_action(user_action);
                } while (user_action !=0);
                System.out.println(RETOUR_LIGNE+RETOUR_LIGNE);
            }else{
                System.err.println("identifiant et mot de passe invalide, recommencez.");
            }
        } while (true);


        System.out.println("A bientot !");
    }
}
