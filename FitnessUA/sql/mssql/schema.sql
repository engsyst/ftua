/*==============================================================*
 * DBMS name:      Microsoft SQL Server 2008                    
 * Created on:     03.07.2014 1:53:51                           
 * 
 * Скрипт сгенерирован Sybase Power Designer 16	после получения
 * последних изменений от других разработчиков.
 * - Поля данных приведены в соответствие с их нотацией. 
 * - Введены ограничения целостности.
 * - Базовая валидация данных.
 * 
 * После генерации скрипт не запускается, требуется 
 * "обработка напильником".
 * 
 * Исходники: FitnessUA_data_model.pdm
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
   AssignmentId         int                  not null,
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
   ClubPrefsId          int                  not null,
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
   ClubId               int                  identity,
   Title                nvarchar(256)        not null,
   Cash                 money                not null default 0,
   constraint PK_CLUBS primary key (ClubId)
)
go

/*==============================================================*/
/* Table: EmpPrefs                                              */
/*==============================================================*/
create table EmpPrefs (
   EmpPrefsId           NUMBER               not null,
   EmployeeId           int                  not null,
   MinDays              int                  not null
      constraint CKC_MINDAYS_EMPPREFS check (MinDays between 0 and 7),
   MaxDays              int                  not null
      constraint CKC_MAXDAYS_EMPPREFS check (MaxDays between 0 and 7),
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
/* Table: EmployeeGroups                                        */
/*==============================================================*/
create table EmployeeGroups (
   EmployeeGroupId      numeric              identity,
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
   'Не может быть более двух назначений на одну половину дня', 
   'user', @CurrentUser, 'table', 'EmployeeToAssignment'
go

/*==============================================================*/
/* Table: Employees                                             */
/*==============================================================*/
create table Employees (
   EmployeeId           int                  identity,
   ClubId               int                  null,
   EmployeeGroupId      numeric              null,
   EmpEmployeegroupid   numeric              null,
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
   Id                   NUMBER               not null,
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
   RoleId               NUMBER               not null,
   Rights               NUMBER               null,
   Title                nvarchar(20)         null,
   constraint PK_ROLE primary key nonclustered (RoleId)
)
go

/*==============================================================*/
/* Table: SchedulePeriod                                        */
/*==============================================================*/
create table SchedulePeriod (
   SchedulePeriodId     int                  not null,
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
   UserId               NUMBER               not null,
   EmployeeId           int                  not null,
   RoleId               NUMBER               not null,
   PwdHache             STRING               not null,
   Login                STRING               not null,
   constraint PK_USERS primary key nonclustered (UserId)
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

