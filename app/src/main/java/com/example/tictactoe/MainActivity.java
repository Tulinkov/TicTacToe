package com.example.tictactoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

/*  Fichier Jeu.java

	Auteur :    Yakiv Tulinkov, matricule 20231597
	Cours :     IFT 1135-A-A22
	Trimestre : Automne 2022
	Travail :   TP2

	But du programme :
		Ce programme utilise la classe Jeu développée lors du TP1 et permet à un utilisateur
		de jouer au Tic-Tac-Toe contre son appareil.

	Dernière mise à jour : 30/10/2022
*/

public class MainActivity extends AppCompatActivity {
    //tableau des cases
    private ImageButton[] cases = new ImageButton[9];
    //champ pour indiquer le résultat
    private TextView tv_result;
    //object de jeu
    private TicTacToe jeu;
    //indicateur si le jeu est fini
    private boolean estFinJeu = false;
    //coups dans l'ordre de mise en œuvre
    private int[] coups = new int[9];
    //nobmre de coups effectués
    private int curCoup;

    /**
     * Méthode « onCreate » de type void
     * Description: méthode appelée à la création d'une activité
     * @param savedInstanceState - l'état de l'application sauvegardé par onSaveInstanceState()
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_result = findViewById(R.id.tv_result);
        for (int i = 0; i < 9; i++) {
            int caseID = getResources().getIdentifier("case" + i, "id", getPackageName());
            cases[i] = findViewById(caseID);
        }
        jeu = new Jeu();
        initialize(null);
    }

    /**
     * Méthode « initialize » de type void
     * Description: initialise un jeu de tic-tac-toe
     * @param v - n'est pas utilisée, pour des raisons de compatibilité avec l'événement click
     */
    public void initialize(View v) {
        estFinJeu = false;
        tv_result.setText("");
        for (int i = 0; i < 9; i++) {
            cases[i].setImageResource(R.drawable.empty);
            cases[i].setContentDescription("");
            cases[i].setAlpha(1f);
            coups[i] = -1;
        }
        curCoup = 0;
        jeu.initialise();
    }

    /**
     * Méthode « onCaseClick » de type void
     * Description: événement de clic sur les cases de la grille de Tic-tac-toe
     * @param v - bouton cliqué
     */
    public void onCaseClick(View v) {
        String caseName = getResources().getResourceEntryName(v.getId());
        int caseX = Integer.parseInt(caseName.substring(caseName.length() - 1));
        if (hasElem(coups, caseX) || estFinJeu) //si la case est déjà remplie ou si le jeu est terminé
            return;
        //traitement du click
        remplirCase(caseX, "X");
        jeu.setX(caseX);
        coups[curCoup++] = caseX;
        if (verifierFinJeu("X"))
            return;
        //traitement du coup d'ordinateur
        int caseO = jeu.getO();
        remplirCase(caseO, "O");
        coups[curCoup++] = caseO;
        verifierFinJeu("O");
    }

    /**
     * Méthode « verifierFinJeu » de type return <boolean>
     * Description: si le jeu est terminé, remplir le résultat et prépare les cases
     * @param joueur - « X » ou « O » représentant le joueur
     * @return <boolean> - true si le jeu est terminé (il y a un gagnant ou une partie nulle)
     */
    private boolean verifierFinJeu(String joueur) {
        //partie nulle
        if (jeu.isPartieNulle()) {
            estFinJeu = true;
            tv_result.setText(R.string.gameRes_draw);
            return true;
        }
        //il y a un gagnant
        int[] combGagnant = new int[3];
        if (jeu.gagnant(joueur, combGagnant)) {
            estFinJeu = true;
            tv_result.setText((joueur == "X") ?
                               R.string.gameRes_XWins :
                               R.string.gameRes_OWins);
            for (int i = 0; i < 9; i++)
                if(hasElem(combGagnant, i))
                    cases[i].setImageResource((joueur == "X") ?
                                               R.drawable.icon_x_gagne :
                                               R.drawable.icon_o_gagne);
                else
                    cases[i].setAlpha(0.25f);
            return true;
        }
        //jeu n'est pas terminé
        return false;
    }

    /**
     * Méthode « onSaveInstanceState » de type void
     * Description: récupère des ressources lorsque le système va détruire l’activité
     * @param savedInstanceState - permet de sauvegarder l'état de l'application
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curCoup", curCoup);
        outState.putIntArray("coups", coups);
    }

    /**
     * Méthode « onRestoreInstanceState » de type void
     * Description: méthode appelée à la recréation
     * @param savedInstanceState - l'état de l'application sauvegardé par onSaveInstanceState()
     */
    @Override
    public void onRestoreInstanceState(@Nullable Bundle inState) {
        super.onRestoreInstanceState(inState);
        curCoup = inState.getInt("curCoup");
        coups = inState.getIntArray("coups");
        int i;
        for (i = 0; i < coups.length && coups[i] != -1; i++)
            remplirCase(coups[i], (i % 2 == 0) ? "X" : "O");
        jeu.testDebug(coups);
        verifierFinJeu((i % 2 == 0) ? "0" : "X");
    }

    /**
     * Méthode auxiliaire « hasElem » de type return <boolean>
     * Description: vérifie si un tableau contient un élément
     * @param tabl - tableau de type <int>
     * @param el - élément à trouver
     * @return <boolean> - true si un tableau contient un élément, sinon false
     */
    private boolean hasElem(int[] tabl, int el) {
        for (int i = 0; i < tabl.length; i++)
            if (tabl[i] == el)
                return true;
        return false;
    }

    /**
     * Méthode « remplirCase » de type void
     * Description: visualise un coup effectué
     * @param nb - numéro de la case à remplir
     * @param joueur - « X » ou « O » représentant le joueur qui a fait le coup
     */
    private void remplirCase(int nb, String joueur) {
        cases[nb].setImageResource((joueur == "X") ?
                R.drawable.icon_x :
                R.drawable.icon_o);
    }
}