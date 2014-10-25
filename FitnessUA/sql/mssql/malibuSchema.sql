/*==============================================================*/
/* Drag and drop to SQL Server Management Studio and run script */
/*==============================================================*/

use master
go

DROP DATABASE FitnessUA
go

CREATE DATABASE FitnessUA
go

use FitnessUA
go

if exists (select 1
          from sysobjects
          where id = object_id('CLR_TRIGGER_EMPLOYEEGROUPS')
          and type = 'TR')
   drop trigger CLR_TRIGGER_EMPLOYEEGROUPS
go

if exists (select 1
   from sys.sysreferences r join sys.sysobjects o on (o.id = r.constid and o.type = 'F')
   where r.fkeyid = object_id('EmployeeGroups') and o.name = 'FK_EMPLOYEEGROUPS_REFERENCE_CLUB')
alter table EmployeeGroups
   drop constraint FK_EMPLOYEEGROUPS_REFERENCE_CLUB
go

if exists (select 1
            from  sysobjects
           where  id = object_id('EmployeeGroups')
            and   type = 'U')
   drop table EmployeeGroups
go

/*==============================================================*/
/* Table: Clubs                                                 */
/*==============================================================*/
create table Clubs (
   ClubId               int                  identity not null,
   Title                nvarchar(256)        not null,
   Cash                 money                not null default 0,
   constraint PK_CLUBS primary key (ClubId)
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

alter table EmployeeGroups
   add constraint FK_EMPLOYEEGROUPS_REFERENCE_CLUB foreign key (ClubId)
      references Clubs (ClubId)
go

/*==============================================================*/
/* Table: Employees                                             */
/*==============================================================*/
create table Employees (
   EmployeeId           int                  identity not null,
   ClubId               int                  NULL REFERENCES Clubs(ClubId),
   EmployeeGroupId      int				     NULL REFERENCES EmployeeGroups (EmployeeGroupId),
   Firstname            nvarchar(256)        not null,
   Secondname           nvarchar(256)        not null,
   Lastname             nvarchar(256)        not null,
   Birthday             date                 not null,
   Address              nvarchar(max)        not null,
   PassportNumber		nvarchar(16)         not null,
   IdNumber				nvarchar(32)         not null,
   CellPhone            nvarchar(32)         not null,
   WorkPhone            nvarchar(32)         null,
   HomePhone            nvarchar(32)         null,
   Email                nvarchar(256)        not null,
   Education            nvarchar(1024)       null,
   Notes                nvarchar(max)        null,
   PassportIssuedBy     nvarchar(1024)       null,
   constraint PK_Employees primary key (EmployeeId)
)
go

INSERT INTO Clubs(Title, Cash) VALUES('Бавария_Origin', 12000);
INSERT INTO Clubs(Title, Cash) VALUES('Маршала Жукова_Origin', 4500.84);
INSERT INTO Clubs(Title, Cash) VALUES('Смольная_Origin', 19956.89);

INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(1, 'admins', 0, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(1, 'teachers', 1, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(1, 'workers', 0, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(2, 'admins', 0, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(2, 'teachers', 1, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(2, 'workers', 0, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(3, 'admins', 0, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(3, 'teachers', 1, 0);
INSERT INTO EmployeeGroups(ClubId, Title, CanTrain, IsDeleted) VALUES(3, 'workers', 0, 1);

INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(1, 1, 'Иван', 'Петрович', 'Корнилов', '19801210', 'Kharkiv Ivanova str. 5', 'MH093450', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'kornilov@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(1, 1, 'Дмитрий', 'Иванович', 'Денисов', '19841010', 'Kharkiv Ivanova str. 4', 'MH083451', '2234567890123456', 
'0919145123', '0574641234', '0578723456', 'denisov@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(1, 2, 'Корней', 'Степанович', 'Чуковский', '19941011', 'Kharkiv Repina str. 5', 'MH093452', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'chookovsky@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(2, 3, 'Сергей', 'Тихонович', 'Васильев', '19921210', 'Kharkiv Plotnykova str. 5', 'MH093453', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'vasiiev@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Печенежский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(2, 2, 'Ринат', 'Абдулович', 'Чиканов', '19901210', 'Kharkiv Kirova str. 67', 'MH093454', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'chikanov@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(2, 3, 'Виталий', 'Фёдорович', 'Деникин', '19781210', 'Kharkiv Linea str. 15', 'MH093455', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'denikin@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Ленинский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(2, 1, 'Олег', 'Васильевич', 'Киров', '19860210', 'Kharkiv Ivanova str. 125', 'MH093456', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'kirov@mail.ru', 'KNURE master', 'Some note 1. Some note 2. Some note 3', 'Московский ГУ МВД в Харьковской области 19.05.2006');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(3, 1, 'Дмитрий', 'Владимирович', 'Леонов', '19901210', 'Kharkiv Franko str. 3', 'MH093457', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'leonov@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 11.03.2005');
INSERT INTO Employees(ClubId, EmployeeGroupId, Firstname, Secondname, Lastname, Birthday, Address, PassportNumber, IdNumber, CellPhone, WorkPhone, HomePhone, Email, Education, Notes, PassportIssuedBy)
VALUES(3, 1, 'Павел', 'Дмитриевич', 'Никонов', '19901210', 'Donetsk Shevchenka str. 15', 'MH093458', '1234567890123456', 
'0919145123', '0574641234', '0578723456', 'nikonov@mail.ru', 'KNURE bachelor', 'Some note 1. Some note 2. Some note 3', 'Дзержинский ГУ МВД в Харьковской области 09.11.2007');
