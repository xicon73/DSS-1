package Business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import Data.ListaPRDAO;
import Data.ResultadoCirculoPRDAO;
import Exception.ExceptionCandidatoExiste;
import Exception.ExceptionIniciarEleicao;
import Exception.ExceptionListaExiste;
import Exception.ExceptionListaNaoExiste;
import Exception.ExceptionTerminarEleicao;

/**
 * Passar circulos tambem nos construtores
 */

public class EleicaoPR extends Eleicao {
	private boolean volta2;
	private Calendar data2;
	private ResultadoCirculoPRDAO voltaR1;
	private ResultadoCirculoPRDAO voltaR2;
	private ListaPRDAO listas;
	private Set<Integer> votantes2;

	public EleicaoPR(int idEleicao, Calendar data, Collection<Circulo> c) {
		super(idEleicao, data);
		this.volta2 = false;
		this.data2 = null;
		this.voltaR1 = initResultadoCirculoPRDAO(idEleicao, 1, c);
		this.voltaR2 = initResultadoCirculoPRDAO(idEleicao, 2, c);
		this.listas = new ListaPRDAO(idEleicao);
		this.votantes2 = new HashSet<>();
	}

	public EleicaoPR(int idEleicao, Calendar data, int estado, boolean permitirVotar, Set<Integer> vot,
			Collection<Circulo> c, Set<Integer> vot2, boolean volta2, Calendar data2) {
		super(idEleicao, data, estado, permitirVotar, vot);
		this.volta2 = volta2;
		this.data2 = data2;
		this.voltaR1 = initResultadoCirculoPRDAO(idEleicao, 1, c);
		this.voltaR2 = initResultadoCirculoPRDAO(idEleicao, 2, c);
		this.listas = new ListaPRDAO(idEleicao);
		this.votantes2 = vot2;
	}

	private ResultadoCirculoPRDAO initResultadoCirculoPRDAO(int idEleicao, int volta, Collection<Circulo> circulos) {
		ResultadoCirculoPRDAO resDAO = new ResultadoCirculoPRDAO(idEleicao, volta);
		for (Circulo c : circulos) {
			if (!resDAO.containsKey(c.getId())) {
				resDAO.put(c.getId(), new ResultadoCirculoPR(c));
			}
		}
		return resDAO;
	}

	public ResultadoCirculoPRDAO getVoltaR1() {
		return this.voltaR1;
	}

	public ResultadoCirculoPRDAO getVoltaR2() {
		return this.voltaR2;
	}

	public ListaPRDAO getListas() {
		return this.listas;
	}

	public Set<Integer> getVotantes2() {
		return this.votantes2;
	}

	public boolean isVolta2() {
		return volta2;
	}

	public void setVolta2(boolean volta2) {
		this.volta2 = volta2;
	}

	public Calendar getData2() {
		return data2;
	}

	public void setData2(Calendar data2) {
		this.data2 = data2;
	}

	/**
	 * Metodo addLista que adiciona a listaPR com o candidato c á base de dados.
	 * Apenas adiciona as listas e nao aos REsultadosCirculoPR
	 * 
	 * @param c
	 * @throws ExceptionCandidatoExiste
	 */
	public void addLista(Candidato cand) throws ExceptionCandidatoExiste {
		for (ListaPR l : this.listas.values()) {
			if (l.candidatoEquals(cand)) {
				throw new ExceptionCandidatoExiste(
						"A lista que deseja adicionar tem um candidato que já se encontra a concorer para a mesma eleição");
			}
		}
		ListaPR lista = new ListaPR(super.getIdEleicao(), this.listas.size() + 1, cand);
		this.listas.put(lista.getIdListaPR(), lista);
	}

	@Override
	public void removeLista(Listavel lista) {
		ListaPR listpr = (ListaPR) lista;
		this.listas.remove(listpr.getIdListaPR());
	}

	@Override
	public void addVoto(Listavel lista, Circulo circulo) {
		ResultadoCirculoPR resPR;
		ListaPR listpr = (ListaPR) lista;
		if (volta2 == false) {
			resPR = this.voltaR1.get(circulo.getId());
			resPR.addVoto(listpr);
			this.voltaR1.put(resPR.getCirculo().getId(), resPR);
		} else {
			resPR = this.voltaR2.get(circulo.getId());
			resPR.addVoto(listpr);
			this.voltaR2.put(resPR.getCirculo().getId(), resPR);
		}
	}
	
	public void public void addVotante(Eleitor e) {
		
	};

	@Override
	public void addVotoNulo(int idCirculo) {
		ResultadoCirculoPR resPR;
		if (volta2 == false) {
			resPR = this.voltaR1.get(idCirculo);
			resPR.addVotoNulo();
			this.voltaR1.put(idCirculo, resPR);
		} else {
			resPR = this.voltaR2.get(idCirculo);
			resPR.addVotoNulo();
			this.voltaR2.put(idCirculo, resPR);
		}
	}
	
