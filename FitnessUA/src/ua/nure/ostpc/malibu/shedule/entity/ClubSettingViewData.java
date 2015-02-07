package ua.nure.ostpc.malibu.shedule.entity;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class ClubSettingViewData implements Serializable, IsSerializable {
	private Club inner;
	private Club outer;
	private int row;

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public Club getInner() {
		return inner;
	}

	public void setInner(Club inner) {
		this.inner = inner;
	}

	public Club getOuter() {
		return outer;
	}

	public void setOuter(Club outer) {
		this.outer = outer;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClubSettingViewData [");
		if (inner != null) {
			builder.append("\n\tinner=");
			builder.append(inner);
			builder.append(", ");
		}
		if (outer != null) {
			builder.append("\n\touter=");
			builder.append(outer);
		}
		builder.append("]");
		return builder.toString();
	}
	
	
}
