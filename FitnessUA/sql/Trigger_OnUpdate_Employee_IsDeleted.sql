USE [FitnessUA]
GO

/****** Object:  Trigger [Employee_Update_IsDeleted]    Script Date: 03/01/2015 18:21:05 ******/
IF  EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[dbo].[Employee_Update_IsDeleted]'))
DROP TRIGGER [dbo].[Employee_Update_IsDeleted]
GO

USE [FitnessUA]
GO

/****** Object:  Trigger [dbo].[Employee_Update_IsDeleted]    Script Date: 03/01/2015 18:21:05 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Engsyst>
-- Description:	<Removes>
-- =============================================
CREATE TRIGGER [dbo].[Employee_Update_IsDeleted] 
	ON  [dbo].[Employee]
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
		SELECT @eID = EmployeeId  FROM inserted;
		SELECT @uId = UserId FROM dbo.EmployeeUserRole WHERE EmployeeId = @eId;
		DELETE FROM dbo.Client WHERE UserId = @uID;
		DELETE FROM dbo.EmployeeUserRole WHERE EmployeeId = @eId;
		DELETE FROM dbo.EmpPrefs WHERE EmployeeId = @eId;
		SELECT @cntEmpl = COUNT(*) FROM dbo.Assignment WHERE EmployeeId = @eId;
		if @cntEmpl = 0
		BEGIN
			DELETE FROM dbo.Employee WHERE EmployeeId = @eId;
		END
	END
GO
