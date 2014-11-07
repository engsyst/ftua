use FitnessUA;
if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_ASSIGNMENT')
          and type = 'TR')
   drop trigger CLR_TRIGGER_ASSIGNMENT
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_CLUBPREFS')
          and type = 'TR')
   drop trigger CLR_TRIGGER_CLUBPREFS
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_CLUB')
          and type = 'TR')
   drop trigger CLR_TRIGGER_CLUB
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_EMPPREFS')
          and type = 'TR')
   drop trigger CLR_TRIGGER_EMPPREFS
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_EMPLOYEE_ASSIGNMENT')
          and type = 'TR')
   drop trigger CLR_TRIGGER_EMPLOYEE_ASSIGNMENT
go

if exists (select 1
          from sysobjects
          where id = object_id('TU_EMPLOYEE_ASSIGNMENT')
          and type = 'TR')
   drop trigger TU_EMPLOYEE_ASSIGNMENT
go

if exists (select 1
          from sysobjects
          where id = object_id('TI_EMPLOYEE_ASSIGNMENT')
          and type = 'TR')
   drop trigger TI_EMPLOYEE_ASSIGNMENT
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_Employee')
          and type = 'TR')
   drop trigger CLR_TRIGGER_Employee
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_ROLE')
          and type = 'TR')
   drop trigger CLR_TRIGGER_ROLE
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_SCHEDULEPERIOD')
          and type = 'TR')
   drop trigger CLR_TRIGGER_SCHEDULEPERIOD
go

if exists (select 1
          from sysobjects
          where id = object_id('TI_SCHEDULEPERIOD')
          and type = 'TR')
   drop trigger TI_SCHEDULEPERIOD
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_USER')
          and type = 'TR')
   drop trigger CLR_TRIGGER_USER
go

if exists (select 1
          from sysobjects
          where id = object_id('TU_USER')
          and type = 'TR')
   drop trigger TU_USER
go

if exists (select 1
          from sysobjects
          where id = object_id('TI_USER')
          and type = 'TR')
   drop trigger TI_USER
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('ClubPrefs') and o.name = 'FK_CLUBPREF_REFERENCE_EMPLOYEE')
alter table ClubPrefs
   drop constraint FK_CLUBPREF_REFERENCE_EMPLOYEE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('ClubPrefs') and o.name = 'FK_CLUBPREF_REFERENCE_SCHEDULE')
alter table ClubPrefs
   drop constraint FK_CLUBPREF_REFERENCE_SCHEDULE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('ClubPrefs') and o.name = 'FK_CLUBPREF_REFERENCE_CLUB')
alter table ClubPrefs
   drop constraint FK_CLUBPREF_REFERENCE_CLUB
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('EmpPrefs') and o.name = 'FK_EMPPREFS_REFERENCE_EMPLOYEE')
alter table EmpPrefs
   drop constraint FK_EMPPREFS_REFERENCE_EMPLOYEE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('SchedulePeriod') and o.name = 'FK_SCHEDULE_REFERENCE_SCHEDULE')
alter table SchedulePeriod
   drop constraint FK_SCHEDULE_REFERENCE_SCHEDULE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('Client') and o.name = 'FK_CLIENT_REFERENCE_ROLE')
alter table Client
   drop constraint FK_CLIENT_REFERENCE_ROLE
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Holidays')
            and   type = 'U')
   drop table Holidays
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('ClubPrefs')
            and   name  = 'XIFEMPLOYEE'
            and   indid > 0
            and   indid < 255)
   drop index ClubPrefs.XIFEMPLOYEE
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('ClubPrefs')
            and   name  = 'XIFSHEDULEPERIOD'
            and   indid > 0
            and   indid < 255)
   drop index ClubPrefs.XIFSHEDULEPERIOD
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('ClubPrefs')
            and   name  = 'XIFCLUB'
            and   indid > 0
            and   indid < 255)
   drop index ClubPrefs.XIFCLUB
go

if exists (select 1
            from  sysobjects
           where  id = object_id('ClubPrefs')
            and   type = 'U')
   drop table ClubPrefs
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Club')
            and   type = 'U')
   drop table Club
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('EmpPrefs')
            and   name  = 'XIFEMLOYEES'
            and   indid > 0
            and   indid < 255)
   drop index EmpPrefs.XIFEMLOYEES
