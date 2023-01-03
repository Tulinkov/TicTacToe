package com.example.tictactoe;

import java.util.Random;
import java.util.Vector;

/*  Fichier Jeu.java

	Auteur :    Yakiv Tulinkov, matricule 20231597
	Cours :     IFT 1135-A-A22
	Trimestre : Automne 2022
	Travail :   TP1
			 
	But du programme :
		Ce programme permet de :
		- gérer un jeu Tic-Tac-Toe;
		- de générer les coups pour le joueur « O » qui joue en deuxième.
								
	Dernière mise à jour : 11/10/2022
*/

public class Jeu implements TicTacToe {	
	//tableau des coups : 0 - libre; -1 - X; 1 - O
	private int[] grille = new int[9];
	//vecteur des états des 8 combinaisons gagnantes
	private Vector<Integer> combGagnantes = new Vector<Integer>(8);	
	//numéro du coup courant
	private int nCoup;
	//position du dernier coup
	private int dernCoup;
	
	//constructeur
	public Jeu() {
		initialise();
	}
	
	/**
	* Méthode « initialise » de type void
	* Description: initialise un jeu de tic-tac-toe
	*/
	public void initialise() {
		for (int i = 0; i < grille.length; i++)
			grille[i] = 0;
		nCoup = 0;
		combGagnantes.removeAllElements();
		for (int i = 0; i < 8; i++)
			combGagnantes.add(0);
	}

	/**
	* Méthode « setX » de type void
	* Description: reçoit de l'interface graphique la position de « X » choisi par le joueur
	* @param cellule - position de « X » selon la grille de l'interface
	*/
	public void setX(int cellule) {
		nCoup++;
		dernCoup = cellule;
		grille[dernCoup] = -1;
		verifierComb();
	}

	/**
	* Méthode « getO » de type return <int>
	* Description: détermine le prochain coup de « O »
	* @return <int> - l'index de la position à jouer selon la grille de l'interface
	*/
	public int getO() {
		int posO = -1;
		switch (nCoup) {
			case 1: //1er coup
				posO = (grille[8] == 0) ? 8 : new Random().nextInt(4) * 2; //centre, s'il n'est pas occupé, sinon un des coins
				break;
			case 2: //2ème coup
				if ((posO = trouverPretGagner("X")) == -1) {
					if (dernCoup % 2 == 0) { //X occupe 2 positions sur une diagonale et O la troisième
						if (grille[8] == -1 && grille[(dernCoup + 4) % 8] == 1)
							posO = cellLibre(1);
						else if (grille[8] == 1 && grille[(dernCoup + 4) % 8] == -1)
							posO = cellLibre(2);
					}
					if (grille[(dernCoup + 3) % 8] == -1 || //X occupe un coin et le centre d'une des lignes/colonnes sur le périmètre, sens des aiguilles par rapport au dernier coup ou
						(dernCoup % 2 == 1 && grille[(dernCoup + 2) % 8] == -1)) //X occupe les centres de deux lignes/colonnes sur le périmètre, sens des aiguilles par rapport au dernier coup
						posO = (dernCoup + 2 - dernCoup % 2) % 8;
					else if (grille[(dernCoup + 5) % 8] == -1 || //X occupe un coin et le centre d'une des lignes/colonnes sur le périmètre, sens inverse des aiguilles par rapport au dernier coup ou
						(dernCoup % 2 == 1 && grille[(dernCoup + 6) % 8] == -1)) //X occupe les centres de deux lignes/colonnes sur le périmètre, sens inverse des aiguilles par rapport au dernier coup
						posO = (dernCoup + 6 + dernCoup % 2) % 8;
					else if (posO == -1)
						posO = cellLibre(2);
				}
				break;
			default: //3ème et 4ème coups
				if ((posO = trouverPretGagner("O")) == -1) //à gagner O
					if ((posO = trouverPretGagner("X")) == -1) //à empêcher à gagner X
						if ((posO = trouverSansX()) == -1) //recherche de la meilleure position
							posO = cellLibre(0);
		}
		grille[posO] = 1;
		verifierComb();
		return posO;
	}

	/**
	* Méthode « gagnant » de type return <boolean>
	* Description: détermine si le joueur, soit « X » ou « O », est gagnant
	* @param joueur - « X » ou « O » représentant le joueur
	* @param pos - tableau des 3 index de la combinaison gagnante
	* @return <boolean> - true s'il y a une combinaison gagnante pour le « joueur », sinon false
	*/
	public boolean gagnant(String joueur, int[] pos ) {
		int val = (joueur == "X") ? -3 : 3;
		int posGagn = combGagnantes.indexOf(val);
		int[] p = cellulesComb(posGagn);
		for (int i = 0; i < 3; i++)
			pos[i] = p[i];
		return (posGagn == -1) ? false : true;
	}
	
	/**
	* Méthode « verifierComb » de type void
	* Description: vérifie toutes les 8 combinaisons en les attribuant les valeurs suivantes :
	* -1, 1 - ne contient qu'un X ou O,
	* -2, 2 - contient deux X ou deux O,
	* -3, 3 - contient trois X ou trois O,
	* -10, 10 - contient deux X et un O, ou vice versa
	*/
	private void verifierComb() {
		for (int i = 0; i < 8; i++)
			combGagnantes.set(i, summComb(i));
	}
	
