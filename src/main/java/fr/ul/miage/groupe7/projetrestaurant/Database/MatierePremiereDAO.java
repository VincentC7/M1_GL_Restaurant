package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Objects;


public class MatierePremiereDAO extends DAO<MatierePremiere> {

    public MatierePremiereDAO() {
        super("MatierePremiere");
    }

    @Override
    public MatierePremiere find(ObjectId id) {
        return null;
    }

    public MatierePremiere findByName(String nom) {
        return null;
    }

    @Override
    public MatierePremiere create(MatierePremiere obj) {
        return null;
    }

    @Override
    public MatierePremiere update(MatierePremiere obj) {
        return null;
    }

    @Override
    public boolean delete(MatierePremiere obj) {
        return false;
    }

}