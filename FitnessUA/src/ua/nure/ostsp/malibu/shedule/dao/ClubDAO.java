/*******************************************************************************
 * 2014, All rights reserved.
 *******************************************************************************/
package ua.nure.ostsp.malibu.shedule.dao;

import java.util.Collection;

import ua.nure.ostsp.malibu.shedule.entity.Club;

// End of user code

/**
 * Description of ClubDAO.
 * 
 * @author engsyst
 */
public interface ClubDAO {
	public int insertClub(Club club);

	public boolean deleteClub(Club club);

	public Club findClub();

	public boolean updateClub(Club club);

	public Collection<Club> selectClubsTO();
}
