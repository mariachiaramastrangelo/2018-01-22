package it.polito.tdp.seriea.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polito.tdp.seriea.model.Season;
import it.polito.tdp.seriea.model.Stagione;
import it.polito.tdp.seriea.model.Team;

public class SerieADAO {

	public List<Season> listAllSeasons() {
		String sql = "SELECT season, description FROM seasons";
		List<Season> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Season(res.getInt("season"), res.getString("description")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<Team> listTeams() {
		String sql = "SELECT team FROM teams";
		List<Team> result = new ArrayList<>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Team(res.getString("team")));
			}

			conn.close();
			return result;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Stagione> getStagioni(Team squadra){
		//conto delle vittorie totali in casa e fuori casa
		String sql="SELECT m.Season as stagione, COUNT(*) as vinte " + 
				"FROM matches m " + 
				"WHERE (m.`HomeTeam`=? && m.`FTR`='H') || (m.`AwayTeam`=? && m.`FTR`='A')  " + 
				"GROUP BY m.`Season` ";
		
		
		List<Stagione> result= new ArrayList<>();
		try {
			Connection conn= DBConnect.getConnection();
			PreparedStatement st= conn.prepareStatement(sql);
			st.setString(1, squadra.getTeam());
			st.setString(2, squadra.getTeam());
			ResultSet rs= st.executeQuery();
			while (rs.next()) {
				result.add(new Stagione(rs.getInt("stagione"), (rs.getInt("vinte")*3)));
			}
			
			conn.close();
			getPareggi(squadra, result);
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public void getPareggi(Team squadra, List<Stagione> result){
		//conto dei pareggi
		String sql="SELECT m.Season as stagione, COUNT(*) as pareggi " + 
					"FROM matches m " + 
					"WHERE (m.`AwayTeam`=? || m.`HomeTeam`=?) && m.`FTR`='D' " + 
					"GROUP BY m.`Season` ";
		try {
		Connection conn= DBConnect.getConnection();
		PreparedStatement st= conn.prepareStatement(sql);
		st.setString(1, squadra.getTeam());
		st.setString(2, squadra.getTeam());
		ResultSet rs= st.executeQuery();
		while (rs.next()) {
			for(Stagione s: result) {
				if(s.getStagione()==rs.getInt("stagione")) {
					s.setPunti(s.sommaPunteggi(rs.getInt("pareggi")));
				}
			}	
		}
			conn.close();
		}catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	

}
