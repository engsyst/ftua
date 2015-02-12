package ua.nure.ostpc.malibu.shedule.entity;


public enum GenFlags {
	/**
	 * Employee can be assigned to one shift in day only 
	 */
	ONLY_ONE_SHIFT(0), 
	/**
	 * Schedule can contain empty shifts 
	 */
	SCHEDULE_CAN_EMPTY(1), 
	/**
	 * Employee can be assigned only if their assignments &lt; emp.getMaxDays 
	 */
	CHECK_EMP_MAX_DAYS(2), 
	/**
	 * Employee must have weekend after any 40 hours of work (MAX_HOURS_IN_WEEK) 
	 */
	WEEKEND_AFTER_MAX_HOURS(3),
	/**
	 * Employee can not be assigned if their work hours &gt; 40 hours (MAX_HOURS_IN_WEEK) 
	 */
	CHECK_MAX_HOURS_IN_WEEK(4),
	/**
	 * ONLY_ONE_SHIFT | SCHEDULE_CAN_EMPTY | CHECK_MAX_DAYS | WEEKEND_AFTER_MAX_HOURS
	 */
	DEFAULT(ONLY_ONE_SHIFT, SCHEDULE_CAN_EMPTY, CHECK_EMP_MAX_DAYS, WEEKEND_AFTER_MAX_HOURS), ;

	private int mode;

	GenFlags(int bit) {
		this.mode = (1 << bit);
	}

	GenFlags(GenFlags... flags) {
		mode = 0;
		for (GenFlags f : flags)
			this.mode |= f.getMask();
	}

	public int getMask() {
		return mode;
	}

	public void setMode(GenFlags... flags) {
		mode = 0;
		for (GenFlags f : flags) {
			int mask = f.getMask(); 
			this.mode |= mask;
		}
	}

	public void setMode(int mode) {
		this.mode = mode;
//		BitSet bits = new BitSet(mode);
//		for (int i = 0; i < bits.length(); i++) {
//			mode |= bits.get(i) ? 1 : 0; 
//		}
	}
	
	public boolean isSet(GenFlags... flags) {
		for (GenFlags f : flags)
			if ((mode & f.mode) != f.mode)
				return false;
		return true;
	}
	
	public static void main(String[] args) {
		Preference p = new Preference(1, 1, 13, 40, 25, 28);
		System.out.println(p.isFlagsSet(GenFlags.SCHEDULE_CAN_EMPTY));
	}
}