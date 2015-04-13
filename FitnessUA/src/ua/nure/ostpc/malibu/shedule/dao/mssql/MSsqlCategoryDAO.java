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
	public List<Category> getCategoriesWithEmployees() {
		Connection con = null;
		List<Category> categories = new ArrayList<Category>();
		try {
			con = MSsqlDAOFactory.getConnection();
			categories = getCategoriesWithEmployees(con);
		} catch (SQLException e) {
			log.error("Can not get categories with employees.", e);
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
	public boolean insertCategory(Collection<Category> categroies)
			throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = insertCategory(categroies, con);
		} catch (SQLException e) {
			log.error("Can not insert category.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean insertCategory(Collection<Category> categroies,
			Connection con) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__INSERT_CATEGORY,
					Statement.RETURN_GENERATED_KEYS);
			int inserted = 0;
			for (Category c : categroies) {
				pstmt.setString(1, c.getTitle());
				inserted += pstmt.executeUpdate();
				ResultSet rs = pstmt.getGeneratedKeys();
				long id = 0;
				if (rs.next()) {
					id = rs.getLong(1);
					deleteOrInsertEmployees(id, c.getEmployeeIdList(),
							SQL__INSERT_EMPLOYEE_IN_CATEGORY, con);
				}

			}
			result = inserted == categroies.size();
		} catch (SQLException e) {
			throw e;
		} finally {
			MSsqlDAOFactory.closeStatement(pstmt);
		}
		return result;
	}

	@Override
	public boolean deleteCategory(Collection<Category> categroies)
			throws DAOException {
		boolean result = false;
		Connection con = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			result = deleteCategory(categroies, con);
		} catch (SQLException e) {
			log.error("Can not delete category.", e);
			MSsqlDAOFactory.roolback(con);
		} finally {
			MSsqlDAOFactory.commitAndClose(con);
		}
		return result;
	}

	private boolean deleteCategory(Collection<Category> categroies,
			Connection con) throws SQLException {
		boolean result;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement(SQL__DELETE_CATEGORY);
			for (Category c : categroies) {
				pstmt.setLong(1, c.getCategoryId());
				pstmt.addBatch();
			}
			result = pstmt.executeBatch().length == categroies.size();
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
			Category oldCategory = getCategoryById(category.getCategoryId());
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
}
