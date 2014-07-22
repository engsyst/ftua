/*==============================================================*
 * DBMS name:      Microsoft SQL Server 2008                    
 * Created on:     03.07.2014 1:53:51                           
 * 
 * ������ ������������ Sybase Power Designer 16	����� ���������
 * ��������� ��������� �� ������ �������������.
 * - ���� ������ ��������� � ������������ � �� ��������. 
 * - ������� ����������� �����������.
 * - ������� ��������� ������.
 * 
 * ����� ��������� ������ �� �����������, ��������� 
 * "��������� �����������".
 * 
 * ���������: FitnessUA_data_model.pdm
 *
 *==============================================================*/


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
          where id = object_id('CLR_TRIGGER_CLUBS')
          and type = 'TR')
   drop trigger CLR_TRIGGER_CLUBS
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_EMPPREFS')
          and type = 'TR')
   drop trigger CLR_TRIGGER_EMPPREFS
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_EMPLOYEEGROUPS')
          and type = 'TR')
   drop trigger CLR_TRIGGER_EMPLOYEEGROUPS
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
          where id = object_id('CLR_TRIGGER_EMPLOYEES')
          and type = 'TR')
   drop trigger CLR_TRIGGER_EMPLOYEES
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_GROUPENUM')
          and type = 'TR')
   drop trigger CLR_TRIGGER_GROUPENUM
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
   where r.fkeyid = object_id('Assignment') and o.name = 'FK_ASSIGNME_REFERENCE_SCHEDULE')
alter table Assignment
   drop constraint FK_ASSIGNME_REFERENCE_SCHEDULE
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
   where r.fkeyid = object_id('ClubPrefs') and o.name = 'FK_CLUBPREF_REFERENCE_CLUBS')
alter table ClubPrefs
   drop constraint FK_CLUBPREF_REFERENCE_CLUBS
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('EmpPrefs') and o.name = 'FK_EMPPREFS_REFERENCE_EMPLOYEE')
alter table EmpPrefs
   drop constraint FK_EMPPREFS_REFERENCE_EMPLOYEE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('EmployeeGroups') and o.name = 'FK_EMPLOYEEGROUPS_REFERENCE_CLUBS')
alter table EmployeeGroups
   drop constraint FK_EMPLOYEEGROUPS_REFERENCE_CLUBS
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('EmployeeToAssignment') and o.name = 'FK_EMPLOYEE_REFERENCE_ASSIGNME')
alter table EmployeeToAssignment
   drop constraint FK_EMPLOYEE_REFERENCE_ASSIGNME
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('EmployeeToAssignment') and o.name = 'FK_EMPLOYEE_REFERENCE_EMPLOYEE')
alter table EmployeeToAssignment
   drop constraint FK_EMPLOYEE_REFERENCE_EMPLOYEE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('Employees') and o.name = 'FK_EMPLOYEE_REFERENCE_CLUBS')
alter table Employees
   drop constraint FK_EMPLOYEE_REFERENCE_CLUBS
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('Employees') and o.name = 'FK_EMPLOYEE_REFERENCE_EMPLOYEEGROUP')
alter table Employees
   drop constraint FK_EMPLOYEE_REFERENCE_EMPLOYEEGROUP
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('GroupEnum') and o.name = 'FK_GROUPENUM_REF_AS_ADMINGROUP')
alter table GroupEnum
   drop constraint FK_GROUPENUM_REF_AS_ADMINGROUP
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('GroupEnum') and o.name = 'FK_GROUPENUM_REF_AS_ASSIGNEDGROUP')
alter table GroupEnum
   drop constraint FK_GROUPENUM_REF_AS_ASSIGNEDGROUP
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('SchedulePeriod') and o.name = 'FK_SCHEDULE_REFERENCE_SCHEDULE')
alter table SchedulePeriod
   drop constraint FK_SCHEDULE_REFERENCE_SCHEDULE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('Users') and o.name = 'FK_USERS_REFERENCE_EMPLOYEE')
