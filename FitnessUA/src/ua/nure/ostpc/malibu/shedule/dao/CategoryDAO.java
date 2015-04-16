package ua.nure.ostpc.malibu.shedule.dao;

import java.util.Collection;
import java.util.List;

import ua.nure.ostpc.malibu.shedule.entity.Category;

/**
 * Interface that all CategoryDAOs must support
 * 
 * @author Volodymyr_Semerkov
 */
public interface CategoryDAO {

	public List<Category> getAllCategories();

	public List<Category> getCategoriesWithEmployees() throws DAOException;

	public Category getCategoryById(long categoryId);

	public boolean insertCategory(Collection<Category> category)
			throws DAOException;

	public boolean deleteCategory(Collection<Category> categories)
			throws DAOException;

	public boolean insertEmployees(long idCategory, Collection<Long> employees)
			throws DAOException;

	public boolean deleteEmployees(long idCategory, Collection<Long> employees)
			throws DAOException;

	public boolean updateCategory(Category category) throws DAOException;
}
