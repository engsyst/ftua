USE [FitnessUA]
GO

/****** Object:  View [dbo].[ActiveEmpWithUser]    Script Date: 02/27/2015 17:13:34 ******/
IF  EXISTS (SELECT * FROM sys.views WHERE object_id = OBJECT_ID(N'[dbo].[ActiveEmpWithUser]'))
DROP VIEW [dbo].[ActiveEmpWithUser]
GO

USE [FitnessUA]
GO

/****** Object:  View [dbo].[ActiveEmpWithUser]    Script Date: 02/27/2015 17:13:34 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

CREATE VIEW [dbo].[ActiveEmpWithUser] WITH SCHEMABINDING
AS
SELECT  DISTINCT   dbo.Employee.EmployeeId, dbo.Employee.Firstname, dbo.Employee.Secondname, dbo.Employee.Lastname, dbo.Employee.Birthday, 
                      dbo.Employee.Address, dbo.Employee.Passportint, dbo.Employee.Idint, dbo.Employee.CellPhone, dbo.Employee.WorkPhone, dbo.Employee.HomePhone, 
                      dbo.Employee.Email, dbo.Employee.Education, dbo.Employee.Notes, dbo.Employee.PassportIssuedBy, dbo.Employee.IsDeleted, dbo.Employee.Colour, 
                      dbo.Client.UserId, dbo.Client.Login, dbo.Client.PwdHache
FROM         dbo.Employee LEFT JOIN
                      dbo.EmployeeUserRole ON dbo.Employee.EmployeeId = dbo.EmployeeUserRole.EmployeeId LEFT JOIN
                      dbo.Client ON dbo.EmployeeUserRole.UserId = dbo.Client.UserId
WHERE
                      Employee.IsDeleted = 0


GO
