/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubSettingViewData;

public interface ClubDAO {

	public Club updateClub(Club club) throws DAOException;

	public Collection<Club> getIndependentClubs();

	public Club findClubById(long clubId);

	public Collection<Club> getAllScheduleClubs();

	public List<Club> getScheduleClubs();
	
	public Collection<Club> getAllOuterClubs();

	public boolean insertClubs(Collection<Club> clubs);

	public List<Club> getDependentClubs();

	public Map<Long, Club> getConformity();

	public Collection<Club> getOnlyOurClub();

	public boolean containsInSchedules(long clubId);

	public Club removeClub(long id) throws DAOException;

	public boolean insertClubsWithConformity(Collection<Club> clubs);
	
	// ===================================
	public List<ClubSettingViewData> getAllClubs() throws Exception;

	public Club setClubIndependent(long id, boolean isIndepended) throws DAOException;

	public Club importClub(Club club) throws DAOException;
	
}