alter table Users
   drop constraint FK_USERS_REFERENCE_EMPLOYEE
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('Users') and o.name = 'FK_USERS_REFERENCE_ROLE')
alter table Users
   drop constraint FK_USERS_REFERENCE_ROLE
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Assignment')
            and   name  = 'XIFSHEDULEPERIOD'
            and   indid > 0
            and   indid < 255)
   drop index Assignment.XIFSHEDULEPERIOD
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('Assignment')
            and   name  = 'XIFCLUB'
            and   indid > 0
            and   indid < 255)
   drop index Assignment.XIFCLUB
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Assignment')
            and   type = 'U')
   drop table Assignment
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
           where  id = object_id('Clubs')
            and   type = 'U')
   drop table Clubs
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
           where  id = object_id('EmployeeGroups')
            and   type = 'U')
   drop table EmployeeGroups
go

if exists (select 1
            from  sysobjects
           where  id = object_id('EmployeeToAssignment')
            and   type = 'U')
   drop table EmployeeToAssignment
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Employees')
            and   type = 'U')
   drop table Employees
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('GroupEnum')
            and   name  = 'XIFASSIGNEDENUM'
            and   indid > 0
            and   indid < 255)
   drop index GroupEnum.XIFASSIGNEDENUM
go

if exists (select 1
            from  sysindexes
           where  id    = object_id('GroupEnum')
            and   name  = 'XIFADMINENUM'
            and   indid > 0
            and   indid < 255)
   drop index GroupEnum.XIFADMINENUM
go

if exists (select 1
            from  sysobjects
           where  id = object_id('GroupEnum')
            and   type = 'U')
   drop table GroupEnum
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
            from  sysindexes
           where  id    = object_id('Users')
            and   name  = 'XIFLOGIN'
            and   indid > 0
            and   indid < 255)
   drop index Users.XIFLOGIN
go

if exists (select 1
            from  sysobjects
           where  id = object_id('Users')
            and   type = 'U')
   drop table Users
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

if exists(select 1 from systypes where name='NUMBER')
   drop type NUMBER
go

if exists(select 1 from systypes where name='STRING')
   drop type STRING
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
/* Domain: NUMBER                                               */
/*==============================================================*/
create type NUMBER
   from int
go

/*==============================================================*/
/* Domain: STRING                                               */
/*==============================================================*/
create type STRING
   from nvarchar(20)
go

/*==============================================================*/
/* Table: Assignment                                            */
/*==============================================================*/
create table Assignment (
   AssignmentId         int                  identity	not null,
   SchedulePeriodId     int                  not null,
   ClubId               int                  not null,
   Date                 date                 not null,
   HalfOfDay            int                  not null
      constraint CKC_HALFOFDAY_ASSIGNME check (HalfOfDay between 0 and 2),
   constraint PK_ASSIGNMENT primary key nonclustered (AssignmentId)
)
go

execute sp_bindefault ZERO, 'Assignment.HalfOfDay'
go

/*==============================================================*/
/* Index: XIFCLUB                                               */
/*==============================================================*/
create unique index XIFCLUB on Assignment (
ClubId ASC
)
go

/*==============================================================*/
/* Index: XIFSHEDULEPERIOD                                      */
/*==============================================================*/
create unique index XIFSHEDULEPERIOD on Assignment (
SchedulePeriodId ASC
)
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
create unique index XIFCLUB on ClubPrefs (
ClubId ASC
)
go

