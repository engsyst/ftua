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
	private boolean isDeleted;

	public Club() {
	}

	public Club(long clubId, String title, boolean isIndependent,
			boolean isDeleted) {
		this.clubId = clubId;
		this.title = title;
		this.isIndependent = isIndependent;
		this.isDeleted = isDeleted;
	}

	public long getClubId() {
		return this.clubId;
	}

	public void setClubId(long clubId) {
		this.clubId = clubId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get club dependency.
	 * 
	 * @return
	 */
	public boolean isIndependent() {
		return isIndependent;
	}

	/**
	 * Set club dependency.
	 * 
	 * @param isIndependent
	 */
	public void setIndependent(boolean isIndependent) {
		this.isIndependent = isIndependent;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime + (int) (clubId ^ (clubId >>> 32));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Club))
			return false;
		Club other = (Club) obj;
		return clubId == other.clubId && title.equals(other.title)
				&& isIndependent == other.isIndependent
				&& isDeleted == other.isDeleted;
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
		sb.append(", isDeleted=");
		sb.append(isDeleted);
		sb.append("]");
		return sb.toString();
	}
}
