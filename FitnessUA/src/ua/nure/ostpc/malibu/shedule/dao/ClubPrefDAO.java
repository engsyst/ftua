package ua.nure.ostpc.malibu.shedule.dao;

import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.ClubPref;

/**
 * Interface that all ClubPrefDAOs must support
 * 
 * @author Volodymyr_Semerkov
 */
public interface ClubPrefDAO {

	public List<ClubPref> getClubPrefsByPeriodId(long periodId);

	public boolean containsClubPref(long clubPrefId);

	public boolean insertClubPref(ClubPref clubPref);

	public boolean updateClubPref(ClubPref clubPref);

	public boolean removeClubPref(ClubPref clubPref);
}