/*==============================================================*/
/* Index: XIFSHEDULEPERIOD                                      */
/*==============================================================*/
create unique index XIFSHEDULEPERIOD on ClubPrefs (
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
/* Table: Clubs                                                 */
/*==============================================================*/
create table Clubs (
   ClubId               int                  identity not null,
   Title                nvarchar(256)        not null,
   Cash                 money                not null default 0,
   IsIndependent        bit                  not null default 0,
   constraint PK_CLUBS primary key (ClubId)
)
go

/*==============================================================*/
/* Table: EmpPrefs                                              */
/*==============================================================*/
create table EmpPrefs (
   EmpPrefsId           NUMBER               identity not null,
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

alter table EmpPrefs
   add CONSTRAINT equalConstr CHECK([MaxDays]>=[MinDays])
go

/*==============================================================*/
/* Index: XIFEMLOYEES                                           */
/*==============================================================*/
create unique index XIFEMLOYEES on EmpPrefs (
EmployeeId ASC
)
go

/*==============================================================*/
/* Table: EmployeeGroups                                        */
/*==============================================================*/
create table EmployeeGroups (
   EmployeeGroupId      int                  identity not null,
   ClubId               int                  null,
   Title                nvarchar(256)        not null,
   CanTrain             bit                  not null default 0,
   IsDeleted            bit                  not null default 0,
   constraint PK_EMPLOYEEGROUPS primary key (EmployeeGroupId)
)
go

/*==============================================================*/
/* Table: EmployeeToAssignment                                  */
/*==============================================================*/
create table EmployeeToAssignment (
   AssignmentId         int                  not null,
   EmployeeId           int                  not null,
   constraint PK_EMPLOYEETOASSIGNMENT primary key nonclustered (AssignmentId, EmployeeId)
)
go

if exists (select 1 from  sys.extended_properties
           where major_id = object_id('EmployeeToAssignment') and minor_id = 0)
begin 
   declare @CurrentUser sysname 
select @CurrentUser = user_name() 
execute sp_dropextendedproperty 'MS_Description',  
   'user', @CurrentUser, 'table', 'EmployeeToAssignment' 
 
end 


select @CurrentUser = user_name() 
execute sp_addextendedproperty 'MS_Description',  
   '�� ����� ���� ����� ���� ���������� �� ���� �������� ���', 
   'user', @CurrentUser, 'table', 'EmployeeToAssignment'
go

/*==============================================================*/
/* Table: Employees                                             */
/*==============================================================*/
create table Employees (
   EmployeeId           int                  identity not null,
   ClubId               int                  null,
   EmployeeGroupId      numeric              null,
   Firstname            nvarchar(256)        not null,
   Secondname           nvarchar(256)        not null,
   Lastname             nvarchar(256)        not null,
   Birthday             date                 not null,
   Address              nvarchar(max)        not null,
   PassportNumber       nvarchar(16)         not null,
   IdNumber             nvarchar(32)         not null,
   CellPhone            nvarchar(32)         not null,
   WorkPhone            nvarchar(32)         null,
   HomePhone            nvarchar(32)         null,
   Email                nvarchar(256)        not null,
   Education            nvarchar(1024)       null,
   Notes                nvarchar(max)        null,
   PassportIssuedBy     nvarchar(1024)       null,
   constraint PK_EMPLOYEES primary key (EmployeeId)
)
go

/*==============================================================*/
/* Table: GroupEnum                                             */
/*==============================================================*/
create table GroupEnum (
   Id                   NUMBER               identity not null,
   AdminGroupId         numeric              not null,
   AssignedGroupId      numeric              not null,
   constraint PK_GROUPENUM primary key nonclustered (Id)
)
go

/*==============================================================*/
/* Index: XIFADMINENUM                                          */
/*==============================================================*/
create index XIFADMINENUM on GroupEnum (
AdminGroupId ASC
)
go

/*==============================================================*/
/* Index: XIFASSIGNEDENUM                                       */
/*==============================================================*/
create index XIFASSIGNEDENUM on GroupEnum (
AssignedGroupId ASC
)
go

/*==============================================================*/
/* Table: Role                                                  */
/*==============================================================*/
create table Role (
   RoleId               NUMBER               identity not null,
   Rights               NUMBER               null,
   Title                nvarchar(20)         null,
   constraint PK_ROLE primary key nonclustered (RoleId)
)
go

/*==============================================================*/
/* Table: SchedulePeriod                                        */
/*==============================================================*/
create table SchedulePeriod (
   SchedulePeriodId     int                  identity not null,
   LastPeriodId         int                  null,
   StartDate            date                 not null,
   EndDate              date                 not null,
   constraint PK_SCHEDULEPERIOD primary key nonclustered (SchedulePeriodId)
)
go

/*==============================================================*/
/* Table: Users                                                 */
/*==============================================================*/
create table Users (
   UserId               NUMBER               identity not null,
   EmployeeId           int                  not null,
   RoleId               NUMBER               not null,
   PwdHache             NVARCHAR(128)        not null,
   Login                STRING               not null,
   constraint PK_USERS primary key nonclustered (UserId)
)
go

/*==============================================================*/
/* Table: Holidays                                              */
/*==============================================================*/
CREATE TABLE Holidays (
Holidayid BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL,
Date DATETIME NOT NULL UNIQUE
)
go

/*==============================================================*/
/* Index: XIFLOGIN                                              */
/*==============================================================*/
create index XIFLOGIN on Users (
Login ASC
)
go

alter table Assignment
   add constraint FK_ASSIGNME_REFERENCE_SCHEDULE foreign key (SchedulePeriodId)
      references SchedulePeriod (SchedulePeriodId)
         on delete cascade
go

alter table ClubPrefs
   add constraint FK_CLUBPREF_REFERENCE_EMPLOYEE foreign key (EmployeeId)
      references Employees (EmployeeId)
go

alter table ClubPrefs
   add constraint FK_CLUBPREF_REFERENCE_SCHEDULE foreign key (SchedulePeriodId)
      references SchedulePeriod (SchedulePeriodId)
         on delete cascade
go

alter table ClubPrefs
   add constraint FK_CLUBPREF_REFERENCE_CLUBS foreign key (ClubId)
      references Clubs (ClubId)
go

alter table EmpPrefs
   add constraint FK_EMPPREFS_REFERENCE_EMPLOYEE foreign key (EmployeeId)
      references Employees (EmployeeId)
go

alter table EmployeeGroups
   add constraint FK_EMPLOYEEGROUPS_REFERENCE_CLUBS foreign key (ClubId)
      references Clubs (ClubId)
go

alter table EmployeeToAssignment
   add constraint FK_EMPLOYEE_REFERENCE_ASSIGNME foreign key (AssignmentId)
      references Assignment (AssignmentId)
         on delete cascade
go

alter table EmployeeToAssignment
   add constraint FK_EMPLOYEE_REFERENCE_EMPLOYEE foreign key (EmployeeId)
      references Employees (EmployeeId)
go

alter table Employees
   add constraint FK_EMPLOYEE_REFERENCE_CLUBS foreign key (ClubId)
      references Clubs (ClubId)
go

alter table Employees
   add constraint FK_EMPLOYEE_REFERENCE_EMPLOYEEGROUP foreign key (EmployeeGroupId)
      references EmployeeGroups (EmployeeGroupId)
go

alter table Employees
   add constraint FK_EMPLOYEE_REFERENCE_USERS foreign key (EmployeeId)
      references Users (EmployeeId)
      on delete cascade 
      on update cascade
go

alter table GroupEnum
   add constraint FK_GROUPENUM_REF_AS_ADMINGROUP foreign key (AdminGroupId)
      references EmployeeGroups (EmployeeGroupId)
go

alter table GroupEnum
   add constraint FK_GROUPENUM_REF_AS_ASSIGNEDGROUP foreign key (AssignedGroupId)
      references EmployeeGroups (EmployeeGroupId)
go

alter table SchedulePeriod
   add constraint FK_SCHEDULE_REFERENCE_SCHEDULE foreign key (LastPeriodId)
      references SchedulePeriod (SchedulePeriodId)
go

alter table Users
   add constraint FK_USERS_REFERENCE_EMPLOYEE foreign key (EmployeeId)
      references Employees (EmployeeId)
go

alter table Users
   add constraint FK_USERS_REFERENCE_ROLE foreign key (RoleId)
      references Role (RoleId)
go

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
       order by 1
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
    raiserror @errno @errmsg
    rollback  transaction
end
go

ALTER TABLE [dbo].[Assignment]  WITH CHECK ADD  CONSTRAINT [FK_ASSIGNME_REFERENCE_CLUB] FOREIGN KEY([ClubId])
REFERENCES [dbo].[Clubs] ([ClubId]) 
ON DELETE CASCADE
GO

INSERT INTO Clubs(Title, Cash, IsIndependent) VALUES('Тренажёрный зал', 2000, 0);
INSERT INTO Clubs(Title, Cash, IsIndependent) VALUES('Аэробика', 4500.84, 0);
INSERT INTO Clubs(Title, Cash, IsIndependent) VALUES('Спортзал', 19956.89, 1);

INSERT INTO SchedulePeriod(StartDate, EndDate) VALUES('20140601', '20140615');
INSERT INTO SchedulePeriod(StartDate, EndDate, LastPeriodId) VALUES('20140616', '20140630', 1);
INSERT INTO SchedulePeriod(StartDate, EndDate, LastPeriodId) VALUES('20140701', '20140710', 2);
INSERT INTO SchedulePeriod(StartDate, EndDate, LastPeriodId) VALUES('20140711', '20140715', 3);
INSERT INTO SchedulePeriod(StartDate, EndDate, LastPeriodId) VALUES('20140716', '20140731', 4);

INSERT INTO GroupEnum(AdminGroupId, AssignedGroupId) VALUES(1, 1);
INSERT INTO GroupEnum(AdminGroupId, AssignedGroupId) VALUES(4, 4);
INSERT INTO GroupEnum(AdminGroupId, AssignedGroupId) VALUES(7, 7);

INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(1, 'admins', 1, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(1, 'teachers', 1, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(1, 'workers', 0, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(2, 'admins', 1, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(2, 'teachers', 1, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(2, 'workers', 0, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(3, 'admins', 1, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(3, 'teachers', 1, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(3, 'workers', 0, 1);

INSERT INTO Assignment(SchedulePeriodId, ClubId, Date, HalfOfDay) VALUES(1, 1, '20140605', 1);
INSERT INTO Assignment(SchedulePeriodId, ClubId, Date, HalfOfDay) VALUES(2, 2, '20140620', 1);
INSERT INTO Assignment(SchedulePeriodId, ClubId, Date, HalfOfDay) VALUES(3, 3, '20140708', 1);

INSERT INTO Role(Rights, Title) VALUES(0, 'responsible person');
INSERT INTO Role(Rights, Title) VALUES(1, 'admin');

INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(1, 1, 'Ivan', 'Ivanovich', 'Ivanov', '19901210', 'Kharkiv Ivanova str. 5', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'ivanov@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(1, 1, 'Dmytryj', 'Ivanovich', 'Denisov', '19941010', 'Kharkiv Ivanova str. 4', 'MH083456', '2234567890123456', 
'0919145123', '0574641234', '0578723456', 'denisov@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(1, 2, 'Ivan', 'Ivanovich', 'Ivanovsky', '19941011', 'Kharkiv Repina str. 5', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'ivanovsky@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(2, 3, 'Ivan', 'Ivanovich', 'Vasiliev', '19921210', 'Kharkiv Plotnykova str. 5', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'vasiiev@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Печенежский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(2, 2, 'Ivan', 'Ivanovich', 'Jakson', '19901210', 'Kharkiv Kirova str. 67', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'petrov@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(2, 3, 'Ivan', 'Ivanovich', 'Jakson', '19901210', 'Kharkiv Linea str. 15', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'jakson@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Ленинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(2, 1, 'Ivan', 'Ivanovich', 'Kirov', '19901210', 'Kharkiv Ivanova str. 125', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'kirov@mail.ru', 'KNURE master', 'Some note 1. Some note 2. Some note 3', 'Московский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(3, 1, 'Ivan', 'Ivanovich', 'Loenov', '19901210', 'Kharkiv Franko str. 3', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'leonov@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 11.03.2005');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(3, 1, 'Ivan', 'Ivanovich', 'Tsvang', '19901210', 'Donetsk Shevchenka str. 15', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'tsvang@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 09.11.2007');

INSERT INTO EmployeeToAssignment(AssignmentId, EmployeeId) VALUES(1, 1);
INSERT INTO EmployeeToAssignment(AssignmentId, EmployeeId) VALUES(1, 2);
INSERT INTO EmployeeToAssignment(AssignmentId, EmployeeId) VALUES(1, 3);
INSERT INTO EmployeeToAssignment(AssignmentId, EmployeeId) VALUES(2, 4);
INSERT INTO EmployeeToAssignment(AssignmentId, EmployeeId) VALUES(2, 5);
INSERT INTO EmployeeToAssignment(AssignmentId, EmployeeId) VALUES(2, 6);
INSERT INTO EmployeeToAssignment(AssignmentId, EmployeeId) VALUES(2, 7);
INSERT INTO EmployeeToAssignment(AssignmentId, EmployeeId) VALUES(3, 8);
INSERT INTO EmployeeToAssignment(AssignmentId, EmployeeId) VALUES(3, 9);

INSERT INTO Users(EmployeeId, RoleId, PwdHache, Login) VALUES(1, 1, 'f6518063e665a1e992d97023ac42e71c', 'loginOne');
INSERT INTO Users(EmployeeId, RoleId, PwdHache, Login) VALUES(2, 2, 'eee2f408ecc67847c29730b91bf7d22b', 'loginTwo');
INSERT INTO Users(EmployeeId, RoleId, PwdHache, Login) VALUES(3, 2, 'd6a21b4184c314aaf34a8c0e7be36d76', 'loginThree');
INSERT INTO Users(EmployeeId, RoleId, PwdHache, Login) VALUES(4, 1, '0fdaecd4e08487bdaf99f5fc707d384b', 'loginFour');
INSERT INTO Users(EmployeeId, RoleId, PwdHache, Login) VALUES(5, 2, '040fbf770f98746b99669b0bc4fa78bb', 'loginFive');
INSERT INTO Users(EmployeeId, RoleId, PwdHache, Login) VALUES(6, 1, '5fa2ee7013af9b05817d41c8834f1321', 'loginSix');
INSERT INTO Users(EmployeeId, RoleId, PwdHache, Login) VALUES(7, 1, '5a1b34f9e2c8661e6d75e3e9d80202e9', 'loginSeven');
INSERT INTO Users(EmployeeId, RoleId, PwdHache, Login) VALUES(8, 1, '1530499374523e86b30ac7ce810a0730', 'loginEight');
INSERT INTO Users(EmployeeId, RoleId, PwdHache, Login) VALUES(9, 1, '91434d485c9891f4b4c9b6d02fd8bd0c', 'loginNine');

INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(1, 3, 5);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(2, 3, 7);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(3, 4, 5);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(4, 5, 7);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(5, 3, 4);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(6, 2, 3);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(7, 1, 2);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(8, 4, 6);
INSERT INTO EmpPrefs(EmployeeId, MinDays, MaxDays) VALUES(9, 3, 7);

INSERT INTO ClubPrefs(ClubId, SchedulePeriodId, EmployeeId) VALUES(1, 3, 1);
INSERT INTO ClubPrefs(ClubId, SchedulePeriodId, EmployeeId) VALUES(2, 4, 2);
INSERT INTO ClubPrefs(ClubId, SchedulePeriodId, EmployeeId) VALUES(3, 5, 3);

INSERT INTO Holidays(Date) VALUES('20140101');
INSERT INTO Holidays(Date) VALUES('20150101');
INSERT INTO Holidays(Date) VALUES('20160101');