go

if exists (select 1
            from  sysobjects
           where  id = object_id('EmpPrefs')
            and   type = 'U')
   drop table EmpPrefs
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Employee')
            and   type = 'U')
   drop table Employee
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Role')
            and   type = 'U')
   drop table Role
go

if exists (select 1
            from  sysobjects
           where  id = object_id('SchedulePeriod')
            and   type = 'U')
   drop table SchedulePeriod
go

if exists (select 1
            from  sysobjects
           where  id = object_id('ComplianceClub')
            and   type = 'U')
   drop table ComplianceClub
go

if exists (select 1
            from  sysobjects
           where  id = object_id('ComplianceEmployee')
            and   type = 'U')
   drop table ComplianceEmployee
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Client')
            and   name  = 'XIFLOGIN'
            and   indid > 0
            and   indid < 255)
   drop index Client.XIFLOGIN
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Client')
            and   type = 'U')
   drop table Client
go

if exists(select 1 from systypes where name='MAXDAYS')
   execute sp_unbindrule MAXDAYS
go

if exists(select 1 from systypes where name='MAXDAYS')
   drop type MAXDAYS
go

if exists(select 1 from systypes where name='MINDAYS')
   execute sp_unbindrule MINDAYS
go

if exists(select 1 from systypes where name='MINDAYS')
   drop type MINDAYS
go

if exists (select 1
   from  sysobjects where type = 'D'
   and name = 'DEFAULT_MAXDAYS'
   )
   drop default DEFAULT_MAXDAYS
go

if exists (select 1
   from  sysobjects where type = 'D'
   and name = 'ZERO'
   )
   drop default ZERO
go

if exists (select 1 from sysobjects where id=object_id('R_MAXDAYS') and type='R')
   drop rule  R_MAXDAYS
go

if exists (select 1 from sysobjects where id=object_id('R_MINDAYS') and type='R')
   drop rule  R_MINDAYS
go

if exists (select 1 from sysobjects where id=object_id('VALIDATION_EMPPREFS') and type='R')
   drop rule  VALIDATION_EMPPREFS
go

if exists (select 1 from sysobjects where id=object_id('VALIDATION_PERIOD') and type='R')
   drop rule  VALIDATION_PERIOD
go

create rule R_MAXDAYS as
      @column between 0 and 7
go

create rule R_MINDAYS as
      @column between 0 and 7
go

/*==============================================================*/
/* Default: DEFAULT_MAXDAYS                                     */
/*==============================================================*/
create default DEFAULT_MAXDAYS
    as 7
go

/*==============================================================*/
/* Default: ZERO                                                */
/*==============================================================*/
create default ZERO
    as 0
go

/*==============================================================*/
/* Domain: MAXDAYS                                              */
/*==============================================================*/
create type MAXDAYS
   from int
go

execute sp_bindrule R_MAXDAYS, MAXDAYS
go

execute sp_bindefault DEFAULT_MAXDAYS, 'MAXDAYS'
go

/*==============================================================*/
/* Domain: MINDAYS                                              */
/*==============================================================*/
create type MINDAYS
   from int
go

execute sp_bindrule R_MINDAYS, MINDAYS
go

execute sp_bindefault ZERO, 'MINDAYS'
go

/*==============================================================*/
/* Table: ClubPrefs                                             */
/*==============================================================*/
create table ClubPrefs (
   ClubPrefsId          int                  identity not null,
   ClubId               int                  not null,
   SchedulePeriodId     int                  not null,
   EmployeeId           int                  not null,
   constraint PK_CLUBPREFS primary key nonclustered (ClubPrefsId)
)
go

/*==============================================================*/
/* Index: XIFCLUB                                               */
/*==============================================================*/
create index XIFCLUB on ClubPrefs (
ClubId ASC
)
go

/*==============================================================*/
/* Index: XIFSHEDULEPERIOD                                      */
/*==============================================================*/
create index XIFSHEDULEPERIOD on ClubPrefs (
SchedulePeriodId ASC
)
go

/*==============================================================*/
/* Index: XIFEMPLOYEE                                           */
/*==============================================================*/
create index XIFEMPLOYEE on ClubPrefs (
EmployeeId ASC
)
go

