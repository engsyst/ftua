USE [FitnessUA]
GO

/****** Object:  Trigger [Club_Update_IsDeleted]    Script Date: 03/03/2015 09:22:00 ******/
IF  EXISTS (SELECT * FROM sys.triggers WHERE object_id = OBJECT_ID(N'[dbo].[Club_Update_IsDeleted]'))
DROP TRIGGER [dbo].[Club_Update_IsDeleted]
GO

USE [FitnessUA]
GO

/****** Object:  Trigger [dbo].[Club_Update_IsDeleted]    Script Date: 03/03/2015 09:22:00 ******/
SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE TRIGGER [dbo].[Club_Update_IsDeleted] 
   ON  [dbo].[Club] 
   AFTER UPDATE
AS 
	DECLARE @cId int;
	DECLARE @cntSCD int;

	IF ( UPDATE (IsDeleted) AND ((SELECT IsDeleted FROM inserted) = 1))
	BEGIN
		-- SET NOCOUNT ON added to prevent extra result sets from
		-- interfering with SELECT statements.
		SET NOCOUNT ON;
		-- Insert statements for trigger here
		SELECT @cId = ClubId FROM inserted;
		SELECT @cntSCD = COUNT(*) FROM dbo.ScheduleClubDay WHERE dbo.ScheduleClubDay.ClubId = @cId;
		IF @cntSCD = 0
		BEGIN
			DELETE FROM dbo.Club WHERE dbo.Club.ClubID = @cId;
		END
	END

GO