	/**
	* Méthode « summComb » de type return <int>
	* Description: compte la valeur de la combinaison spécifiée
	* @param ncomb - numéro de la combinaison
	* @return <int> - valeur de la combinaison
	*/
	private int summComb(int ncomb) {
		int[] cells = cellulesComb(ncomb);
		int res = grille[cells[0]] + grille[cells[1]] + grille[cells[2]];
		if (Math.abs(res) == 1 && grille[cells[0]] != 0 && grille[cells[1]] != 0 && grille[cells[2]] != 0) //si toutes les positions de la combinaison sont occupées et leur valeur n'est pas la même
			res *= 10;
		return res;
	}
	
	/**
	* Méthode « cellulesComb » de type return <int[]>
	* Description: compte les index des cellules correspondant à une victoire, il existe 8 combinaisons
	* @param ncomb - numéro de la combinaison
	* @return <int[]> - positions des cellules de la combinaison
	*/
	private int[] cellulesComb(int nComb) {
		return (nComb < 4) ?
				new int[] {nComb, 8, nComb + 4} : //[0 - 3] - combinaisons comprenant la cellule centrale
				new int[] {(nComb - 4) * 2, (nComb - 4) * 2 + 1, ((nComb - 4) * 2 + 2) % 8}; //[4 - 7] - combinaisons sur le périmètre de la grille
	}
	
	/**
	* Méthode « trouverPretGagner » de type return <int>
	* Description: cherche le coup suivant menant à la victoire
	* @param joueur - « X » ou « O » représentant le joueur
	* @return <int> - position de la cellule où il faut jouer pour gagner, ou -1 si elle n'est pas trouvée 
	*/
	private int trouverPretGagner(String joueur) {
		int comb = combGagnantes.indexOf(2 * (joueur == "X" ? -1 : 1));
		if (comb != -1) {
			int[] pos = cellulesComb(comb);
			for (int i = 0; i < 3; i++) //recherche de la cellule non rempli
				if (grille[pos[i]] == 0)
					return pos[i];
		}		
		return -1;
	}
	
	/**
	* Méthode « cellLibre » de type return <int>
	* Description: cherche les cellules non occupées
	* @param pos - paramètre de la recherche : 0 - toutes excepté le centre, 1 - au coin, 2 - au centre d'une des côtés
	* @return <int> - position de la cellule libre 
	*/
	private int cellLibre (int pos) {
		int debut = (pos == 2) ? 1 : 0,
			pas = (pos == 0) ? 1 : 2;
		Vector<Integer> cellLibres = new Vector<Integer>(4);
		for (int i = debut; i < 9; i += pas)
			if (grille[i] == 0)
				cellLibres.add(i);
		return cellLibres.get(new Random().nextInt(cellLibres.size()));
	}
	
	/**
	* Méthode « trouverSansX » de type return <int>
	* Description: cherche une cellules appartenant simultanément à deux ou à une combinaison contenant seulement un O
	* @return <int> - position de la cellule trouvée, ou -1 si elle n'est pas trouvée 
	*/
	private int trouverSansX() {
		Vector<Integer> pos1 = new Vector<Integer>(), //cellules appartenant à 1 combinaison trouvée
						pos2 = new Vector<Integer>(); //cellules appartenant à 2 combinaisons trouvées
		for (int i = 0; i < combGagnantes.size(); i++)
			if (combGagnantes.get(i) == 1) { // recherche des combinaisons où il y a un « O », mais pas de « X »
				int[] cells = cellulesComb(i);
				for (int j = 0; j < 3; j++)
					if (grille[cells[j]] == 0)
						if (pos1.indexOf(cells[j]) == -1)
							pos1.add(cells[j]);
						else
							pos2.add(cells[j]);
			}
		if (pos1.size() == 0 && pos2.size() == 0)
			return -1;
		Vector<Integer> pos = (pos2.size() == 0) ? pos1 : pos2;
		return pos.get(new Random().nextInt(pos.size()));
	}

	/**
	* Méthode « isPartieNulle » de type return <boolean>
	* Description: détermine si toutes les positions de la grille sont occupées
	* 			   sans qu'il y ait de gagnant
	* @return <boolean> - true si c'est le cas, sinon false
	*/
	public boolean isPartieNulle() {
		return (combGagnantes.indexOf(3) == -1 && combGagnantes.indexOf(-3) == -1 && nCoup > 4);
	}

	/**
	* Méthode « testDebug » de type void
	* Description: remplit la grille avant commencer le jeu 
	* @param coups - tableau des positions des premiers coups (« X » - éléments paires, « O » - éléments impaires)
	*/
	public void testDebug(int[] coups) {
		for (int i = 0; i < coups.length && coups[i] != -1; i++) {
			if (i % 2 == 1)
				grille[coups[i]] = 1;
			else {
				nCoup++;
				dernCoup = coups[i];
				grille[coups[i]] = -1;
			}
		}
		verifierComb();
	}
}
