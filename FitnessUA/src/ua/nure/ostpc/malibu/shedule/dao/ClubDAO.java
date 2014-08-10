/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import ua.nure.ostpc.malibu.shedule.entity.Club;

public interface ClubDAO {

	public boolean updateClub(Club club);

	public Collection<Club> getIndependentClubs();

	public Club findClubById(long clubId);

	public Collection<Club> getAllScheduleClubs();

	public Collection<Club> getAllMalibuClubs();

	public boolean insertClubs(Collection<Club> clubs);

	public List<Club> getDependentClubs();
	
	public Map<Long, Club> getConformity();
	
	public Collection<Club> getOnlyOurClub();
	
	public Boolean deleteClub(long id);
	
	public boolean insertClubsWithConformity(Collection<Club> clubs);
}
