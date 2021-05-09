package fr.ul.miage.groupe7.projetrestaurant;


import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import fr.ul.miage.groupe7.projetrestaurant.service.GeneralProperties;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


public class Restaurant {

    private final PlatsDAO platsDAO;
    private final TableDAO tablesDAO;
    private final UtilisateursDAO utilisateursDAO;
    private final MatierePremiereDAO matierePremiereDAO;
    private final CommandesDAO commandesDAO;
    private final CustomScanner scanner;
    private Utilisateurs utilisateur;

    public Restaurant(){
        scanner = new CustomScanner();
        platsDAO = new PlatsDAO();
        tablesDAO = new TableDAO();
        commandesDAO = new CommandesDAO();
        utilisateursDAO = new UtilisateursDAO();
        matierePremiereDAO = new MatierePremiereDAO();
        creer_tables();

        utilisateur = utilisateursDAO.find("Tluc", "azerty");
        if(utilisateur == null){
            utilisateur = utilisateursDAO.create( new Utilisateurs("Luc","Tristan", Utilisateurs.ROLE.SERVEUR,"azerty",null));
        }
    }

    private static String[] actions =
            {
                    "0 : Quitter l'application",
                    "1 : Afficher l'état des tables",
                    "2 : Ajouter une matiere premiere au stock",
                    "3 : Modifier une matiere premiere du stock",
                    "4 : Visualiser le stock",
                    "5 : Modifie le serveur de la table",
                    "6 : Ajouter un plat",
                    "7 : Réserver une table",
                    "8 : Débarrasser une table",
                    "9 : Sélectionner une table"
            };

    //0 = Pas de commande 1 = Commandes
    private static String[][] actionsTables =
            {
                    {
                            "0 : Revenir au menu superieur",
                            "1 : Créer une commande",
                            "2 : Affichage informations"
                    },
                    {
                            "0 : Revenir au menu superieur",
                            "1 : Ajouter un plat à une commande",
                            "2 : Affichage informations",
                            "3 : Editer la facture"
                    }
            };


    public void afficher_actions(){
        System.out.println("Que voulez vous faire ? (tapez le numero de l'action)");
        for (String action : actions){
            System.out.println("\t"+action);
        }
    }

    /**
     * Affiche les actions sur une table
     * @param arrayLevel correspond à la profondeur du tableau actionsTable
     */
    public void afficher_actions_table(int arrayLevel){
        if(arrayLevel >= actionsTables[arrayLevel].length)
            return;
        System.out.println("Que voulez vous faire ? (tapez le numero de l'action)");
        for (String action : actionsTables[arrayLevel]){
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
                modification_Serveur_Table();
                break;
            case 6:
                ajouter_plat();
                break;
            case 7:
                reserver_une_table();
                break;
            case 8:
                debarrasser_une_table();
                break;
            case 9:
                action_tables();
                break;
            default:
                break;
        }
    }

    public void effectuer_action_table_creation_commandes(int user_action,Table t) {
        switch (user_action){
            case 1:
                creer_commandes(t);
                break;
            case 2:
                affiche_infos_table(t);
                break;
            default:
                break;
        }
    }

    public void effectuer_action_table_ajout_commande(int user_action,Table t) {
        switch (user_action){
            case 1:
                ajouter_plat_commandes(t);
                break;
            case 2:
                affiche_infos_table(t);
                break;
            case 3:
                genere_facture(t);
                break;
            default:
                break;
        }
    }

    /**
     * Permet de sélectionner la table et de lancer des actions dessus
     */
    public void action_tables(){
        List<Table> tables = tablesDAO.findByServeur(utilisateur);
        if(tables.isEmpty()){
            System.out.println("Vous n'avez pas de tables");
            return;
        }
        int action;
        while (true) {
            do {
                System.out.println(afficher_numero_tables(tables));
                System.out.println("Selectionner une table (0 por revenir en arriere)");
                action = scanner.get_int();
                if(action == 0)
                    return;
            } while (!table_existe(action, tables));
            Table t = tablesDAO.findByNum(action);
            action_commandes(t);
        }

    }

    /**
     * selection des actions sur une table
     * @param t table sur laquelle on réalise nos actions
     */
    public void action_commandes(Table t){
        Commandes c = commandesDAO.findByTable(t.getNumero());
        // si il y a une commande a cette table
        int action;
        if(c != null){
            do {
                afficher_actions_table(1);
                action = scanner.get_int();
                if (action != 0) effectuer_action_table_ajout_commande(action,t);
            } while (action !=0);
        }
        else{
            do {
                afficher_actions_table(0);
                action = scanner.get_int();
                if (action == 0)
                    return;

                effectuer_action_table_creation_commandes(action,t);
            } while (action !=1);
            action_commandes(t);
        }
    }

