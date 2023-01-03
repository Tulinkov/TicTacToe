package com.example.tictactoe;

public interface TicTacToe {

   /* pour initialiser un jeu de tic tac toe */
   public void initialise();

   /* pour recevoir l'index de la cellule choisie par X */
   public void setX( int cellule);

   /* Pour transmettre l'index de la position du O jou� */
   public int getO();

   /* vrai ou faux, le joueur pass� en param�tre ("X" ou "O")
    * a gagn�?  Si vrai, les index des 3 cellules de la combinaison
    * gagnante sont dans le tableau pos.
    */
   public boolean gagnant(String joueur, int[] pos );

   /*Toutes les cellules sont occup�es et il n'y a aucun gagnant */
   public boolean isPartieNulle();

   public void testDebug(int[] indicesCoups);
}