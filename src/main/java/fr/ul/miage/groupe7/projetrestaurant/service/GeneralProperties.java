package fr.ul.miage.groupe7.projetrestaurant.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GeneralProperties {

    private static  Properties prop;

    /**
     * Permet de charger le fichier de paramétrage de l'application
     *
     */
    private static void load(){
        try (InputStream input = new FileInputStream("config.properties")) {

            prop = new Properties();
            prop.load(input);

        } catch (IOException ex) {
            System.err.println("Fichier de configuration introuvable");
        }
    }

    /**
     * Permet de charger le fichier de paramétrage de l'application
     *
     * @param fileName Nom du fichier de configuration à charger
     */
    private static void load(String fileName){
        try (InputStream input = new FileInputStream(fileName)) {
            prop = new Properties();
            prop.load(input);

        } catch (IOException ex) {
            System.err.println("Fichier de configuration introuvable");
        }
    }

    /**
     *  Retourne la valeur qui correspond au nom du paramètre de configuration que l'on passe en paramètre de la fonction
     *
     * @param propertieName Nom du paramètre auquel on souhaite accéder
     * @return
     */
    public static String getPropertie(String propertieName){
        load();
        return prop.getProperty(propertieName);
    }

    /**
     *  Retourne la valeur qui correspond au nom du paramètre de configuration que l'on passe en paramètre de la fonction
     *
     * @param propertieName Nom du paramètre auquel on souhaite accéder
     * @param fileName Nom du fichier de configuration
     * @return
     */
    public static String getPropertie(String propertieName, String fileName){
        load(fileName);
        return prop.getProperty(propertieName);
    }

}
