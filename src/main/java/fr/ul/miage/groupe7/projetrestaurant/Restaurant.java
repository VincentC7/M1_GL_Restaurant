package fr.ul.miage.groupe7.projetrestaurant;


public class Restaurant {

    private static String[] actions =
            {
                    "0 : Quitter l'application",
            };


    public void afficher_actions(){
        System.out.println("Que voulez vous faire ? (tapez le numero de l'action");
        for (String action : actions){
            System.out.println("\t"+action);
        }
    }

    public void effectuer_action(int user_action) {
        switch (user_action){
            default:
                break;
        }
    }
}
