package xadrez.peca;

import ui.Cor;
import ui.Icone;

import xadrez.tabuleiro.Casa;
import xadrez.tabuleiro.Tabuleiro;
import xadrez.tabuleiro.Simulador;
import xadrez.movimento.Movimento;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Dama extends Peca {
	private ArrayList<Point> direcoes;
	
	public Dama (Cor nova_cor, Point P) {
		super (nova_cor, P);
		
		// inicializa direções
		direcoes = new ArrayList<> ();
		direcoes.add (new Point (1, 1));	// diagonal principal, pra baixo
		direcoes.add (new Point (1, -1));	// diagonal secundária, pra cima
		direcoes.add (new Point (-1, 1));	// diagonal secundária, pra baixo
		direcoes.add (new Point (-1, -1));	// diagonal principal, pra cima
		direcoes.add (new Point (1, 0));	// direita
		direcoes.add (new Point (0, 1));	// cima
		direcoes.add (new Point (-1, 0));	// esquerda
		direcoes.add (new Point (0, -1));	// baixo
	}
	public Dama (Cor nova_cor, int linha, int coluna) {
		this (nova_cor, new Point (coluna, linha));
	}
	
	
	@Override
	public String toString () {
		return "D";
	}
	
	@Override
	public ArrayList<Movimento> possiveisMovimentos () {
		ArrayList<Casa> casas = new ArrayList<> ();
		Tabuleiro tab = Tabuleiro.getTabuleiro ();
		
		// para cada direção possível
		for (int count = 0; count < direcoes.size (); count++) {
			int i, j;
			Casa aux;	// auxiliar, para testar se pode por ou não em 'casas'
			for (i = (int) coord.getY () + (int) direcoes.get (count).getY (), j = (int) coord.getX () + (int) direcoes.get (count).getX (); Tabuleiro.estaDentro (i, j); i += (int) direcoes.get (count).getY (), j += (int) direcoes.get (count).getX ()) {
				aux = tab.getCasa (i, j);
				
				aux.addDominio (cor);
				
				// não está ocupada por uma peça da mesma cor
				if (!aux.estaOcupadaCor (cor)) {
					casas.add (aux);
					// ocupada por um inimigo
					if (aux.estaOcupada ())
						break;
				}
				else
					break;
			}
		}
		
		ArrayList<Movimento> movs = new ArrayList<> ();
		for (int i = 0; i < casas.size (); i++)
			movs.add (new Movimento (getEssaCasa (), casas.get (i)));
		
		return movs;
	}
	
	@Override
	public void domina (Simulador sim) {
		for (int count = 0; count < direcoes.size (); count++) {
			int i, j;
			Casa aux;	// auxiliar, pra testar à vontade pra por ou não em 'casas'
			// se ainda estiver no tabuleiro, é uma possibilidade
			for (i = (int) coord.getY () + (int) direcoes.get (count).getY (), j = (int) coord.getX () + (int) direcoes.get (count).getX (); Tabuleiro.estaDentro (i, j); i += (int) direcoes.get (count).getY (), j += (int) direcoes.get (count).getX ()) {
				aux = sim.getCasa (i, j);
				
				aux.addDominio (cor);
				
				// ocupada por um inimigo mas domina
				if (aux.estaOcupada ())
					break;
			}
		}
	}
	
	/* GETTERS */
	@Override
	public ImageIcon getIcone () {
		return Icone.DAMA.getImg (cor);
	}
	
	@Override
	public byte getMask () {
		byte aux = super.getMask ();
		return (byte) (8 + aux);
	}
}
