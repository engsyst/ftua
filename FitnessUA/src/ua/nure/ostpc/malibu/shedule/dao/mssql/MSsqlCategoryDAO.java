package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.CategoryDAO;
import ua.nure.ostpc.malibu.shedule.dao.DAOException;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlCategoryDAO implements CategoryDAO {
	private static final Logger log = Logger.getLogger(MSsqlCategoryDAO.class);

	private static final String SQL__GET_CATEGORIES_ID_WITH_EMPLOYEES = "SELECT DISTINCT Categories.CategoryId FROM Categories "
			+ "INNER JOIN CategoryEmp ON CategoryEmp.CategoryId=Categories.CategoryId;";
	private static final String SQL__GET_ALL_CATEGORIES = "SELECT * FROM Categories;";
	private static final String SQL__GET_CATEGORY_BY_ID = "SELECT * FROM Categories WHERE CategoryId=?;";
	private static final String SQL__GET_EMPLOYEES_ID_BY_CATEGORY_ID = "SELECT EmployeeId FROM CategoryEmp WHERE CategoryId=?;";
	private static final String SQL__DELETE_CATEGORY = "delete from Categories where CategoryId = ?";
	private static final String SQL__INSERT_CATEGORY = "insert into Categories(Title) values (?)";
	private static final String SQL__INSERT_EMPLOYEE_IN_CATEGORY = "INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES (?, ?)";
	private static final String SQL__DELETE_EMPLOYEE_FROM_CATEGORY = "DELETE FROM CategoryEmp WHERE "
			+ "CategoryId = ? AND EmployeeId = ?";
	private static final String SQL__UPDATE_CATEGORY_TITLE = "UPDATE Categories SET Title=? WHERE CategoryId=?;";
	private static final String SQL__GET_OTHER_CATEGORY_WITH_TITLE = "SELECT * FROM Categories WHERE Title=? AND CategoryId!=?;";

	@Override
	public List<Category> getAllCategories() {
		Connection con = null;
		List<Category> categories = new ArrayList<Category>();
		try {
			con = MSsqlDAOFactory.getConnection();
			categories = getAllCategories(con);
		} catch (SQLException e) {
			log.error("Can not get all categories.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return categories;
	}

	private List<Category> getAllCategories(Connection con) throws SQLException {
		Statement stmt = null;
		List<Category> categories = new ArrayList<Category>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(SQL__GET_ALL_CATEGORIES);
			while (rs.next()) {
				Category category = unMapCategory(rs);
				List<Long> employeesIdByCategoryId = getEmployeesIdByCategoryId(
						con, category.getCategoryId());
				category.setEmployeeIdList(employeesIdByCategoryId);
				categories.add(category);
			}
			return categories;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(stmt);
		}
	}

	@Override
	public List<Category> getCategoriesWithEmployees() throws DAOException {
		Connection con = null;
		List<Category> categories = new ArrayList<Category>();
		try {
			con = MSsqlDAOFactory.getConnection();
			categories = getCategoriesWithEmployees(con);
		} catch (SQLException e) {
			log.error("Can not get categories with employees.", e);
			throw new DAOException("Can not get categories with employees.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return categories;
	}

	private List<Category> getCategoriesWithEmployees(Connection con)
			throws SQLException {
		Statement stmt = null;
		List<Category> categories = new ArrayList<Category>();
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt
					.executeQuery(SQL__GET_CATEGORIES_ID_WITH_EMPLOYEES);
			while (rs.next()) {
				long categoryId = rs.getLong(MapperParameters.CATEGORY__ID);
				Category category = getCategoryById(con, categoryId);
				categories.add(category);
			}
			return categories;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(stmt);
		}
	}

	@Override
	public Category getCategoryById(long categoryId) {
		Connection con = null;
		Category category = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			category = getCategoryById(con, categoryId);
		} catch (SQLException e) {
			log.error("Can not get category by id.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return category;
	}

	private Category getCategoryById(Connection con, long categoryId)
			throws SQLException {
		PreparedStatement pstmt = null;
		Category category = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_CATEGORY_BY_ID);
			pstmt.setLong(1, categoryId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				category = unMapCategory(rs);
				List<Long> employeesIdByCategoryId = getEmployeesIdByCategoryId(
						con, categoryId);
				category.setEmployeeIdList(employeesIdByCategoryId);
			}
			return category;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	private List<Long> getEmployeesIdByCategoryId(Connection con,
			long categoryId) throws SQLException {
		PreparedStatement pstmt = null;
		List<Long> employeesIdList = new ArrayList<Long>();
		try {
			pstmt = con.prepareStatement(SQL__GET_EMPLOYEES_ID_BY_CATEGORY_ID);
			pstmt.setLong(1, categoryId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				employeesIdList.add(rs
						.getLong(MapperParameters.CATEGORY_EMP__EMPLOYEE_ID));
			}
			return employeesIdList;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}

	private Category unMapCategory(ResultSet rs) throws SQLException {
		Category category = new Category();
		category.setCategoryId(rs.getLong(MapperParameters.CATEGORY__ID));
		category.setTitle(rs.getString(MapperParameters.CATEGORY__TITLE));
		return category;
	}

	@Override
	public long insertCategory(Category category) throws DAOException {
		long categoryId = 0;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			categoryId = insertCategory(category, con);
		} catch (SQLException e) {
			log.error("Can not insert category.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return categoryId;
	}

	private long insertCategory(Category category, Connection con)
			throws SQLException {
		long categoryId = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_CATEGORY,
					Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, category.getTitle());
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				categoryId = rs.getLong(1);
				deleteOrInsertEmployees(categoryId,
						category.getEmployeeIdList(),
						SQL__INSERT_EMPLOYEE_IN_CATEGORY, con);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return categoryId;
	}

	@Override
	public boolean insertCategories(Collection<Category> categories)
			throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertCategories(categories, con);
		} catch (SQLException e) {
			log.error("Can not insert categories.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean insertCategories(Collection<Category> categories,
			Connection con) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_CATEGORY,
					Statement.RETURN_GENERATED_KEYS);
			int inserted = 0;
			for (Category category : categories) {
				pstmt.setString(1, category.getTitle());
				inserted += pstmt.executeUpdate();
				ResultSet rs = pstmt.getGeneratedKeys();
				long id = 0;
				if (rs.next()) {
					id = rs.getLong(1);
					deleteOrInsertEmployees(id, category.getEmployeeIdList(),
							SQL__INSERT_EMPLOYEE_IN_CATEGORY, con);
				}

			}
			result = inserted == categories.size();
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean deleteCategory(long categoryId) throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = deleteCategory(categoryId, con);
		} catch (SQLException e) {
			log.error("Can not delete category.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean deleteCategory(long categoryId, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__DELETE_CATEGORY);
			pstmt.setLong(1, categoryId);
			result = pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean deleteCategories(Collection<Category> categories)
			throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = deleteCategories(categories, con);
		} catch (SQLException e) {
			log.error("Can not delete categories.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean deleteCategories(Collection<Category> categories,
			Connection con) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__DELETE_CATEGORY);
			for (Category c : categories) {
				pstmt.setLong(1, c.getCategoryId());
				pstmt.addBatch();
			}
			result = pstmt.executeBatch().length == categories.size();
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean insertEmployees(long idCategory, Collection<Long> employees)
			throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = deleteOrInsertEmployees(idCategory, employees,
					SQL__INSERT_EMPLOYEE_IN_CATEGORY, con);
		} catch (SQLException e) {
			log.error("Can not insert employees in category.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	@Override
	public boolean deleteEmployees(long idCategory, Collection<Long> employees)
			throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = deleteOrInsertEmployees(idCategory, employees,
					SQL__DELETE_EMPLOYEE_FROM_CATEGORY, con);
		} catch (SQLException e) {
			log.error("Can not delete employees in category.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean deleteOrInsertEmployees(long idCategory,
			Collection<Long> employees, String query, Connection con)
			throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(query);
			for (long id : employees) {
				pstmt.setLong(1, idCategory);
				pstmt.setLong(2, id);
				pstmt.addBatch();
			}
			result = pstmt.executeBatch().length == employees.size();
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean updateCategory(Category category) throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = updateCategory(con, category);
		} catch (SQLException e) {
			log.error("Can not update category.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean updateCategory(Connection con, Category category)
			throws SQLException {
		boolean result = false;
		try {
			result = updateCategoryTitle(con, category.getTitle(),
					category.getCategoryId());
			Category oldCategory = getCategoryById(con, category.getCategoryId());
			List<Long> newEmployeeIdList = (List<Long>) category
					.getEmployeeIdList();
			List<Long> oldEmployeeIdList = (List<Long>) oldCategory
					.getEmployeeIdList();
			List<Long> totalEmployeeIdList = new ArrayList<Long>();
			for (Long employeeId : newEmployeeIdList) {
				if (oldEmployeeIdList.contains(employeeId)) {
					totalEmployeeIdList.add(employeeId);
				}
			}
			newEmployeeIdList.removeAll(totalEmployeeIdList);
			oldEmployeeIdList.removeAll(totalEmployeeIdList);
			deleteOrInsertEmployees(category.getCategoryId(),
					newEmployeeIdList, SQL__INSERT_EMPLOYEE_IN_CATEGORY, con);
			deleteOrInsertEmployees(category.getCategoryId(),
					oldEmployeeIdList, SQL__DELETE_EMPLOYEE_FROM_CATEGORY, con);
		} catch (SQLException e) {
			throw e;
		}
		return result;
	}

	private boolean updateCategoryTitle(Connection con, String categoryTitle,
			long categoryId) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__UPDATE_CATEGORY_TITLE);
			pstmt.setString(1, categoryTitle);
			pstmt.setLong(2, categoryId);
			result = pstmt.executeUpdate() == 1;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean containsOtherCategoryWithTitle(String title, long categoryId) {
		Connection con = null;
		boolean result = false;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = containsOtherCategoryWithTitle(con, title, categoryId);
		} catch (SQLException e) {
			log.error("Can not get other category with title.", e);
		} finally {
			MSsqlDAOFactory.close(con);
		}
		return result;
	}

	private boolean containsOtherCategoryWithTitle(Connection con,
			String title, long categoryId) throws SQLException {
		PreparedStatement pstmt = null;
		boolean result = false;
		try {
			pstmt = con.prepareStatement(SQL__GET_OTHER_CATEGORY_WITH_TITLE);
			pstmt.setString(1, title);
			pstmt.setLong(2, categoryId);
			ResultSet rs = pstmt.executeQuery();
			result = rs.isBeforeFirst();
			return result;
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
	}
}
