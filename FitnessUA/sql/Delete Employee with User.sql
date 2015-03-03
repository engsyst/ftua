-- ================================================
-- Template generated from Template Explorer using:
-- Create Trigger (New Menu).SQL
--
-- Use the Specify Values for Template Parameters 
-- command (Ctrl-Shift-M) to fill in the parameter 
-- values below.
--
-- See additional Create Trigger templates for more
-- examples of different Trigger statements.
--
-- This block of comments will not be included in
-- the definition of the function.
-- ================================================
USE FitnessUA
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Engsyst>
-- Description:	<Removes >
-- =============================================
CREATE TRIGGER dbo.Employee_Update_IsDeleted 
   ON  dbo.Employee
   AFTER UPDATE
AS 
   DECLARE @uId int;
   DECLARE @cntEmpl int;
   DECLARE @cntCompl int;
   DECLARE @eId int;

-- Initialize the variable.
   SET NOCOUNT ON;
   SET @uId = 0;
   SET @eId = 0;
    IF ( UPDATE (IsDeleted) AND ((SELECT IsDeleted FROM inserted) = 1))
	BEGIN
		-- SET NOCOUNT ON added to prevent extra result sets from
		-- interfering with SELECT statements.
		--SET NOCOUNT ON;
		SELECT @eID = EmployeeId  FROM inserted;
        SELECT @cntEmpl = COUNT(*) FROM dbo.Assignment WHERE EmployeeId = @eId;
        if @cntEmpl = 0
        BEGIN
			DELETE FROM dbo.Employee WHERE EmployeeId = @eId;
        END
        
        SELECT @cntCompl = COUNT(*) FROM dbo.ComplianceEmployee WHERE OurEmployeeId = @eId;
        if @cntCompl > 0
        BEGIN
			DELETE FROM dbo.ComplianceEmployee WHERE OurEmployeeId = @eId;
        END
        
		SELECT @uId = UserId FROM dbo.EmployeeUserRole WHERE EmployeeId = @eId;
		DELETE FROM dbo.Client WHERE UserId = @uID;
		DELETE FROM dbo.EmployeeUserRole WHERE EmployeeId = @eId;
        DELETE FROM dbo.EmpPrefs WHERE EmployeeId = @eId;
	END
GO
