package it.polito.tdp.seriea.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Parameterizable;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.omg.CORBA.PUBLIC_MEMBER;

import it.polito.tdp.seriea.db.SerieADAO;


public class Model {
	private SerieADAO dao;
	private SimpleDirectedWeightedGraph<Stagione, DefaultWeightedEdge> grafo;
	private List<Stagione> stagioni;
	private List <DefaultWeightedEdge> camminoMax;
	
	public Model() {
		dao= new SerieADAO();
	}
	
	public List<Team> getTeams(){
		return dao.listTeams();
	}
	public List<Stagione> getStagioni(Team squadra) {
		stagioni=dao.getStagioni(squadra);
		return stagioni ;
	}

	public void creaGrafo(Team squadra) {
		grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, getStagioni(squadra));
		for(Stagione s1: grafo.vertexSet()) {
			for(Stagione s2: grafo.vertexSet()) {
				if(!s1.equals(s2)) {
				if(s1.getPunti()>s2.getPunti()) {
					Graphs.addEdgeWithVertices(grafo, s2, s1, s1.getPunti()-s2.getPunti());
				}else {
					Graphs.addEdgeWithVertices(grafo, s1, s2, s2.getPunti()-s1.getPunti());
				}}
			}
		}
		
		
	}
	
	
	public String grafoCreato() {
		return "Grafo creato:\n"+grafo.vertexSet().size()+" vertici+\n"+grafo.edgeSet().size()+" archi\n";
	}
	public String annataDoro() {
		int anno=0;
		int differenza=0;
		
		for(Stagione s: grafo.vertexSet()) {
			int entrata= calcolaSommaPesi(grafo.incomingEdgesOf(s));
			int uscita= calcolaSommaPesi(grafo.outgoingEdgesOf(s));
			int diff= entrata-uscita;
			if(diff>differenza) {
				anno= s.getStagione();
				differenza= diff;
			}
		
		}
		return anno+" "+differenza;
	}

	private int calcolaSommaPesi(Set<DefaultWeightedEdge> set) {
		int somma=0;
		for(DefaultWeightedEdge de: set) {
			somma+=grafo.getEdgeWeight(de);
		}
		return somma;
	}
	
	//algoritmo ricorsivo
	public void init() {
		List<DefaultWeightedEdge> parziale= new ArrayList<DefaultWeightedEdge>() ;
		camminoMax= new ArrayList<>();
		camminoPerfetto(parziale, 1);
	}
	private void camminoPerfetto(List<DefaultWeightedEdge> parziale, int livello) {
		//usa la lista delle stagioni per trovare quelle sostiutive
		
		if(parziale.size()>camminoMax.size()) {
			camminoMax= new ArrayList<>(parziale);
		}
		
		
		//se l'arco c'è il punteggio è già migliore
		
		for(DefaultWeightedEdge dwe: grafo.outgoingEdgesOf(stagioni.get(livello-1))) {
			
			if(!parziale.contains(dwe) ) {
				
				if(parziale.size()==0) {
					if(grafo.getEdgeTarget(dwe).equals(stagioni.get(livello))){
					parziale.add(dwe);
					camminoPerfetto(parziale, livello);
					parziale.remove(dwe);
					}
				}else {
				if(grafo.getEdgeTarget(parziale.get(livello-1)).equals(stagioni.get(livello+1)) && grafo.getEdgeTarget(dwe).equals(stagioni.get(livello+1))) {
				parziale.add(dwe);
				camminoPerfetto(parziale, livello+1);
				parziale.remove(dwe);
			}
				}
			}
			}
		
	}
		
	


	public List<DefaultWeightedEdge> getCamminoMax(){
		
		return camminoMax;
	}
	
}