/*==============================================================*/
/* Table: Club                                                 */
/*==============================================================*/
create table Club (
   ClubId               int                  identity not null,
   Title                nvarchar(256)        not null,
   IsIndependent        bit                  not null default 0,
   constraint PK_CLUB primary key (ClubId)
)
go

/*==============================================================*/
/* Table: EmpPrefs                                              */
/*==============================================================*/
create table EmpPrefs (
   EmpPrefsId           int               identity not null,
   EmployeeId           int                  not null,
   MinDays              int                  not null
      constraint CKC_MINDAYS_EMPPREFS check (MinDays between 0 and 7),
   MaxDays              int                  not null
      constraint CKC_MAXDAYS_EMPPREFS check (MaxDays between 0 and 7),
      CONSTRAINT equalConstr CHECK([MaxDays]>=[MinDays]),
   constraint PK_EMPPREFS primary key nonclustered (EmpPrefsId)
)
go

execute sp_bindefault ZERO, 'EmpPrefs.MinDays'
go

execute sp_bindefault DEFAULT_MAXDAYS, 'EmpPrefs.MaxDays'
go

/*==============================================================*/
/* Index: XIFEMLOYEES                                           */
/*==============================================================*/
create unique index XIFEMLOYEES on EmpPrefs (
EmployeeId ASC
)
go

/*==============================================================*/
/* Table: Employee                                             */
/*==============================================================*/
create table Employee (
   EmployeeId           int                  identity not null,
   Firstname            nvarchar(256)        not null,
   Secondname           nvarchar(256)        not null,
   Lastname             nvarchar(256)        not null,
   Birthday             date                 not null,
   Address              nvarchar(max)        not null,
   Passportint			nvarchar(16)         not null,
   Idint				nvarchar(32)         not null,
   CellPhone            nvarchar(32)         not null,
   WorkPhone            nvarchar(32)         null,
   HomePhone            nvarchar(32)         null,
   Email                nvarchar(256)        not null,
   Education            nvarchar(1024)       null,
   Notes                nvarchar(max)        null,
   PassportIssuedBy     nvarchar(1024)       null,
   Colour				int					 null default 9,
   constraint PK_Employee primary key (EmployeeId)
)
go

/*==============================================================*/
/* Table: ComplianceClub                                        */
/*==============================================================*/
CREATE TABLE ComplianceClub(
	ComplianceClubId	INT				PRIMARY KEY IDENTITY(1, 1) NOT NULL,
	OriginalClubId		INT				NOT NULL REFERENCES Clubs(ClubId) ON DELETE CASCADE ON UPDATE CASCADE,
	OurClubID			INT				NOT NULL REFERENCES Club(ClubId) ON DELETE CASCADE ON UPDATE CASCADE
)
go

/*==============================================================*/
/* Table: ComplianceEmployee                                    */
/*==============================================================*/
CREATE TABLE ComplianceEmployee(
	ComplianceEmployeeId	INT				PRIMARY KEY IDENTITY(1, 1) NOT NULL,
	OriginalEmployeeId		INT				NOT NULL REFERENCES Employees(EmployeeId) ON DELETE CASCADE ON UPDATE CASCADE,
	OurEmployeeId			INT				NOT NULL REFERENCES Employee(EmployeeId) ON DELETE CASCADE ON UPDATE CASCADE
)
go

/*==============================================================*/
/* Table: Role                                                  */
/*==============================================================*/
create table Role (
   RoleId               int               identity not null,
   Rights               int               null,
   Title                nvarchar(20)      null,
   constraint PK_ROLE primary key nonclustered (RoleId)
)
go

/*==============================================================*/
/* Table: SchedulePeriod                                        */
/*==============================================================*/
create table SchedulePeriod (
   SchedulePeriodId     INT                 IDENTITY(1,1) NOT NULL,
   LastPeriodId         INT                 NULL,
   StartDate            DATE                NOT NULL,
   EndDate              DATE                NOT NULL,
   Status				INT					NOT NULL,
   constraint PK_SCHEDULEPERIOD primary key nonclustered (SchedulePeriodId)
)
go

