/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import ua.nure.ostpc.malibu.shedule.entity.Club;

public interface ClubDAO {

	public boolean updateClub(Club club);

	public Collection<Club> getIndependentClubs();

	public Club findClubById(Connection con, long clubId) throws SQLException;

	public Collection<Club> getAllClubs();

	public Collection<Club> getOurClubs();

	public boolean insertClubs(Collection<Club> clubs);

	public Collection<Club> getDependentClubs();
}
