package fr.ul.miage.groupe7.projetrestaurant;


import fr.ul.miage.groupe7.projetrestaurant.Database.Table;
import fr.ul.miage.groupe7.projetrestaurant.service.GeneralProperties;

public class Restaurant {

    private static String[] actions =
            {
                    "0 : Quitter l'application",
                    "1 : Afficher l'état des tables",
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
}
