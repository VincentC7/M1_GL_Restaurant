package fr.ul.miage.groupe7.projetrestaurant;


import fr.ul.miage.groupe7.projetrestaurant.Database.*;
import fr.ul.miage.groupe7.projetrestaurant.service.GeneralProperties;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
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
    }

    private static Action[] actions =
            {
                    new Action(0, "Se déconnecter"                          , new Utilisateurs.ROLE[0]),
                    new Action(1, "Afficher l'état des tables"              , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.SERVEUR, Utilisateurs.ROLE.ASSISTANT_SERVICE, Utilisateurs.ROLE.MAITRE_HOTEL}),
                    new Action(2, "Ajouter une matiere premiere au stock"   , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(3, "Modifier une matiere premiere du stock"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(4, "Visualiser le stock"                     , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.CUISINIER}),
                    new Action(5, "Modifie le serveur de la table"          , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.MAITRE_HOTEL}),
                    new Action(6, "Ajouter un plat"                         , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.CUISINIER}),
                    new Action(7, "Réserver une table"                      , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.MAITRE_HOTEL}),
                    new Action(8, "Débarrasser une table"                   , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.ASSISTANT_SERVICE}),
                    new Action(9, "Sélectionner une table"                  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.SERVEUR, Utilisateurs.ROLE.ASSISTANT_SERVICE, Utilisateurs.ROLE.MAITRE_HOTEL}),
                    new Action(10, "Visualiser les commandes à cuisiner"    , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.CUISINIER}),
                    new Action(11, "Placer un client qui a réservé "        , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.MAITRE_HOTEL}),
                    new Action(12, "Statistique sur le benefice de chaque plat"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(13, "Statistique sur le temps de preparation de chaque plat"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(14, "Statistique sur le temps de preparation total"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(15, "Placer un client"                       , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.MAITRE_HOTEL}),
                    new Action(16, "Recette moyenne des déjeuners"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(17, "Recette moyenne des dîners"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(18, "Recette moyenne quotidienne"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(19, "Recette moyenne hebdomadaire"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(20, "Recette moyenne mensuelle"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(21, "Créer un utilisateur"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(22, "Afficher la carte"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
                    new Action(23, "Editer la facture"               , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.MAITRE_HOTEL}),
                    new Action(24, "Statistiques sur la popularité des plats"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.DIRECTEUR}),
            };

    //0 = Pas de commande 1 = Commandes
    private static Action[][] actionsTables =
            {
                    {
                            new Action(0, "Revenir au menu superieur"       , new Utilisateurs.ROLE[0]),
                            new Action(1, "Créer une commande"              , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.SERVEUR}),
                            new Action(2, "Affichage informations"          , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.SERVEUR}),
                    },
                    {
                            new Action(0, "Revenir au menu superieur"       , new Utilisateurs.ROLE[0]),
                            new Action(1, "Ajouter un plat à une commande"  , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.SERVEUR}),
                            new Action(2, "Affichage informations"          , new Utilisateurs.ROLE[]{Utilisateurs.ROLE.SERVEUR}),
                            new Action(3, "Affichage des commandes à servir", new Utilisateurs.ROLE[]{Utilisateurs.ROLE.SERVEUR}),
                    }
            };


    public void afficher_actions(){
        System.out.println("Que voulez vous faire ? (tapez le numero de l'action)");
        int i=0;
        for(Action action : actions){
            if (action.peutFaire(utilisateur.getRole())){
                System.out.println("\t"+(i++)+" : "+action.getAction());
            }
        }
    }

    private int get_id_action_tables(int user_action,int array_level){
        int id_action = -1;
        for (Action action : actionsTables[array_level]) {
            if (action.peutFaire(utilisateur.getRole())) {
                id_action++;
                if (id_action == user_action) {
                    return action.getNumAction();
                }
            }
        }
        return -1;
    }

    /**
     * Affiche les actions sur une table
     * @param arrayLevel correspond à la profondeur du tableau actionsTable
     */
    public void afficher_actions_table(int arrayLevel){
        if(arrayLevel >= actionsTables[arrayLevel].length)
            return;
        System.out.println("Que voulez vous faire ? (tapez le numero de l'action)");
        int i=0;
        for(Action action : actionsTables[arrayLevel]){
            if (action.peutFaire(utilisateur.getRole())){
                System.out.println("\t"+(i++)+" : "+action.getAction());
            }
        }
    }

    private int get_id_action(int user_action){
        int id_action = -1;
        for (Action action : actions) {
            if (action.peutFaire(utilisateur.getRole())) {
                id_action++;
                if (id_action == user_action) {
                    return action.getNumAction();
                }
            }
        }
        return -1;
    }

    public void effectuer_action(int user_action) {
        switch (get_id_action(user_action)){
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
            case 10:
                visualiser_commandes_cuisinier();
                break;
            case 11:
                placer_reservation();
                break;
            case 12:
                stat_benef_par_plats();
                break;
            case 13:
                stat_temps_de_preparation_par_plats();
                break;
            case 14:
                stat_temps_de_preparation();
                break;
            case 15:
                placer_client();
                break;
            case 16:
                stat_recette_dejeuner();
                break;
            case 17:
                stat_recette_diner();
                break;
            case 18:
                stat_recette_quotidienne();
                break;
            case 19:
                stat_recette_hebdmadaire();
                break;
            case 20:
                stat_recette_mensuelle();
                break;
            case 21:
                ceer_utilisateur();
                break;
            case 22:
                afficher_carte();
                break;
            case 23:
                genere_facture();
            case 24:
                stat_pop_plats();
                break;
            default:
                break;
        }
    }

    public void effectuer_action_table_creation_commandes(int user_action,Table t) {
        switch (get_id_action_tables(user_action,0)){
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
        switch (get_id_action_tables(user_action,1)){
            case 1:
                ajouter_plat_commandes(t);
                break;
            case 2:
                affiche_infos_table(t);
                break;
            case 3:
                visualiser_commandes_serveur(t);
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
        tables = tables.stream().filter((t) -> t.getEtat().equals(Table.ETAT.OCCUPEE)).collect(Collectors.toList());
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
        System.out.println(t.toStringServeur());
        Commandes cmd = commandesDAO.findByTable(t.getNumero());
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
                    if(t.getEtat().equals(Table.ETAT.PROPRE)) {
                        LocalDateTime ldt = LocalDateTime.now();
                        Reservation.CRENEAU c = (ldt.getHour() > 16) ? Reservation.CRENEAU.SOIR : Reservation.CRENEAU.MATIN;
                        if (t.isReserved(ldt.toLocalDate(), c)) {
                            t.setEtat(Table.ETAT.RESERVEE);
                        }
                    }
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
            HashMap<ObjectId,BigDecimal> hm = new HashMap<>();
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
                    hm = (HashMap<ObjectId, BigDecimal>) select_matiere_premiere(hm,ingredients);
            }while (!ingredients.equals("T"));
            ArrayList<String> categs = new ArrayList<>();
            String categ;
            do{
                System.out.println("\tDe quelle catégorie fait partie votre plat ? Appuyez sur T pour terminer cette étape");
                categ = scanner.get_simple();
                if(!categ.equals("T"))
                    categs.add(categ);
            }while (!categ.equals("T"));
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
            plats = new Plats(nom,hm,prix,categs,enfant.equals("y"));
            plats = platsDAO.create(plats);
            if (plats == null)
                System.out.println("\tLes paramètres que vous avez fourni sont faux, recommencez.");
            else
                System.out.println("Votre plats {"+ Main.RETOUR_LIGNE +plats+"} a bien été créé");
        }while (plats == null);
    }

    public Map<ObjectId,BigDecimal> select_matiere_premiere(Map<ObjectId,BigDecimal> hm, String nom){
        MatierePremiere mp = matierePremiereDAO.findByName(nom);
        if(mp == null){
            System.out.println("Ce n'est pas une matière première");
            return hm;
        }
        BigDecimal unite;
        do {
            System.out.println("Combien en avez vous besoin pour la recette");
            unite = scanner.get_float();
        }while (unite.doubleValue() < 0);
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
        List<Table> tables = tablesDAO.findByEtat(Table.ETAT.SALE);
        if (tables.isEmpty()){
            System.out.println("Il n'y a pas de table à débarrasser");
            return;
        }
        int num_table;
        do {
            for(int index = 0; index < tables.size(); index ++) {
                System.out.println((index + 1) + " : " + tables.get(index).toString());
            }
            System.out.println("Entrez le numéro de la table que vous voulez débarrasser");
            num_table = scanner.get_int();
        }while ((num_table) <= 0 || (num_table) > tables.size());

        Table t = tables.get((num_table-1));
        t.setEtat(Table.ETAT.PROPRE);
        System.out.println("La table numéro " + t.getNumero()+ " à bien été débarrassée");
        tablesDAO.update(t);
    }



    /**
     * Ajoute un plat à une commande
     * @param t table de la commande
     */
    public void ajouter_plat_commandes(Table t){
        int action, action2;
        Commandes cmd = commandesDAO.findByTable(t.getNumero());
        HashMap<String, ArrayList<Plats>> plats = Plats.trierPlatsByCat((ArrayList<Plats>) platsDAO.findByMenuAndDisponibility());
        Set<String> set = plats.keySet();
        AtomicReference<String> cat = new AtomicReference<>();
        do {
            AtomicInteger ai = new AtomicInteger(1);
            set.forEach((k) -> {
                cat.set(k);
                System.out.println(ai.getAndIncrement() + " : "+ k);
            } );
            System.out.println("Selectionner une catégorie ou 0 pour revenir en arriere");
            action = scanner.get_int();
            if(action == 0)
                return;
        }while (action > set.size() || action <= 0);

        do {
            AtomicInteger ai2 = new AtomicInteger(1);
            Plats.trierAlpha(plats.get(cat.get()));
            plats.get(cat.get()).forEach((e) -> System.out.println(ai2.getAndIncrement() + Main.RETOUR_LIGNE + e.toString()) );
            System.out.println("Selectionner un plat ou 0 pour revenir en arriere");
            action2 = scanner.get_int();
            if(action2 == 0)
                return;
        }while (action2 > plats.size() || action2 <= 0);

        cmd.addCommandes(new CommandesPlats(plats.get(cat.get()).get(action2-1).get_id()));
        commandesDAO.update(cmd);
    }

    /**
     * Afficher la carte du jour
     */
    public void afficher_carte(){
        HashMap<String, ArrayList<Plats>> plats = Plats.trierPlatsByCat((ArrayList<Plats>) platsDAO.findByMenuAndDisponibility());
        if(!plats.isEmpty()){
            Set<String> cat = plats.keySet();
            for (String c: cat){
                System.out.println(c);
                plats.get(c).forEach((e) -> System.out.println(e.toString()) );
            }
        }else{
            System.out.println("Il n'y a pas de plats dans la carte du jour");
        }
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
    public void genere_facture(){
        List<Table> tables = tablesDAO.findByEtat(Table.ETAT.OCCUPEE);
        if (tables.isEmpty()){
            System.out.println("Il n'y a pas de table à facturer");
            return;
        }
        int num_table;
        do {
            for(int index = 0; index < tables.size(); index ++) {
                System.out.println((index + 1) + " : Serveur " + tables.get(index).getServeur().getIdentifiant() + ", numéro de table "+ tables.get(index).getNumero());
            }
            System.out.println("Entrez le numéro de la table que vous voulez facturer");
            num_table = scanner.get_int();
        }while ((num_table) <= 0 || (num_table) > tables.size());

        Table t = tables.get(num_table-1);
        Commandes cmd = commandesDAO.findByTable(t.getNumero());
        cmd.finir();
        if(cmd.isEtat()){
            t.setEtat(Table.ETAT.SALE);
            TableDAO tableDAO = new TableDAO();
            tableDAO.update(t);
        }
        String facture = cmd.genererFacture();
        commandesDAO.update(cmd);
        System.out.println(facture);
    }

    public void visualiser_commandes_cuisinier(){
        FileAttente fileAttente_cuisine = new FileAttente();
        LinkedList<CommandesPlats> file = fileAttente_cuisine.getCommandes();

        if(file.isEmpty()){
            System.out.println("Vous n'avez pas de plat à préparer");
        }else{
            do {
                System.out.println(fileAttente_cuisine.afficherCommandes());
                System.out.println("Voulez-vous préparer le plat suivant ? (y/n)");

                String act = scanner.get_simple();
                if(act.equals("y")){
                    CommandesPlats plat = fileAttente_cuisine.traiterCommande();
                    String res;
                    do {
                        System.out.println("Tapez fini lorsque vous avez réalisé votre plat");
                        res = scanner.get_simple();
                    }while (!res.equals("fini"));
                    Commandes commandes = commandesDAO.findCommandsFromCommand(plat.get_id());
                    commandes.change_etat_commande(plat.get_id());
                    commandesDAO.update(commandes);
                    System.out.println("Le plat est pret à etre servi");
                }else{
                    break;
                }

            }while (!fileAttente_cuisine.getCommandes().isEmpty());
        }
    }

    public void setUtilisateur(Utilisateurs utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void visualiser_commandes_serveur(Table t){
        int num = t.getNumero();
        Commandes c = commandesDAO.findByTable(num);
        ArrayList<CommandesPlats> file = c.getCommandesPlats().stream()
                .filter(p -> p.getEtat().equals(CommandesPlats.ETAT_PLAT.PRET)).collect(Collectors
                        .toCollection(ArrayList::new));
        if(file.isEmpty()){
            System.out.println("Vous n'avez pas de plat à servir");
        }else{
            do {
                System.out.println(file.get(0));
                System.out.println("Voulez-vous servir le plat suivant ? (y/n)");

                String act = scanner.get_simple();
                if(act.equals("y")){
                    c.change_etat_commande(file.get(0).get_id());
                    commandesDAO.update(c);
                    System.out.println("Le plat est pret à etre servi");
                    file.remove(0);
                }else{ break; }
            }while (!file.isEmpty());
        }
    }

    public void placer_reservation(){
        var tables = tablesDAO.findByEtat(Table.ETAT.RESERVEE);
        if(tables.isEmpty()){
            System.out.println("Il n'y a pas de table réservée");
            return;
        }
        LocalDateTime ldt = LocalDateTime.now();
        Reservation.CRENEAU c = (ldt.getHour() > 16) ? Reservation.CRENEAU.SOIR : Reservation.CRENEAU.MATIN;
        int index;
        do {
            var i = new AtomicInteger(1);
            tables.stream()
                    .forEach(t -> System.out.println(i.getAndIncrement() + " " + t.getReservation(ldt.toLocalDate(), c).getNom()));
            System.out.println("Selectionner le numéro de la réservation");
            index = scanner.get_int();
        }while ((index-1) < 0 || (index-1) >= tables.size());
        Table t = tables.get((index-1));
        t.setEtat(Table.ETAT.OCCUPEE);
        t.deleteReservation(ldt.toLocalDate(),c);
        System.out.println("La table est " + t.getNumero());
        tablesDAO.update(t);
    }

    public void placer_client(){
        var tables = tablesDAO.findByEtat(Table.ETAT.PROPRE);
        if(tables.isEmpty()) {
            System.out.println("Il n'y a plus de tables disponibles");
            return;
        }

        int num_table;
        do {
            for(int index = 0; index < tables.size(); index ++) {
                System.out.println((index + 1) + " : " + tables.get(index).toString());
            }
            System.out.println("Selectionnez une table par son numéro");
            num_table = scanner.get_int();
        }while ((num_table) <= 0 || (num_table) > tables.size());
        Table t = tables.get((num_table-1));
        t.setEtat(Table.ETAT.OCCUPEE);
        System.out.println("La table numéro " + t.getNumero()+ " à bien été attribuée");
        tablesDAO.update(t);
    }

    public void ceer_utilisateur(){
        String nom;
        do{
            System.out.println("Entrez le nom du nouvel utilisateur (2 caractères au minimum");
            nom = scanner.get_simple();
        }while (nom.length() < 2);

        String prenom;
        do{
            System.out.println("Entrez son prénon (2 caractères au minimum");
            prenom = scanner.get_simple();
        }while (prenom.length() < 2);

        Utilisateurs.ROLE[] roles = Utilisateurs.ROLE.values();
        int num_role;
        do{
            for(int i = 0; i < roles.length; i++){
                System.out.println((i +1) +" : "+ roles[i]);
            }
            System.out.println("Selectionnez un rôle par son numéro");
                    num_role = scanner.get_int();
        }while ((num_role) <= 0 || (num_role) > roles.length);

        Utilisateurs utilisateurs = new Utilisateurs(nom, prenom, roles[num_role-1], null, null);
        utilisateurs = utilisateursDAO.create(utilisateurs);
        System.out.println("Identidiant de l'utilisateur : "+utilisateurs.getIdentifiant());
        System.out.println("Mot de passe de l'utilisateur : "+utilisateurs.getMdp());
    }

    public void stat_temps_de_preparation(){
        System.out.println("Le temps de préparation moyen est " + commandesDAO.getPreparationTime() +" min");
    }

    public void stat_temps_de_preparation_par_plats(){
        for(var c : commandesDAO.getPreparationTimeByPlats().entrySet()){
            System.out.println("Le temps de préparation de "+ c.getKey() + " est de " + c.getValue() +" min");
        }
    }

    public void stat_benef_par_plats(){
        for(var c : commandesDAO.getPlatsBenef().entrySet()){
            System.out.println("Le benéfice de "+ c.getKey() + " est de " + c.getValue() +" €");
        }
    }

    public void stat_recette_dejeuner(){
        ArrayList<Commandes> cmds = new ArrayList<>();
        cmds.addAll(commandesDAO.findAllCommand());
        System.out.println("La recette moyenne des déjeuners est de " + Commandes.recetteRepas(cmds, true)+" €");
    }

    public void stat_recette_diner(){
        ArrayList<Commandes> cmds = new ArrayList<>();
        cmds.addAll(commandesDAO.findAllCommand());
        System.out.println("La recette moyenne des dîners est de " + Commandes.recetteRepas(cmds, false)+" €");
    }

    public void stat_recette_quotidienne(){
        ArrayList<Commandes> cmds = new ArrayList<>();
        cmds.addAll(commandesDAO.findAllCommand());
        System.out.println("La recette moyenne quotidienne est de " + Commandes.recetteQuotidienne(cmds)+" €");
    }

    public void stat_recette_hebdmadaire(){
        ArrayList<Commandes> cmds = new ArrayList<>();
        cmds.addAll(commandesDAO.findAllCommand());
        System.out.println("La recette moyenne des hebdomadaire est de " + Commandes.recetteHebdomadaire(cmds)+" €");
    }

    public void stat_recette_mensuelle(){
        ArrayList<Commandes> cmds = new ArrayList<>();
        cmds.addAll(commandesDAO.findAllCommand());
        System.out.println("La recette moyenne des mensuelle est de " + Commandes.recetteMensuelle(cmds)+" €");
    }

    private void stat_pop_plats() {
        System.out.println(Plats.statistiquesPlats());
    }

}
