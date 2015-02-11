package ua.nure.ostpc.malibu.shedule.service;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import ua.nure.ostpc.malibu.shedule.dao.DAOFactory;
import ua.nure.ostpc.malibu.shedule.dao.ScheduleDAO;
import ua.nure.ostpc.malibu.shedule.entity.Club;
import ua.nure.ostpc.malibu.shedule.entity.ClubDaySchedule;
import ua.nure.ostpc.malibu.shedule.entity.Employee;
import ua.nure.ostpc.malibu.shedule.entity.Schedule;


public class ExcelService {
	private static final Logger log = Logger.getLogger(ExcelService.class);

	private static String [] dayOfWeek = {"Вс","Пн","Вт","Ср","Чт","Пт","Сб"};
	
	
	
	/**
	 * Convert Schedule object to excel document
	 * 
	 * @param shedule
	 *            - Object contains current schedule to convert
	 * @return byte [] - ByteArray of result Excel document. Returns null if
	 *         conversion fails.
	 */
	public static byte [] scheduleAllToExcelConvert(Schedule schedule) throws NullPointerException {
		
		if(schedule == null){throw new NullPointerException("Schedule object is null");}
		
		return 	scheduleUserToExcelConvert(schedule, null);
	}
		
	
	
	
	/**
	 * Convert Schedule object to excel document
	 * 
	 * @param shedule
	 *            - Object contains current schedule to convert
	 * @param user
	 *            - Employee. Excel document will be contains only schedule for
	 *            this user. If user = null - the excel document will be
	 *            contains schedule for all employees.
	 * @return byte [] - ByteArray of result Excel document. Returns null if
	 *         conversion fails.
	 */
	public static byte []  scheduleUserToExcelConvert(Schedule schedule, Employee user) throws NullPointerException {
		
		
		if(schedule == null){throw new NullPointerException("Schedule object is null");}
		
		WritableWorkbook workbook = null;
		ByteArrayOutputStream resultStream = null;
		try {
			resultStream = new ByteArrayOutputStream();
			workbook = Workbook. createWorkbook(resultStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		WritableSheet sheet = workbook.createSheet(schedule.getPeriod().getStartDate()+" - " +schedule.getPeriod().getEndDate(), 0);
		
		try {
			WritableCellFormat headerTextFormat = new WritableCellFormat();
			headerTextFormat.setAlignment(Alignment.CENTRE);
			headerTextFormat.setWrap(true);
			sheet.mergeCells(0, 0, 7, 0);
			sheet.mergeCells(0, 1, 7, 1);
			
			for(int coll = 0; coll <= 7; coll++){
				sheet.setColumnView(coll, 20);
			}
			
			String stateShedule = null;
			String headerShedule = null;
			
			if (user == null) {

				headerShedule = "График работы на период : "
						+ schedule.getPeriod().getStartDate() + " - "
						+ schedule.getPeriod().getEndDate();
			} else {
				headerShedule = "График работы на период : "
						+ schedule.getPeriod().getStartDate() + " - "
						+ schedule.getPeriod().getEndDate() + "( "
						+ user.getNameForSchedule() + " )";
			}
			
			switch (schedule.getStatus()) {
			case CLOSED:
				stateShedule = "Закрыт";
				break;
			case CURRENT:
				stateShedule = "Текущий";
				break;
			case DRAFT:
				stateShedule = "Черновик";
				break;
			case FUTURE:
				stateShedule = "Будущий";
				break;
			}
		
			Label label = new Label(0, 0, headerShedule, headerTextFormat);
			sheet.addCell(label);		
			label = new Label(0, 1, "Состояние : "+stateShedule, headerTextFormat);
			sheet.addCell(label);
			

			
			Map<Date, List<ClubDaySchedule>> listSchedule = schedule.getDayScheduleMap();
			
			Calendar calendar = Calendar.getInstance();
			List<Date> listDays=new ArrayList<Date>(listSchedule.keySet());
			java.util.Collections.sort(listDays);
			
			List<Club>  clubsList = (List<Club>) DAOFactory.getDAOFactory(DAOFactory.MSSQL).getClubDAO().getDependentClubs();
			
			
		//Check for start of week 
			
			calendar.setTime(new java.util.Date(listDays.get(0).getTime()));
			int firstDayNumber = calendar.get(Calendar.DAY_OF_WEEK) - 1;
			while(firstDayNumber > 1){
				calendar.add(Calendar.DATE, -1);
				listDays.add(0, new java.sql.Date(calendar.getTimeInMillis()));
				firstDayNumber --;
			}

		//Check for end of week 
			
			calendar.setTime(new java.util.Date(listDays.get(listDays.size()-1).getTime()));
			int lastDayNumber = calendar.get(Calendar.DAY_OF_WEEK) -1;
			while(lastDayNumber < 7){
				calendar.add(Calendar.DATE, 1);
				listDays.add(new java.sql.Date(calendar.getTimeInMillis()));
				lastDayNumber ++;
			}
		// Generate Excel document	
			
			int coll = 1;
			int row = 3;
			int numPages = listDays.size() / 7; 		// Generates pages per week
			for (int i = 0; i < numPages; i++) {
				
				label = new Label(0, row++, "Дата", headerTextFormat);
				sheet.addCell(label);
				label = new Label(0, row++, "День недели", headerTextFormat);
				sheet.addCell(label);

				for (Club club : clubsList) {
					label = new Label(0, row++, club.getTitle(), headerTextFormat);
					sheet.addCell(label);
					
				}
				row++;
			}
		
			int numDaysWeek = 7;
			coll = 1;
			row = 3;
			int page = 0;
			for(Date curDate : listDays){
				
				
				
				if(numDaysWeek == 0)
				{
					numDaysWeek = 7;
					coll = 1;
					page++;
					row = page * (clubsList.size() + 3) + 3;
					
				}
			
				calendar.setTime(curDate);
				label = new Label(coll, row++, curDate.toString(), headerTextFormat);
				sheet.addCell(label);
				label = new Label(coll, row++, dayOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1], headerTextFormat);
				sheet.addCell(label);
				
				List<ClubDaySchedule> daySchedule = listSchedule.get(curDate);
				if (daySchedule != null) {
					row = page * (clubsList.size() + 3) + 5;
					for (ClubDaySchedule curScheduleClub : daySchedule) {

						

						for (Club curClub : clubsList) {
							if (curClub.getTitle().equalsIgnoreCase(curScheduleClub.getClub().getTitle())) {
								StringBuffer names = new StringBuffer();

								for (Employee employee : curScheduleClub.getEmployees()) {
									
									if(user == null){
									names.append(employee.getNameForSchedule());
									names.append("\n");
									}
									else{
										if(user.getEmployeeId() == employee.getEmployeeId()){
											names.append(user.getNameForSchedule());
											names.append("\n");
										}
									}
								}

								label = new Label(coll, row, names.toString(), headerTextFormat);
								sheet.addCell(label);

							}
							row++;	
						
						}
						row = page * (clubsList.size() + 3) + 5;
					}
					
				} 
				
				row = page * (clubsList.size() + 3) + 3;
				
				coll++;
				numDaysWeek --;
			}
			
			workbook.write();
			workbook.close();
			byte [] res = resultStream.toByteArray();
			System.out.println(resultStream.size());
			
		
		return res;
		
		
		
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

		
	}
	
	public static void main(String[] args) throws IOException {
		
		// Test last generated schedule
		
	 ScheduleDAO daoShedule = DAOFactory.getDAOFactory(DAOFactory.MSSQL).getScheduleDAO();
	 Schedule testSchedule = daoShedule.getSchedule(daoShedule.getAllPeriods().get(daoShedule.getAllPeriods().size() - 1).getPeriodId());
	 
	byte[] excel = scheduleAllToExcelConvert(testSchedule);
	FileOutputStream fos = new FileOutputStream("Shedule.xls");
	fos.write(excel);
	fos.close();
	 
	
	}
	
	

}
