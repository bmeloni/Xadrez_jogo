package xadrez.peca;

import ui.Cor;
import ui.Icone;

import xadrez.tabuleiro.Casa;
import xadrez.tabuleiro.Tabuleiro;
import xadrez.tabuleiro.Simulador;
import xadrez.movimento.Movimento;
import xadrez.movimento.EnPassant;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Peao extends Peca {
	/**
	 * Estado do peão (muda na jogabilidade):
	 * 		PRIMEIRA: primeira jogada desse peão: pode andar duas casas
	 * 		EN_PASSANT: acabou de dar a primeira jogada: pode ser tomado por 'en passant'
	 * 		NADA: se passou a chance de 'en passant'
	 */
	private enum Estado {
		NADA,	// acabaram os estados especiais
		PRIMEIRA,	// primeira jogada: posso mover duas
		MOVI_DUAS,	// move duas casas: tenho que poder ser tomado por en passant
		EN_PASSANT;	// posso ser tomado por en passant
	};
	
	private Estado estado;
	private int lado;
	
	/**
	 * Ctor
	 */
	public Peao (Cor nova_cor, Point P) {
		super (nova_cor, P);
		estado = Estado.PRIMEIRA;	
		// dependendo da cor, vai pra frente ou pra trás
		lado = (cor == Cor.BRANCO) ? -1 : 1;
	}
	public Peao (Cor nova_cor, int linha, int coluna) {
		this (nova_cor, new Point (coluna, linha));
	}
	public Peao (Cor nova_cor, int linha, int coluna, int estado) {
		this (nova_cor, new Point (coluna, linha));
		switch (estado) {
			case 1: this.estado = Estado.PRIMEIRA; break;
			case 2: this.estado = Estado.MOVI_DUAS; break;
			case 3: this.estado = Estado.EN_PASSANT; break;
			default: this.estado = Estado.NADA;
		}
	}
	
	
	@Override
	public String toString () {
		return "P";
	}
	@Override	// Só o peão sobreescreve esse método
	public String getNotacao () {
		return "";
	}
	
	@Override
	public ArrayList<Movimento> possiveisMovimentos () {
		ArrayList<Movimento> movs = new ArrayList<> ();
		ArrayList<Casa> casas = new ArrayList<> ();
		Tabuleiro tab = Tabuleiro.getTabuleiro ();

		Casa aux, aux_en_passant;	// auxiliares, para testar se pode por ou não em 'casas'
		aux = tab.getCasa ((int) coord.getY () + lado, (int) coord.getX ());
		if (!aux.estaOcupada ()) {
			casas.add (aux);
			// se for a primeira jogada, e ninguém tá na frente, pode andar 2 casas
			if (estado == Estado.PRIMEIRA) {
				aux = tab.getCasa ((int) coord.getY () + (2 * lado), (int) coord.getX ());
				if (!aux.estaOcupada ())
					casas.add (aux);
			}
		}
		// pode tomar
		aux = tab.getCasa ((int) coord.getY () + lado, (int) coord.getX () + 1);
		aux_en_passant = tab.getCasa ((int) coord.getY (), (int) coord.getX () + 1);
		if (aux != null) {
			aux.addDominio (cor);
			if (aux.estaOcupadaCor (cor.oposta ()))
				casas.add (aux);
			else if (!aux.estaOcupada () && aux_en_passant != null && aux_en_passant.estaOcupadaCor (cor.oposta ()) && aux_en_passant.getPeca () instanceof Peao && ((Peao) aux_en_passant.getPeca ()).estado == Estado.EN_PASSANT)
				movs.add (new EnPassant (getEssaCasa (), aux, aux_en_passant));
		}
			
		aux = tab.getCasa ((int) coord.getY () + lado, (int) coord.getX () - 1);
		aux_en_passant = tab.getCasa ((int) coord.getY (), (int) coord.getX () - 1);
		if (aux != null) {
			aux.addDominio (cor);
			if (aux.estaOcupadaCor (cor.oposta ()))
				casas.add (aux);
			else if (!aux.estaOcupada () && aux_en_passant != null && aux_en_passant.estaOcupadaCor (cor.oposta ()) && aux_en_passant.getPeca () instanceof Peao && ((Peao) aux_en_passant.getPeca ()).estado == Estado.EN_PASSANT)
				movs.add (new EnPassant (getEssaCasa (), aux, aux_en_passant));
		}

		for (int i = 0; i < casas.size (); i++)
			movs.add (new Movimento (getEssaCasa (), casas.get (i)));
		
		return movs;
	}
	
	@Override
	public void domina (Simulador sim) {
		Casa aux;
		aux = sim.getCasa ((int) coord.getY () + lado, (int) coord.getX () + 1);
		if (aux != null)
			aux.addDominio (cor);
		aux = sim.getCasa ((int) coord.getY () + lado, (int) coord.getX () - 1);
		if (aux != null)
			aux.addDominio (cor);
	}
	
	/**
	 * Se moveu, talvez mude o estado
	 */
	public void update (boolean moveu) throws PromoveuException {
		if (moveu) {
			if (estado == Estado.PRIMEIRA && ((int) coord.getY () == 3 || (int) coord.getY () == 4))
					estado = Estado.MOVI_DUAS;
			else
				estado = Estado.NADA;
		}
		// não moveu - update de cada vez: do updatePiaums
		else {
			// move duas, então posso ser tomado por en passant
			if (estado == Estado.MOVI_DUAS) {
				estado = Estado.EN_PASSANT;
			}
			else if (estado == Estado.EN_PASSANT) {
				estado = Estado.NADA;
			}
			// Troca de peça do peao
			else if ((int) coord.getY () == 0 || (int) coord.getY () == 7) {
				morre ();
				String[] opcoes = { "Bispo", "Cavalo", "Dama", "Torre" };		// opções para trocar o peao por outra peça
				String selecionado = (String) JOptionPane.showInputDialog (null, "Qual peça você deseja?", "", JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);
				PecaFactory fabrica = new PecaFactory ();
				Peca nova = fabrica.cria (selecionado);
				nova.setCoord (coord);
				nova.setCor (cor);

				Tabuleiro.getTabuleiro ().getCasa (coord).setPeca (nova);
				Tabuleiro.getTabuleiro ().getCasa (coord).atualizaIcone ();

				throw new PromoveuException (nova);
			}
		}
	}
	
	/* GETTERS */
	@Override
	public ImageIcon getIcone () {
		return Icone.PEAO.getImg (cor);
	}
	
	@Override
	public byte getMask () {
		byte aux = super.getMask ();
		return (byte) ((estado.ordinal () << 5) + 12 + aux);
	}
	/* SETTERS */
	@Override
	public void setCor (Cor cor) {
		super.setCor (cor);
		lado = (cor == Cor.BRANCO) ? -1 : 1;
	}
}
