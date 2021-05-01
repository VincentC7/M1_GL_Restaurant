package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.types.ObjectId;

public class MatierePremiereDAO extends DAO<MatierePremiere> {

    public MatierePremiereDAO() {
        super("MatierePremiere");
    }

    @Override
    public MatierePremiere find(ObjectId id) {
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
