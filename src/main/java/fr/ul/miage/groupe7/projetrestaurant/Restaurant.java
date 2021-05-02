package fr.ul.miage.groupe7.projetrestaurant;


import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import fr.ul.miage.groupe7.projetrestaurant.service.GeneralProperties;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
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
                    "2 : Ajouter une matiere premiere au stock",
                    "3 : Modifier une matiere premiere du stock",
                    "4 : Visualiser le stock",
                    "5 : Modifie le serveur de la table"
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
                ajouter_mp_stock();
                break;
            case 3:
                modifier_mp_stock();
                break;
            case 4:
                afficher_stock();
                break;
            case 5:
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


    // ==================== Stock ====================
    private void ajouter_mp_stock(){
        System.out.println("\tVous allez à présent créer une nouvelle matiere premiere (tapez 0 pour quitter à tout moment)");
        CustomScanner scanner = new CustomScanner();
        MatierePremiere matierePremiere;
        MatierePremiereDAO matierePremiereDAO = new MatierePremiereDAO();
        do {
            System.out.println("\tQuel nom voulez vous donner à votre matiere premiere ?");
            String nom;
            do{
                nom = scanner.get_simple();
                if (nom.equals("")) System.out.println("Veuillez donner un nom");
            }while (nom.equals(""));
            if (nom.equals("0"))break;
            boolean valid;
            int unite;
            do {
                System.out.println("\tQuel est l'unitée de votre matiere premiere ?");
                int i=1;
                for (MatierePremiere.UNITE u : MatierePremiere.UNITE.values()){
                    System.out.println("\t"+(i++)+" : "+u.toString());
                }
                unite = scanner.get_int();
                if (unite == 0)return;
                valid = unite > 0 && unite < i;
            }while (!valid);
            matierePremiere = new MatierePremiere(nom,new BigDecimal(0), MatierePremiere.UNITE.values()[unite-1]);
            matierePremiere = matierePremiereDAO.create(matierePremiere);
            if (matierePremiere == null)
                System.out.println("\tLes paramètres que vous avez fourni sont faux, recommencez.");
            else
                System.out.println("Votre matiere premiere {"+matierePremiere+"} a bien été créé");
        }while (matierePremiere == null);
    }

    private void afficher_stock(){
        MatierePremiereDAO matierePremiereDAO = new MatierePremiereDAO();
        HashSet<MatierePremiere> matierePremieres = matierePremiereDAO.findAll();
        if (matierePremieres.size() != 0){
            System.out.println("Voici le stock :");
            for (MatierePremiere matierePremiere : matierePremieres) {
                System.out.println("\t * "+matierePremiere.getNom()+" ("+matierePremiere.getQuantitee()+" "+matierePremiere.getUnite()+")");
            }
        }else{
            System.out.println("Vous n'avez pas encore de matière première en stock");
        }
    }

    private void modifier_mp_stock() {
        MatierePremiereDAO matierePremiereDAO = new MatierePremiereDAO();
        HashSet<MatierePremiere> matierePremieres = matierePremiereDAO.findAll();
        System.out.println("Pour quelle matiere premiere voulez vous ajouter du sock ? (tapez 0 pour quitter à tout moment)");
        CustomScanner scanner = new CustomScanner();
        boolean valid;
        int choix;
        do {
            System.out.println("\tQuel est l'unitée de votre matiere premiere ?");
            int i=1;
            for (MatierePremiere mp : matierePremieres){
                System.out.println("\t"+(i++)+" : "+mp.getNom()+"("+mp.getQuantitee()+" "+mp.getUnite()+")");
            }
            choix = scanner.get_int();
            if (choix == 0)return; //Quitter
            valid = choix > 0 && choix < i;
        }while (!valid);
        Object[] mps = matierePremieres.toArray();
        MatierePremiere mp_modif = (MatierePremiere) mps[choix-1];
        System.out.println("Combien d'unité voulez vous ajouter à "+mp_modif.getNom()+"(en "+mp_modif.getUnite()+")");
        mp_modif.ajouter(new BigDecimal(String.valueOf(scanner.get_float())));
        matierePremiereDAO.update(mp_modif);
    }
    // ==================== Fin Stock ====================


}
