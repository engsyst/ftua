package ua.nure.ostpc.malibu.shedule.security.path;

import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.entity.Right;

/**
 * Security manager.
 * 
 * @author Volodymyr_Semrkov
 * 
 */
public class SecurityManager {
	List<Constraint> constraints;
	private static final Logger log = Logger.getLogger(SecurityManager.class);

	public SecurityManager(List<Constraint> constraints) {
		for (Constraint constraint : constraints) {
			String urlPattern = constraint.getURLPattern();
			urlPattern = urlPattern.replaceAll("\\*", ".*");
			constraint.setURLPattern(urlPattern);
		}
		this.constraints = constraints;
	}

	public boolean accept(String pagePath, Right right) {
		log.trace("pagePath --> " + pagePath);
		log.trace("right --> " + right.name());
		for (Constraint constraint : constraints) {
			if (constraint.getRights().contains(right)
					&& pagePath.matches(constraint.getURLPattern())) {
				return false;
			}
		}
		return true;
	}
}