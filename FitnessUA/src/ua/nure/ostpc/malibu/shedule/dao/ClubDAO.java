/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import ua.nure.ostpc.malibu.shedule.entity.Club;

public interface ClubDAO {

	public boolean updateClub(Club club) throws SQLException;

	public Collection<Club> selectClubs() throws SQLException;

	public Club findClubById(Connection con, long clubId) throws SQLException;

	public Collection<Club> getMalibuClubs() throws SQLException;

	public Collection<Club> getOurClubs() throws SQLException;

	public void insertClubs(Collection<Club> clubs) throws SQLException;

	public Collection<Club> getDependentClubs();

}
