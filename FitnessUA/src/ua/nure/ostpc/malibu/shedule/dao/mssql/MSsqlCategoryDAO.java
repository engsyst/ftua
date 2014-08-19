package ua.nure.ostpc.malibu.shedule.dao.mssql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ua.nure.ostpc.malibu.shedule.dao.CategoryDAO;
import ua.nure.ostpc.malibu.shedule.entity.Category;
import ua.nure.ostpc.malibu.shedule.parameter.MapperParameters;

public class MSsqlCategoryDAO implements CategoryDAO {
	private static final Logger log = Logger.getLogger(MSsqlCategoryDAO.class);

	private static final String SQL__GET_CATEGORIES_ID_WITH_EMPLOYEES = "SELECT DISTINCT Categories.CategoryId FROM Categories "
			+ "INNER JOIN CategoryEmp ON CategoryEmp.CategoryId=Categories.CategoryId;";
	private static final String SQL__GET_CATEGORY_BY_ID = "SELECT * FROM Categories WHERE CategoryId=?;";
	private static final String SQL__GET_EMPLOYEES_ID_BY_CATEGORY_ID = "SELECT EmployeeId FROM CategoryEmp WHERE CategoryId=?;";

	@Override
	public List<Category> getCategoriesWithEmployees() {
		Connection con = null;
		List<Category> categories = null;
		try {
			con = MSsqlDAOFactory.getConnection();
			categories = getCategoriesWithEmployees(con);
		} catch (SQLException e) {
			log.error("Can not get categories with employees.", e);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
		}
		return categories;
	}

	private List<Category> getCategoriesWithEmployees(Connection con)
			throws SQLException {
		Statement stmt = null;
		List<Category> categories = null;
		try {
			stmt = con.createStatement();
			ResultSet rs = stmt
					.executeQuery(SQL__GET_CATEGORIES_ID_WITH_EMPLOYEES);
			if (rs.isBeforeFirst()) {
				categories = new ArrayList<Category>();
			}
			while (rs.next()) {
				long categoryId = rs.getLong(MapperParameters.CATEGORY__ID);
				Category category = getCategoryById(con, categoryId);
				categories.add(category);
			}
			return categories;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
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
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				log.error("Can not close connection.", e);
			}
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
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
	}

	private List<Long> getEmployeesIdByCategoryId(Connection con,
			long categoryId) throws SQLException {
		PreparedStatement pstmt = null;
		List<Long> employeesIdList = null;
		try {
			pstmt = con.prepareStatement(SQL__GET_EMPLOYEES_ID_BY_CATEGORY_ID);
			pstmt.setLong(1, categoryId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isBeforeFirst()) {
				employeesIdList = new ArrayList<Long>();
			}
			if (rs.next()) {
				employeesIdList.add(rs
						.getLong(MapperParameters.CATEGORY_EMP__EMPLOYEE_ID));
			}
			return employeesIdList;
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
	}

	private Category unMapCategory(ResultSet rs) throws SQLException {
		Category category = new Category();
		category.setCategoryId(rs.getLong(MapperParameters.CATEGORY__ID));
		category.setTitle(rs.getString(MapperParameters.CATEGORY__TITLE));
		return category;
	}
}
