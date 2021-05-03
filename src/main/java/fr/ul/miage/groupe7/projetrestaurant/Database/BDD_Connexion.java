package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import fr.ul.miage.groupe7.projetrestaurant.service.GeneralProperties;

public class BDD_Connexion {

    private static BDD_Connexion bdd_connexion;
    private static MongoDatabase database;
    private static boolean test = false;
    public final static String DATABASE_NAME="DATABASE_NAME";
    public final static String DATABASE_TEST="DATABASE_TEST";

    private BDD_Connexion(){
        MongoClient mongoClient = MongoClients.create();
        if(!test)
            database = mongoClient.getDatabase(GeneralProperties.getPropertie(DATABASE_NAME));
        else
            database = mongoClient.getDatabase(GeneralProperties.getPropertie(DATABASE_TEST));
    }

    public static MongoDatabase getInstance(){
        if (bdd_connexion == null){
            bdd_connexion = new BDD_Connexion();
        }
        return database;
    }

    public static void setTest(){
        test = true;
    }

    public static void setDev(){
        test = false;
    }

}
