package xadrez.peca;

public class PromoveuException extends Exception {
	String S;
	Peca nova;
	
	public PromoveuException (Peca nova) {
		S = "Peça alterda com sucesso!";
		this.nova = nova;
	}
	
	public Peca getNova () {
		return nova;
	}
}
