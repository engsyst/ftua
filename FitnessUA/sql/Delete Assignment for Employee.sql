delete FROM Assignment WHERE AssignmentId in (SELECT Assignment.AssignmentId
FROM         SchedulePeriod INNER JOIN
                      ScheduleClubDay ON SchedulePeriod.SchedulePeriodId = ScheduleClubDay.SchedulePeriodId INNER JOIN
                      Shifts ON ScheduleClubDay.ScheduleClubDayId = Shifts.ScheduleClubDayId INNER JOIN
                      Assignment INNER JOIN
                      Employee ON Assignment.EmployeeId = Employee.EmployeeId ON Shifts.ShiftId = Assignment.ShiftId
where Employee.EmployeeId = 4 and (SchedulePeriod.Status = 3 or SchedulePeriod.Status = 2))
      
GO