/*==============================================================*/
/* Table: ScheduleClubDay                                       */
/*==============================================================*/
CREATE TABLE ScheduleClubDay (
	ScheduleClubDayId	INT		PRIMARY KEY IDENTITY(1, 1) NOT NULL,
	Date				DATE	NOT NULL,
	SchedulePeriodId	INT		NOT NULL REFERENCES SchedulePeriod(SchedulePeriodId) ON DELETE CASCADE ON UPDATE CASCADE,
	ClubId				INT		NOT NULL REFERENCES Club(ClubId) ON DELETE CASCADE ON UPDATE CASCADE,	
	ShiftsNumber		INT		NOT NULL,
	WorkHoursInDay		INT		NOT NULL CHECK(WorkHoursInDay>0 AND WorkHoursInDay<=24)
)
go

/*==============================================================*/
/* Table: Shifts                                                */
/*==============================================================*/
CREATE TABLE Shifts (
	ShiftId				INT		PRIMARY KEY IDENTITY(1, 1) NOT NULL,
	ScheduleClubDayId	INT		NOT NULL REFERENCES ScheduleClubDay(ScheduleClubDayId) ON DELETE CASCADE ON UPDATE CASCADE,
	ShiftNumber			INT		NOT NULL,	
	QuantityOfEmp		INT		NOT NULL CHECK(QuantityOfEmp>0)
)
go

/*==============================================================*/
/* Table: Assignment                                            */
/*==============================================================*/
CREATE TABLE Assignment (
	AssignmentId		INT		PRIMARY KEY IDENTITY(1, 1) NOT NULL,
	ShiftId				INT		NOT NULL REFERENCES Shifts(ShiftId) ON DELETE CASCADE ON UPDATE CASCADE,
	EmployeeId			INT		NOT NULL REFERENCES Employee(EmployeeId) ON DELETE CASCADE ON UPDATE CASCADE
)
go

/*==============================================================*/
/* Table: Client                                                */
/*==============================================================*/
create table Client (
   UserId               int	                 identity not null,
   PwdHache             NVARCHAR(128)        not null,
   Login                nvarchar(20)         not null,
   constraint PK_USER primary key nonclustered (UserId)
)
go

/*==============================================================*/
/* Table: EmployeeUserRole                                      */
/*==============================================================*/
create table EmployeeUserRole (
   EmployeeUserRoleId	int  IDENTITY NOT NULL,
   EmployeeId           int  NOT NULL REFERENCES Employee(EmployeeId) ON DELETE CASCADE ON UPDATE CASCADE,
   UserId           	int  NULL REFERENCES Client(UserId),
   RoleId           	int  NOT NULL REFERENCES Role(RoleId) ON DELETE CASCADE ON UPDATE CASCADE,
   constraint PK_EMPLOYEE_CLIENT_ROLE primary key nonclustered (EmployeeUserRoleId)
)
go

/*==============================================================*/
/* Table: Holidays                                              */
/*==============================================================*/
CREATE TABLE Holidays (
	Holidayid		INT			PRIMARY KEY identity (1, 1) not null,
	Date			DATETIME	not null unique,
	Repeate			INT			not null
)
go

/*==============================================================*/
/* Table: Prefs                                                 */
/*==============================================================*/
CREATE TABLE Prefs (
	PrefId				INT		PRIMARY KEY IDENTITY(1, 1) NOT NULL,
	ShiftsNumber		INT		NOT NULL,
	WorkHoursInDay		INT		NOT NULL CHECK(WorkHoursInDay>0 AND WorkHoursInDay<=24)
)
go

/*==============================================================*/
/* Table: Categories                                            */
/*==============================================================*/
CREATE TABLE Categories (
	CategoryId			INT				PRIMARY KEY IDENTITY(1, 1) NOT NULL,
	Title				NVARCHAR(64)	NOT NULL UNIQUE
)
go

/*==============================================================*/
/* Table: CategoryEmp                                           */
/*==============================================================*/
CREATE TABLE CategoryEmp (
	CategoryEmpId		INT				PRIMARY KEY IDENTITY(1, 1) NOT NULL,
	CategoryId			INT				NOT NULL REFERENCES Categories(CategoryId) ON DELETE CASCADE ON UPDATE CASCADE,
	EmployeeId			INT				NOT NULL REFERENCES Employee(EmployeeId) ON DELETE CASCADE ON UPDATE CASCADE
)
go