	@Override
	public void addVotoBranco(int idCirculo) {
		ResultadoCirculoPR resPR;
		if (volta2 == false) {
			resPR = this.voltaR1.get(idCirculo);
			resPR.addVotoBranco();
			this.voltaR1.put(idCirculo, resPR);
		} else {
			resPR = this.voltaR2.get(idCirculo);
			resPR.addVotoBranco();
			this.voltaR2.put(idCirculo, resPR);
		}
	}

	/**
	 * Metodo que vai criar um objecto boletim com todas as listas que vao
	 * participar na eleiçao
	 */
	@Override
	public Boletim getBoletim(int idCirculo) {
		Boletim b = null;
		if (this.estado(0)) {
			if (!volta2) {
				b = new Boletim(this.listas.size());
				for (ListaPR l : this.listas.values()) {
					b.addLista(l);
				}
			} else {
				b = new Boletim(2);
				for (ListaPR l : this.listas.values()) {
					if (l.ordem2() != -1) {
						b.addLista(l);
					}
				}
			}
		}
		return b;
	}

	/**
	 * Metodo que vai gerar os numeros de ordem para as listas
	 * 
	 * @param collection
	 * @return
	 */
	private Boletim geraBoletim(Collection<ListaPR> listas) {
		Random r = new Random();
		int nListas = listas.size();
		Boletim b = new Boletim(nListas);
		int rand;
		for (ListaPR listaPR : listas) {
			rand = r.nextInt(nListas - 1);
			if (!this.volta2) {
				listaPR.setOrdem1(rand);
			} else {
				listaPR.setOrdem2(rand);
			}
			this.listas.put(listaPR.getIdListaPR(), listaPR);
			b.addLista(listaPR);
			nListas--;
		}
		return b;
	}

	/**
	 * ter em atenção a esta função possivel local de erro
	 * 
	 * @return
	 */
	public Set<ListavelVotos> resultadosPrimeiraVolta() {
		HashMap<ListaPR, Integer> validos;
		HashMap<ListaPR, Integer> segVolta = new HashMap<>();
		for (ResultadoCirculoPR resC : this.voltaR1.values()) {
			validos = resC.getValidos();
			for (ListaPR lista : validos.keySet()) {
				segVolta.put(lista, segVolta.get(lista) + validos.get(lista)); // linha a rever
			}
		}
		//ordenar as listas pelo numero de votos
		TreeSet<ListavelVotos> listasSeg = new TreeSet<>(new ComparatorListavelVotos());
		for(ListaPR lista : segVolta.keySet()){
			ListavelVotos lv = new ListavelVotos(lista, segVolta.get(lista));
			listasSeg.add(lv);
		}
		return listasSeg;
	}
	
	/*
	 * preciso depois verificar se a ordem do comaprador está correta para isto dar as duas mais votadas
	 */
	private Set<ListaPR> disputaSegundaVolta(Set<ListavelVotos> listas){
		int n =0;
		Set<ListaPR> listasSeg = new HashSet<ListaPR>();
		Iterator<ListavelVotos> itListas = listas.iterator();
		while(itListas.hasNext() && n<2){
			listasSeg.add((ListaPR)itListas.next().getLista());
		}
		return listasSeg;
	}
	

	/**
	 * Falta para quando inicio a eleiçao colocar as listas nos resultados
	 * preciso de colocar nos resultados volta1 as listas se for para a volta 2
	 * colocar nos resultados as duas listas anteriores gerarBoletins
	 * @throws ExceptionTerminarEleicao 
	 */
	@Override
	public void iniciar() throws ExceptionIniciarEleicao {
		if (super.estado(-1)) {// iniciar depois da eleicao ter sido criada
			Collection<ListaPR> list = this.listas.values();
			this.geraBoletim(list);
			for (ResultadoCirculoPR resC : this.voltaR1.values()) {
				resC.addListas(list);
				this.voltaR1.put(resC.getCirculo().getId(), resC);
			}
		} else {
			if (super.estado(0) && this.volta2) { // iniciar segunda volta
				Collection<ListaPR> list = this.disputaSegundaVolta(this.resultadosPrimeiraVolta());
				if(list.size()==2){
					this.geraBoletim(list);
					for (ResultadoCirculoPR resC : this.voltaR2.values()) {
						resC.addListas(list);
						this.voltaR2.put(resC.getCirculo().getId(), resC);
					}
				}
				
			}else{
				throw new ExceptionIniciarEleicao("Inpossivel iniar Elicão;");
			}
		}
	}

	@Override
	public void terminar() {
		if (super.estado(0) && this.volta2 == false) { // terminar primeira
														// volta
			if(){//verificar se ouve maioria absoluta
				
			}

		} else {// segunda volta
			if(){
				
			}
		}
	}

	// metodo para calcular se ouve vencedor com maioria absoluta

	private void defData2() {
		Calendar data2aux = new GregorianCalendar();
		data2aux.set(super.getData().get(Calendar.YEAR), super.getData().get(Calendar.MONTH), super.getData().get(Calendar.DAY_OF_YEAR));;
		data2aux.add(Calendar.DAY_OF_YEAR, 30);
		while(data2aux.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY){
			data2aux.add(Calendar.DAY_OF_YEAR, 1);
		}
		this.data2 = data2aux;
	}

}
