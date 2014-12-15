package ua.nure.ostpc.malibu.shedule.service;

import org.apache.log4j.Logger;
/*
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;


import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ua.nure.ostpc.malibu.shedule.dao.mssql.MSsqlDAOFactory;
import ua.nure.ostpc.malibu.shedule.entity.AssignmentExcel;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.Period;
*/
public class ExcelService {
	private static final Logger log = Logger.getLogger(ExcelService.class);
/*
	@SuppressWarnings({ "unchecked" })
	public String pushToExcel(Period period) throws SQLException,
			RowsExceededException, WriteException, IOException {
		Statement st = null;
		Connection con = null;

		@SuppressWarnings("rawtypes")
		Set<Club> clubs = new HashSet();
		try {
			if (log.isDebugEnabled())
				log.debug("Try pushToExcel");
			con = MSsqlDAOFactory.getConnection();
			st = con.createStatement();
			java.sql.ResultSet resSet = st
					.executeQuery(String
							.format("SELECT DISTINCT cl.Title  , cl.QuantityOfPeople from [Assignment] ass , Club cl where ass.ClubId=cl.ClubId and  SchedulePeriodId =  "
									+ period.getPeriodId() + ";"));
			while (resSet.next()) {
				Club tempClub = new Club();
				tempClub.setTitle((resSet.getString("Title")));
				clubs.add(tempClub);
			}
		} catch (SQLException e) {
			log.error("Can not select club id.", e);
		} finally {
			if (st != null) {
				try {
					st.close();
				} catch (SQLException e) {
					log.error("Can not close statement.", e);
				}
			}
		}
		Locale local = new Locale("ru", "RU");
		java.util.Date StartDate = period.getStartDate();
		java.util.Date EndDate = period.getEndDate();
		int PeriodDuration = period.getDurationDays();
		GregorianCalendar calenStart = new GregorianCalendar();
		GregorianCalendar calenEnd = new GregorianCalendar();
		GregorianCalendar calenCurrent = new GregorianCalendar();

		calenStart.setTime(StartDate);
		calenEnd.setTime(EndDate);
		calenCurrent.setTime(StartDate);

		// creating Excell
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		dateFormatter = new SimpleDateFormat("dd-MM-yy");

		String nameOfTheSheduleFile = "c:/temp/Shedule "
				+ dateFormatter.format(StartDate) + " to "
				+ dateFormatter.format(calenEnd.getTime()) + ".xls";
		WritableWorkbook wb = Workbook.createWorkbook(new File(
				nameOfTheSheduleFile));
		WritableSheet sheet = wb.createSheet("list  1", 0);
		String ourDate = null;

		for (int i = 1; i <= (PeriodDuration + 1); i++) {

			ourDate = calenCurrent.getDisplayName(Calendar.DAY_OF_WEEK, 2,
					local)
					+ " "
					+ calenCurrent.get(Calendar.DAY_OF_MONTH)
					+ "  "
					+ calenCurrent.getDisplayName(Calendar.MONTH, 2, local)
					+ "  " + calenCurrent.get(Calendar.YEAR);

			sheet.setColumnView(i + 1, 30);
			sheet.addCell(new Label(i + 1, 0, ourDate));
			calenCurrent.add(Calendar.DATE, 1);

		}
		System.out.println("goods");
		Set<AssignmentExcel> assignmentExcel = assignmentExcelDAO
				.selectAssignmentsExcel(con, period);
		Iterator<AssignmentExcel> iterAssExcel = assignmentExcel.iterator();

		calenCurrent.setTime(StartDate);

		SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
		while (iterAssExcel.hasNext()) {
			AssignmentExcel assignment = iterAssExcel.next();
			int columnNumber = 0;
			int rownNumber = 0;

			for (int i = 1; i <= (PeriodDuration + 1); i++) {
				if (dateFormatter1.format(assignment.getDate()).equals(
						dateFormatter1.format(calenCurrent.getTime()))) {
					columnNumber = (i + 1);
					break;

				}
				calenCurrent.add(Calendar.DATE, 1);
			}
			calenCurrent.setTime(StartDate);

			for (int j = 1; j < sheet.getRows() + 2; j++) {

				if (assignment.getClubTitle().equals(
						sheet.getCell(0, j).getContents())) {

					rownNumber = j;
					if (assignment.getHalfOfDay() == 1) {
						for (int k = 0; k < assignment.getQuantityOfPeople(); k++) {
							if (sheet.getCell(columnNumber, rownNumber + k)
									.getContents().isEmpty()) {
								sheet.addCell(new Label(columnNumber,
										rownNumber + k, assignment.getName(),
										getCellFormat(new Colour(assignment
												.getColour(), "2323", 1, 1, 1) {
										})));
								break;
							}
						}
						break;
					} else {
						rownNumber += assignment.getQuantityOfPeople();
						for (int k = 0; k < assignment.getQuantityOfPeople(); k++) {
							if (sheet.getCell(columnNumber, rownNumber + k)
									.getContents().isEmpty()) {
								sheet.addCell(new Label(columnNumber,
										rownNumber + k, assignment.getName(),
										getCellFormat((Colour.BRIGHT_GREEN))));
								break;
							}
						}
						break;
					}
				}

			}

		}

		sheet.removeColumn(0);
		sheet.removeColumn(0);
		sheet.insertColumn(0);
		sheet.insertColumn(0);
		sheet.addCell(new Label(0, 0, "Р�РјСЏ РєР»СѓР±Р° / Р”Р°С‚Р° "));
		sheet.addCell(new Label(1, 0, "РџРѕР»РѕРІРёРЅР° РґРЅСЏ"));
		sheet.setColumnView(0, 20);
		sheet.setColumnView(1, 20);

		wb.write();
		wb.close();
		return nameOfTheSheduleFile;
	}

	private static WritableCellFormat getCellFormat(Colour colour)
			throws WriteException {
		WritableFont cellFont = new WritableFont(WritableFont.TIMES, 12);
		WritableCellFormat cellFormat = new WritableCellFormat(cellFont);
		cellFormat.setBackground(colour);
		return cellFormat;
	}
*/
}
