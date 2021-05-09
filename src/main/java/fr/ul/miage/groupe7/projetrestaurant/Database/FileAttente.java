package fr.ul.miage.groupe7.projetrestaurant.Database;

import org.bson.types.ObjectId;

import java.util.LinkedList;

public class FileAttente {

    private LinkedList<CommandesPlats> file;

    public FileAttente() {
        this.file = new LinkedList<>();
    }

    public LinkedList<CommandesPlats> getCommandes(){
        file = new LinkedList<>();
        CommandesDAO commandesDAO = new CommandesDAO();
        commandesDAO.findCommandesPlatsById(CommandesPlats.ETAT_PLAT.COMMANDE).forEach((e) -> {
            if(isEnfant(e.getIdPlat())){
                file.add(indexEnfant(), e);
            }else{
                file.addLast(e);
            }
        });

        return file;
    }

    public CommandesPlats traiterCommande(){
        CommandesDAO commandesDAO = new CommandesDAO();
        this.getCommandes();
        CommandesPlats commandesPlats = file.getFirst();
        Commandes commandes = commandesDAO.findCommandsFromCommand(commandesPlats.get_id());
        commandes.change_etat_commande(commandesPlats.get_id());
        commandes = commandesDAO.update(commandes);

        return commandes.getCommandePlat(commandesPlats.get_id());
    }

    public String afficherCommandes(){
        var sb = new StringBuilder();
        var format = "%-21s: %s%n";
        PlatsDAO platsDAO = new PlatsDAO();
        for (CommandesPlats c: file) {
            sb.append(platsDAO.find(c.getIdPlat()).toStringCommande());
            sb.append(String.format(format,"Numéro dans la liste", file.indexOf(c)));
        }
        return sb.toString();
    }

    private boolean isEnfant(ObjectId idPlat){
        PlatsDAO platsDAO = new PlatsDAO();
        return platsDAO.find(idPlat).isEnfant();
    }

    private int indexEnfant(){
        int index = 0;
        if (file.isEmpty()){
            return index;
        }else{
            for (CommandesPlats p: file) {
                if(isEnfant(p.getIdPlat())){
                    index++;
                }else{
                    break;
                }
            }
        }
        return index;
    }

}