    public void affiche_infos_table(Table t){
        Commandes cmd = commandesDAO.findByTable(t.getNumero());
        System.out.println(t.toStringServeur());
        cmd.getCommandesPlats().forEach(System.out::println);
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
        var etage = new AtomicInteger(1);
        var format = "%nEtage %d : %s ";
        var format2 = "%s ";
        tables.forEach(t -> res.append(
                (t.getEtage() == etage.get()) ? String.format(format ,etage.getAndIncrement(), t)
                        : String.format(format2,t)
        ));
        return res.toString();
    }

    /**
     * Affiche l'état de toutes les tables par étages
     *
     * @param tables Tables dans le restaurant
     * @return
     */
    public String afficher_numero_tables(List<Table> tables){
        StringBuilder res = new StringBuilder();
        Collections.sort(tables);
        tables.forEach(t -> res.append("La table "
        ).append(t.getNumero()).append(" est disponible pour cela").append(Main.RETOUR_LIGNE));
        return res.toString();
    }

    /**
     * Affiche les tables ne possédant pas de serveur
     * @param tablesVide liste de table vide
     * @return
     */
    public String afficher_table_sans_serveur(List<Table> tablesVide){
        var sb = new StringBuilder();

        tablesVide.forEach(t -> sb.append("La table ")
                .append(t.getNumero())
                .append(String.format("%s %n"," n'a pas de serveur")));
        return sb.toString();
    }

    /**
     * renvoie true si la table existe
     * @return
     */
    public boolean table_existe(int numero,List<Table> tables){
        return tables.stream().anyMatch(t -> t.getNumero() == numero);
    }

