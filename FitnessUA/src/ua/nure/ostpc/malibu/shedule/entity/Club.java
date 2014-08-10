package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Description of Club.
 * 
 * @author engsyst
 */
public class Club implements Serializable, IsSerializable {
	private static final long serialVersionUID = 1L;
	private long clubId;
	private String title;
	private boolean isIndependent;

	public Club() {
	}

	public Club(long clubId, String title, boolean isIndependent) {
		this.clubId = clubId;
		this.title = title;
		this.isIndependent = isIndependent;
	}

	/**
	 * Returns clubId.
	 * 
	 * @return clubId
	 */
	public long getClubId() {
		return this.clubId;
	}

	/**
	 * Sets a value to attribute clubId.
	 * 
	 * @param newClubId
	 */
	public void setClubId(long newClubId) {
		this.clubId = newClubId;
	}

	/**
	 * Returns title.
	 * 
	 * @return title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets a value to attribute title.
	 * 
	 * @param newTitle
	 */
	public void setTitle(String newTitle) {
		this.title = newTitle;
	}

	/**
	 * Returns isIndependen.
	 * 
	 * @return isIndependen
	 */
	public Boolean getIsIndependen() {
		return this.isIndependent;
	}

	/**
	 * Sets a value to attribute isIndependen.
	 * 
	 * @param newIsIndependen
	 */
	public void setIsIndependent(Boolean newIsIndependen) {
		this.isIndependent = newIsIndependen;
	}

	public int getQuantityOfPeople() {
		return 0;
	}

	public void setQuantityOfPeople(int quantityOfPeople) {
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Club [clubId=");
		sb.append(clubId);
		sb.append(", title=");
		sb.append(title);
		sb.append(", isIndependent=");
		sb.append(isIndependent);
		sb.append("]");
		return sb.toString();
	}
}
