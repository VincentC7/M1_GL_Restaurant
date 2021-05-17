package fr.ul.miage.groupe7.projetrestaurant;

import fr.ul.miage.groupe7.projetrestaurant.Database.Utilisateurs;

import java.util.HashSet;

public class Action {

    private String action;
    private int numAction;
    private Utilisateurs.ROLE[] droits;

    public Action(int numAction, String action, Utilisateurs.ROLE[] droits) {
        this.action = action;
        this.numAction = numAction;
        this.droits = droits;
    }

    public boolean peutFaire(Utilisateurs.ROLE role){
        if (droits.length == 0 || role == Utilisateurs.ROLE.DIRECTEUR) return true;
        for (Utilisateurs.ROLE droit : droits) {
            if (droit == role) return true;
        }
        return false;
    }

    public String getAction() {
        return action;
    }

    public int getNumAction() { return numAction; }

    public Utilisateurs.ROLE[] getDroits() {
        return droits;
    }

}
