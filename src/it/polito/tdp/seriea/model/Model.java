package it.polito.tdp.seriea.model;

import java.util.List;
import java.util.Set;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.seriea.db.SerieADAO;

public class Model {
	SerieADAO dao;
	SimpleDirectedWeightedGraph<Stagione, DefaultWeightedEdge> grafo;
	
	public Model() {
		dao= new SerieADAO();
	}
	
	public List<Team> getTeams(){
		return dao.listTeams();
	}
	public List<Stagione> getStagioni(Team squadra) {
		return dao.getStagioni(squadra);
	}

	public void creaGrafo(Team squadra) {
		grafo= new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(grafo, getStagioni(squadra));
		for(Stagione s1: grafo.vertexSet()) {
			for(Stagione s2: grafo.vertexSet()) {
				if(!s1.equals(s2)) {
				if(s1.getPunti()>s2.getPunti()) {
					Graphs.addEdgeWithVertices(grafo, s1, s2, s1.getPunti()-s2.getPunti());
				}else {
					Graphs.addEdgeWithVertices(grafo, s2, s1, s2.getPunti()-s1.getPunti());
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
}
