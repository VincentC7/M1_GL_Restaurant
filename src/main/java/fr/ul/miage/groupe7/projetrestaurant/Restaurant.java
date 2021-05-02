package fr.ul.miage.groupe7.projetrestaurant;


import fr.ul.miage.groupe7.projetrestaurant.Database.MatierePremiere;
import fr.ul.miage.groupe7.projetrestaurant.Database.MatierePremiereDAO;
import fr.ul.miage.groupe7.projetrestaurant.Database.Table;
import fr.ul.miage.groupe7.projetrestaurant.service.GeneralProperties;

import java.math.BigDecimal;
import java.util.Scanner;

public class Restaurant {

    private static String[] actions =
            {
                    "0 : Quitter l'application",
                    "1 : Afficher l'état des tables",
                    "2 : Ajouter une matiere premiere au stock",
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
                Table[][] tables = creer_tables();
                System.out.println(afficher_tables(tables));
                break;
            case 2:
                ajouter_mp_stock();
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
    public String afficher_tables(Table[][] tables){
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < tables.length; i++){
            res.append("Etage "+ (i+1) + " : ");
            for (int j = 0; j < tables[i].length; j++){
                res.append(tables[i][j].toString()+" ");
            }
            res.append("\n");
        }
        return res.toString();
    }

    /**
     * Création des tables du restaurant pour tous les étages
     *
     * @return
     */
    public Table[][] creer_tables(){
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
                res[i][y] = new Table(i+1, numero_table, Table.ETAT.PROPRE);
                numero_table++;
            }
        }

        return res;
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
}
