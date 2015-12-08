package Business;

import java.util.Date;

public class EleicaoPR extends Eleicao{
	private int IdEleicao;
	private boolean volta2;
	private Date data2;
	private  ResultadoPR voltaR1;
	private  ResultadoPR voltaR2;
	private Boletim boletim1;
	private Boletim boletim2;
	
	public EleicaoPR(Date data, int estado, int idEleicao, boolean volta2, Date data2) {
		super(data, estado,idEleicao);
		IdEleicao = idEleicao;
		this.volta2 = volta2;
		this.data2 = data2;
	}
	public int getIdEleicao() {
		return IdEleicao;
	}
	public void setIdEleicao(int idEleicao) {
		IdEleicao = idEleicao;
	}
	public boolean isVolta2() {
		return volta2;
	}
	public void setVolta2(boolean volta2) {
		this.volta2 = volta2;
	}
	public Date getData2() {
		return data2;
	}
	public void setData2(Date data2) {
		this.data2 = data2;
	}
	
	
}