package fr.ul.miage.groupe7.projetrestaurant;


import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import fr.ul.miage.groupe7.projetrestaurant.service.GeneralProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Restaurant {

    private PlatsDAO platsDAO;

    private TableDAO tablesDAO;
    private UtilisateursDAO utilisateursDAO;
    private MatierePremiereDAO matierePremiereDAO;
    private CustomScanner scanner;

    public Restaurant(){
        scanner = new CustomScanner();
        platsDAO = new PlatsDAO();
        tablesDAO = new TableDAO();
        utilisateursDAO = new UtilisateursDAO();
        matierePremiereDAO = new MatierePremiereDAO();
        creer_tables();
    }

    private static String[] actions =
            {
                    "0 : Quitter l'application",
                    "1 : Afficher l'état des tables",
                    "2 : Modifie le serveur de la table"
            };


    public void afficher_actions(){
        System.out.println("Que voulez vous faire ? (tapez le numero de l'action)");
        for (String action : actions){
            System.out.println("\t"+action);
        }
    }

    public void effectuer_action(int user_action) {
        switch (user_action){
            case 1:
                List<Table> tables = tablesDAO.findAll();
                System.out.println(afficher_tables(tables));

                break;
            case 2:
                display_Modification_Serveur_Table();
                break;
            default:
                break;
        }
    }

    /**
     * Affiche l'état de toutes les tables par étages
     *
     * @param tables Tables dans le restaurant
     * @return
     */
    public String afficher_tables(List<Table> tables){
        StringBuilder res = new StringBuilder();
        Collections.sort(tables);
        AtomicInteger etage = new AtomicInteger(1);
        String format = "%nEtage %d : %s ";
        String format2 = "%s ";
        tables.forEach(t -> res.append(
                (t.getEtage() == etage.get()) ? String.format(format ,etage.getAndIncrement(), t)
                        : String.format(format2,t)
        ));
        return res.toString();
    }

    /**
     * Affiche les tables sans serveur
     * @return
     */
    public String afficher_table_sans_serveur(List<Table> tablesVide){
        StringBuilder sb = new StringBuilder();

        tablesVide.forEach(t -> sb.append("La table ")
                .append(t.getNumero())
                .append(String.format("%s %n"," n'a pas de serveur")));
        return sb.toString();
    }

    /**
     * renvoie true si la table existe
     * @return
     */
    public boolean table_existe(int numero){
        return tablesDAO.findAll().stream().anyMatch(t -> t.getNumero() == numero );
    }

    /**
     * Methode qui effectue toutes las actions correspondantes à l'ajout d'un serveur à une table
     * @return
     */
    public void display_Modification_Serveur_Table(){
        int user_action;
        List<Table> tablesVide = tablesDAO.findByServeurNull();
        do {
            System.out.println(afficher_table_sans_serveur(tablesVide));
            System.out.println("Sélectionner une table");
            user_action = scanner.get_int();
        } while (!table_existe(user_action));
        Table t = tablesDAO.findByNum(user_action);
        List<Utilisateurs> serveurs = utilisateursDAO.findAllServeur();
        do {
            System.out.println(affiche_des_utilisateurs(serveurs));
            System.out.println("Sélectionner un utilisateur");
            user_action = scanner.get_int();
        } while (user_action < 0 || user_action >= serveurs.size());
        t.setServeur(serveurs.get(user_action));
        System.out.println(tablesDAO.update(t));
    }

    /**
     * Affiche les utilisateurs
     * @return
     */
    public String affiche_des_utilisateurs(List<Utilisateurs> utilisateurs){
        StringBuilder sb = new StringBuilder();
        AtomicInteger nb = new AtomicInteger(0);
        utilisateurs.forEach(u -> sb.append(String.format("%d %n",nb.getAndIncrement()))
                .append(u));
        return sb.toString();
    }




    /**
     * Création des tables du restaurant pour tous les étages
     *
     */
    public void creer_tables(){
        // on récupère le nombre d'étage
        int nb_etages = Integer.parseInt(GeneralProperties.getPropertie("NOMBRE_ETAGE"));
        // on récupère le nombre de table par étage
        int nb_tables = Integer.parseInt(GeneralProperties.getPropertie("NOMBRE_TABLE_PAR_ETAGE"));

        // initialisation du tableau de sortie
        Table[][] res = new Table[nb_etages][nb_tables];

        // on compte le numéro de table (pour éviter d'avoir deux tables avec le même numéro)
        int numero_table = 1;
        for(int i = 0; i < nb_etages; i++){
            for(int y = 0; y < nb_tables; y++){
                Table t = tablesDAO.findByNum(numero_table);
                if(t != null){
                    t.setEtage(i+1);
                    tablesDAO.update(t);
                }
                else{
                    tablesDAO.create(new Table(i+1, numero_table, Table.ETAT.PROPRE));
                }
                numero_table++;
            }
        }
    }
}
