USE [FitnessUA]
GO

/****** Object:  View [dbo].[EmpWithUser]    Script Date: 02/26/2015 22:03:08 ******/
SET ANSI_NULLS ON
GO

CREATE VIEW dbo.EmpWithUserRoles WITH SCHEMABINDING
AS
SELECT DISTINCT 
                      Employee.*, 
                      Client.*, Role.*
FROM         dbo.Employee INNER JOIN
                      dbo.EmployeeUserRole ON Employee.EmployeeId = EmployeeUserRole.EmployeeId INNER JOIN
                      Client ON EmployeeUserRole.UserId = Client.UserId INNER JOIN
                      dbo.Role ON EmployeeUserRole.RoleId = Role.RoleId
WHERE     (Employee.IsDeleted = 0)

GO
