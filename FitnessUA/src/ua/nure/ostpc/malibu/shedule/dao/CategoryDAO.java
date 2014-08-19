package ua.nure.ostpc.malibu.shedule.dao;

import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Category;

/**
 * Interface that all CategoryDAOs must support
 * 
 * @author Volodymyr_Semerkov
 */
public interface CategoryDAO {

	public List<Category> getCategoriesWithEmployees();

	public Category getCategoryById(long categoryId);
}