/*==============================================================*/
/* Table: Log                                                   */
/*==============================================================*/
CREATE TABLE Log (
	LogId				INT				PRIMARY KEY IDENTITY(1, 1) NOT NULL,
	Date				DATETIME		NOT NULL,
	EmployeeId			INT				NULL,
	Message				NVARCHAR(1000)	NOT NULL
)
go

/*==============================================================*/
/* Index: XIFLOGIN                                              */
/*==============================================================*/
create index XIFLOGIN on Client (
Login ASC
)
go

alter table ClubPrefs
   add constraint FK_CLUBPREF_REFERENCE_EMPLOYEE foreign key (EmployeeId)
      references Employee (EmployeeId)
go

alter table ClubPrefs
   add constraint FK_CLUBPREF_REFERENCE_SCHEDULE foreign key (SchedulePeriodId)
      references SchedulePeriod (SchedulePeriodId)
         on delete cascade
go

alter table ClubPrefs
   add constraint FK_CLUBPREF_REFERENCE_CLUB foreign key (ClubId)
      references Club (ClubId)
go

alter table EmpPrefs
   add constraint FK_EMPPREFS_REFERENCE_EMPLOYEE foreign key (EmployeeId)
      references Employee (EmployeeId)
go

alter table SchedulePeriod
   add constraint FK_SCHEDULE_REFERENCE_SCHEDULE foreign key (LastPeriodId)
      references SchedulePeriod (SchedulePeriodId)
go

ALTER TABLE [dbo].[Prefs]  WITH CHECK ADD  CONSTRAINT [CK_Prefs] CHECK  (([WorkHoursInDay]>(0) AND [WorkHoursInDay]<=(24)))
GO

ALTER TABLE [dbo].[Prefs] CHECK CONSTRAINT [CK_Prefs]
GO

create trigger TI_SCHEDULEPERIOD on SchedulePeriod for insert as
begin
    declare
       @maxcard  int,
       @numrows  int,
       @numnull  int,
       @errno    int,
       @errmsg   varchar(255)

    select  @numrows = @@rowcount
    if @numrows = 0
       return

    /*  The cardinality of Parent "SchedulePeriod" in child "SchedulePeriod" cannot exceed 1 */
    if update(LastPeriodId)
    begin
       select @maxcard = (select count(*)
          from   SchedulePeriod old
          where ins.LastPeriodId = old.LastPeriodId)
       from  inserted ins
       where ins.LastPeriodId is not null
       group by ins.LastPeriodId
       if @maxcard > 1
       begin
          select @errno  = 50007,
                 @errmsg = 'The maximum cardinality of a child has been exceeded! Cannot create child in "SchedulePeriod".'
          goto error
       end
    end

    return

/*  Errors handling  */
error:
    rollback  transaction
end
go

CREATE TRIGGER cascade_user_deletion
ON EmployeeUserRole AFTER DELETE
AS
IF @@ROWCOUNT>0
BEGIN
	DECLARE @userId INT, @count INT
	DECLARE c CURSOR FOR SELECT UserId FROM deleted;
	OPEN c
	FETCH NEXT FROM c INTO @userId
	WHILE @@FETCH_STATUS = 0
	BEGIN
		SELECT @count=COUNT(UserId) FROM EmployeeUserRole WHERE UserId=@userId;
		IF (@userId IS NOT NULL AND @count=0)
		BEGIN
			DELETE FROM Client WHERE UserId=@userId;
		END;
		FETCH NEXT FROM c INTO @userId
	END;
	CLOSE c
	DEALLOCATE c
END;
GO

CREATE TRIGGER log_table_limit
ON Log AFTER INSERT
AS
    DECLARE @tableCount int
    SELECT @tableCount = Count(*)
    FROM Log

    IF @tableCount > 500000
    BEGIN
        DELETE FROM Log WHERE LogId IN (SELECT TOP 50000 LogId FROM Log ORDER BY Date ASC);
    END
GO

INSERT INTO Club(Title, IsIndependent) VALUES('Бавария', 0);
INSERT INTO Club(Title, IsIndependent) VALUES('Маршала Жукова', 0);
INSERT INTO Club(Title, IsIndependent) VALUES('Смольная', 1);
INSERT INTO Club(Title, IsIndependent) VALUES('Юбилейный', 0);
INSERT INTO Club(Title, IsIndependent) VALUES('Простоквашино', 0);

