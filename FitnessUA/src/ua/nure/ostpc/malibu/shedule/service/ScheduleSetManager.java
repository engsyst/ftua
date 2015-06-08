package ua.nure.ostpc.malibu.shedule.service;

public class ScheduleSetManager implements Runnable {

	private NonclosedScheduleCacheService nonclosedScheduleCacheService;

	public ScheduleSetManager(
			NonclosedScheduleCacheService nonclosedScheduleCacheService) {
		this.nonclosedScheduleCacheService = nonclosedScheduleCacheService;
	}

	@Override
	public void run() {
		nonclosedScheduleCacheService.removeClosedSchedules();
		nonclosedScheduleCacheService.setCurrentStatus();
	}
}