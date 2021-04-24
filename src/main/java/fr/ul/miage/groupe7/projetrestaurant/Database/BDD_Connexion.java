package fr.ul.miage.groupe7.projetrestaurant.Database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class BDD_Connexion {

    private static BDD_Connexion bdd_connexion;
    private static MongoDatabase database;

    private BDD_Connexion(){
        MongoClient mongoClient = MongoClients.create();
        database = mongoClient.getDatabase("restaurant");
    }

    public static MongoDatabase getInstance(){
        if (bdd_connexion == null){
            bdd_connexion = new BDD_Connexion();
        }
        return database;
    }
}