INSERT INTO SchedulePeriod(StartDate, EndDate, Status) VALUES('20141011', '20141014', 2);
INSERT INTO SchedulePeriod(StartDate, EndDate, LastPeriodId, Status) VALUES('20141015', '20141019', 1, 0);
INSERT INTO SchedulePeriod(StartDate, EndDate, LastPeriodId, Status) VALUES('20141020', '20141029', 2, 0);

INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141015', 2, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141016', 2, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141017', 2, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141018', 2, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141019', 2, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141015', 2, 2, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141016', 2, 2, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141017', 2, 2, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141018', 2, 2, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141019', 2, 2, 3, 12);

INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141020', 3, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141021', 3, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141022', 3, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141023', 3, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141024', 3, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141025', 3, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141026', 3, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141027', 3, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141028', 3, 1, 3, 12);
INSERT INTO ScheduleClubDay(Date, SchedulePeriodId, ClubId, ShiftsNumber, WorkHoursInDay) VALUES('20141029', 3, 1, 3, 12);

INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(1, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(1, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(1, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(2, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(2, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(2, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(3, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(3, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(3, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(4, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(4, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(4, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(5, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(5, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(5, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(6, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(6, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(6, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(7, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(7, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(7, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(8, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(8, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(8, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(9, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(9, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(9, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(10, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(10, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(10, 3, 1);

INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(11, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(11, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(11, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(12, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(12, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(12, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(13, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(13, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(13, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(14, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(14, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(14, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(15, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(15, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(15, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(16, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(16, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(16, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(17, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(17, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(17, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(18, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(18, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(18, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(19, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(19, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(19, 3, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(20, 1, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(20, 2, 1);
INSERT INTO Shifts(ScheduleClubDayId, ShiftNumber, QuantityOfEmp) VALUES(20, 3, 1);

INSERT INTO Role(Rights, Title) VALUES(0, 'responsible person');
INSERT INTO Role(Rights, Title) VALUES(1, 'admin');
INSERT INTO Role(Rights, Title) VALUES(2, 'subscriber');

INSERT INTO Employee(Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy,Colour)
VALUES('Иван', 'Петрович', 'Корнилов', '19801210', 'Kharkiv Ivanova str. 5', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'kornilov@mailinator.com', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006',9);
INSERT INTO Employee(Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy,Colour)
VALUES('Дмитрий', 'Иванович', 'Денисов', '19841010', 'Kharkiv Ivanova str. 4', 'MH083456', '2234567890123456', 
'0919145123', '0574641234', '0578723456', 'denisov@mailinator.com', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006',9);
INSERT INTO Employee(Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy,Colour)
VALUES('Корней', 'Степанович', 'Чуковский', '19941011', 'Kharkiv Repina str. 5', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'chookovsky@mailinator.com', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006',9);
INSERT INTO Employee(Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy,Colour)
VALUES('Сергей', 'Тихонович', 'Васильев', '19921210', 'Kharkiv Plotnykova str. 5', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'vasiiev@mailinator.com', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Печенежский ГУ МВД в Харьковской области 19.05.2006',9);
INSERT INTO Employee(Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy,Colour)
VALUES('Ринат', 'Абдулович', 'Чиканов', '19901210', 'Kharkiv Kirova str. 67', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'chikanov@mailinator.com', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006',9);
INSERT INTO Employee(Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy,Colour)
VALUES('Виталий', 'Фёдорович', 'Деникин', '19781210', 'Kharkiv Linea str. 15', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'denikin@mailinator.com', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Ленинский ГУ МВД в Харьковской области 19.05.2006',9);
INSERT INTO Employee(Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy,Colour)
VALUES('Олег', 'Васильевич', 'Киров', '19860210', 'Kharkiv Ivanova str. 125', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'kirov@mailinator.com', 'KNURE master', 'Some note 1. Some note 2. Some note 3', 'Московский ГУ МВД в Харьковской области 19.05.2006',9);
INSERT INTO Employee(Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy,Colour)
VALUES('Дмитрий', 'Владимирович', 'Леонов', '19901210', 'Kharkiv Franko str. 3', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'leonov@mailinator.com', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 11.03.2005',9);
INSERT INTO Employee(Firstname, Secondname, Lastname, Birthday, Address, Passportint, Idint, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy,Colour)
VALUES('Павел', 'Дмитриевич', 'Никонов', '19901210', 'Donetsk Shevchenka str. 15', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'nikonov@mailinator.com', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 09.11.2007',9);

INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(1, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(2, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(3, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(4, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(5, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(6, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(7, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(8, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(9, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(10, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(11, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(12, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(13, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(14, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(15, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(16, 9);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(17, 5);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(18, 6);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(19, 9);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(20, 5);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(21, 6);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(22, 9);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(23, 5);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(24, 6);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(25, 9);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(26, 5);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(27, 6);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(28, 9);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(29, 5);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(30, 6);

INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(31, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(32, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(33, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(34, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(35, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(36, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(37, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(38, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(39, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(40, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(41, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(42, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(43, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(44, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(45, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(46, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(47, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(48, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(49, 1);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(50, 2);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(51, 3);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(52, 9);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(53, 5);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(54, 6);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(55, 9);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(56, 5);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(57, 6);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(58, 9);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(59, 5);
INSERT INTO Assignment(ShiftId, EmployeeId) VALUES(60, 6);

INSERT INTO Client(PwdHache, Login) VALUES('d47ada80c500beb9c5d2c323842162c9', 'One');
INSERT INTO Client(PwdHache, Login) VALUES('35a4dece636e8d02f67ed9275778699e', 'Two');
INSERT INTO Client(PwdHache, Login) VALUES('97c72437698dc1afadfb16ed723200d7', 'Three');
INSERT INTO Client(PwdHache, Login) VALUES('7ab6c177dbe8d7a8955d085deccb28c8', 'Four');
INSERT INTO Client(PwdHache, Login) VALUES('ecf4807731135ce784c9f0a363b8ca8a', 'Five');
INSERT INTO Client(PwdHache, Login) VALUES('120330b9670df04eef8ad8176d135acf', 'Six');
INSERT INTO Client(PwdHache, Login) VALUES('667b1f80052f652fca8dbde75347053b', 'Seven');
INSERT INTO Client(PwdHache, Login) VALUES('5dfa4b4e17dc323198a080425099539c', 'Eight');
INSERT INTO Client(PwdHache, Login) VALUES('de75e8f0be17d6cd49f666edea1e7e71', 'Nine');

INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(1, 1, 1);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(1, 1, 2);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(1, 1, 3);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(2, 2, 2);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(2, 2, 3);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(3, 3, 2);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(3, 3, 3);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(4, 4, 1);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(5, 5, 2);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(5, 5, 3);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(6, 6, 2);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(7, 7, 2);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(8, 8, 1);
INSERT INTO EmployeeUserRole(EmployeeId, UserId, RoleId) VALUES(9, 9, 2);

INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(1, 3, 5);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(2, 3, 6);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(3, 4, 5);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(4, 5, 6);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(5, 3, 4);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(6, 2, 3);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(7, 1, 2);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(8, 4, 6);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(9, 3, 6);

INSERT INTO ClubPrefs(ClubId, SchedulePeriodId, EmployeeId) VALUES(1, 1, 1);
INSERT INTO ClubPrefs(ClubId, SchedulePeriodId, EmployeeId) VALUES(2, 2, 2);
INSERT INTO ClubPrefs(ClubId, SchedulePeriodId, EmployeeId) VALUES(3, 3, 3);

INSERT INTO Holidays(Date, Repeate) VALUES('20140101', 1);
INSERT INTO Holidays(Date, Repeate) VALUES('20150101', 1);
INSERT INTO Holidays(Date, Repeate) VALUES('20160101', 1);

INSERT INTO Prefs(ShiftsNumber, WorkHoursInDay) VALUES(1, 16);

INSERT INTO Categories(Title) VALUES('Старшие');
INSERT INTO Categories(Title) VALUES('Средние');
INSERT INTO Categories(Title) VALUES('Младшенькие');
INSERT INTO Categories(Title) VALUES('Блондинки');

INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES(1, 1);
INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES(1, 2);
INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES(1, 3);
INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES(2, 9);
INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES(2, 5);
INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES(2, 6);
INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES(3, 1);
INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES(3, 5);
INSERT INTO CategoryEmp(CategoryId, EmployeeId) VALUES(3, 9);