    /**
     * Methode qui effectue toutes las actions correspondantes à l'ajout d'un serveur à une table
     * @return
     */
    public void modification_Serveur_Table(){
        int user_action;
        List<Table> tablesVide = tablesDAO.findByServeurNull();
        do {
            System.out.println(afficher_table_sans_serveur(tablesVide));
            System.out.println("Sélectionner une table");
            user_action = scanner.get_int();
        } while (!table_existe(user_action,tablesVide));
        var t = tablesDAO.findByNum(user_action);
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
        MatierePremiere matierePremiere;
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
        HashSet<MatierePremiere> matierePremieres = matierePremiereDAO.findAll();
        System.out.println("Pour quelle matiere premiere voulez vous ajouter du sock ? (tapez 0 pour quitter à tout moment)");
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

    //Ajouter un PLAT
    private void ajouter_plat(){
        System.out.println("\tVous allez à présent créer un nouveau plat (tapez 0 pour quitter à tout moment)");
        Plats plats;
        do {
            String nom,enfant;
            HashMap<ObjectId,Integer> hm = new HashMap<>();
            BigDecimal prix;
            do{
                System.out.println("\tQuel nom voulez vous donner à votre plats ?");
                nom = scanner.get_simple();
            }while (nom.equals(""));
            if (nom.equals("0"))break;
            String ingredients;
            do{
                afficher_stock();
                System.out.println("\tQuel ingrédients avez vous besoins ? Appuyez sur T pour terminer cette étape");
                ingredients = scanner.get_simple();
                if(!ingredients.equals("T"))
                    hm = (HashMap<ObjectId, Integer>) select_matiere_premiere(hm,ingredients);
            }while (!ingredients.equals("T"));
            do{
                System.out.println("\tQuel est le prix");
                prix = scanner.get_float();
            }while (prix.doubleValue() < 0);
            do{
                System.out.println("\tEst ce un plat pour enfant y or n");
                enfant = scanner.get_simple().toLowerCase(Locale.ROOT);
            }while ( !enfant.equals("y") && !enfant.equals("n") );
            /*
            Ici pour les catégories plus tard
             */
            plats = new Plats(nom,hm,prix,null,enfant.equals("y"));
            plats = platsDAO.create(plats);
            if (plats == null)
                System.out.println("\tLes paramètres que vous avez fourni sont faux, recommencez.");
            else
                System.out.println("Votre plats {"+ Main.RETOUR_LIGNE +plats+"} a bien été créé");
        }while (plats == null);
    }

    public Map<ObjectId,Integer> select_matiere_premiere(Map<ObjectId,Integer> hm, String nom){
        MatierePremiere mp = matierePremiereDAO.findByName(nom);
        if(mp == null){
            System.out.println("Ce n'est pas une matière première");
            return hm;
        }
        int unite;
        do {
            System.out.println("Combien en avez vous besoin pour la recette");
            unite = scanner.get_int();
        }while (unite < 0);
        hm.put(mp.get_id(),unite);
        return hm;
    }
    //Ajouter un PLAT


    //Réserver une table
    private void reserver_une_table(){

        String nom,creneau;
        int year,month,day;
        LocalDate date;
        do{
            System.out.println("\tQuel est le nom de la personne qui reserve ?");
            nom = scanner.get_simple();
        }while (nom.equals(""));
        do{
            System.out.println("\tIndiquer le creneau (Matin/Soir)");
            creneau = scanner.get_simple();
            creneau = creneau.substring(0, 1).toUpperCase() + creneau.substring(1);
        }while ( !creneau.equals("Soir") && !creneau.equals("Matin") );
        LocalDateTime ldt;
        do{
            System.out.println("\tIndiquer l'année");
            year = scanner.get_int();
            System.out.println("\tIndiquer le mois");
            month = scanner.get_int();
            System.out.println("\tIndiquer le jour");
            day = scanner.get_int();
            date = LocalDate.of(year,month,day);
            ldt = (creneau.equals("Matin")) ? date.atTime(12,0) : date.atTime(19,0);
            System.out.println(LocalDateTime.now().isBefore(ldt));
        }while (LocalDateTime.now().isAfter(ldt)) ;
        int user_action;
        List<Table> tablesVide =tablesDAO.findAll();
        tablesVide = tables_sans_reservation(tablesVide,date,creneau);
        do {
            System.out.println("Les tables disponibles pour la date");
            tablesVide.forEach(t -> System.out.println("\t" + t.getNumero()));
            System.out.println("Sélectionner une table");
            user_action = scanner.get_int();
        } while (!table_existe(user_action,tablesVide));
        Table t = tablesDAO.findByNum(user_action);
        t.addReservation(new Reservation(creneau,nom,date));
        tablesDAO.update(t);
    }

    /**
     * Renvoie une liste de table qui n'ont pas de résevration
     * pour une date précise
     * @param tables liste de table
     * @param ld date de la réservation
     * @param creneau créneau de la réservation (Matin/Soir)
     * @return une liste filtré
     */
    private List<Table> tables_sans_reservation(List<Table> tables,LocalDate ld,String creneau){
        return tables.stream()
                .filter(t -> !(t.getReservations().contains(new Reservation(creneau,"nom", ld))))
                .collect(Collectors.toList());
    }

    /**
     * Permet de débarrasser une table (Passer de l'état sale à second_service)
     */
    private void debarrasser_une_table() {
        Table t1 = tablesDAO.findByNum(1);
        t1.setServeur(utilisateur);
        t1.setEtat(Table.ETAT.OCUPEE);
        t1.setEtat(Table.ETAT.SALE);
        tablesDAO.update(t1);
        Table t2 = tablesDAO.findByNum(2);
        t2.setEtat(Table.ETAT.OCUPEE);
        t2.setEtat(Table.ETAT.SALE);
        t2.setServeur(utilisateur);
        tablesDAO.update(t2);

        List<Table> tables = tablesDAO.findByServeur(utilisateur);
        tables.forEach((e) -> {
            // on affiche que les tables sales que le serveur peut débarrasser
            if(e.getEtat().equals(Table.ETAT.SALE)){
                System.out.print(e.toStringServeur());
            }
        });

        int num_table;
        do{
            System.out.println("Entrez le numéro de la table que vous voulez débarrasser");
            num_table = scanner.get_int();
        }while (num_table < 1 || num_table > tables.size());

        Table tmp = null;
        for(Table t: tables){
            if(t.getNumero() == num_table){
                tmp = t;
            }
        }
        tmp.setEtat(Table.ETAT.SECOND_SERVICE);
        tablesDAO.update(tmp);
        System.out.println("Table débarrassée, à drésser pour un second service !");

        // on remet les tables par défaut
        t1.setEtat(null);
        tablesDAO.update(t1);
        t2.setEtat(null);
        tablesDAO.update(t2);
    }



    /**
     * Ajoute un plat à une commande
     * @param t table de la commande
     */
    public void ajouter_plat_commandes(Table t){
        int action;
        Commandes cmd = commandesDAO.findByTable(t.getNumero());
        List<Plats> plats = platsDAO.findByMenu();
        do {
            AtomicInteger ai = new AtomicInteger(1);
            plats.forEach(p -> System.out.println(ai.getAndIncrement() + Main.RETOUR_LIGNE + p.toString()) );
            System.out.println("Selectionner un plat ou 0 pour revenir en arriere");
            action = scanner.get_int();
            if(action == 0)
                return;
        }while (action > plats.size() || action <= 0);
        cmd.addCommandes(new CommandesPlats(plats.get(action-1).get_id()));
        commandesDAO.update(cmd);
    }

    /**
     * Crée une nouvelle commande
     * @param t table de la commande
     */
    public void creer_commandes(Table t){
        commandesDAO.create(new Commandes(t.getNumero()));
    }

    /**
     * Génere la facture d'une table
     * @param t table de la commande
     */
    public void genere_facture(Table t){
        Commandes cmd = commandesDAO.findByTable(t.getNumero());
        cmd.change_etat_commande(0);
        cmd.change_etat_commande(0);
        cmd.change_etat_commande(1);
        cmd.change_etat_commande(1);
        cmd.finir();
        String facture = cmd.genererFacture();
        System.out.println(facture);
    }

}
