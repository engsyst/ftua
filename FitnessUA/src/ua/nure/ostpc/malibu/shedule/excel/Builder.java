package ua.nure.ostpc.malibu.shedule.excel;

import java.util.Set;

public interface Builder<T> {
	T createItem(Set<DataField> fields);
}
