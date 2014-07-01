create database FitnessUA;
use FitnessUA;
create table GroupEnum (id bigint, admin_id bigint, assigned_id bigint);

CREATE TABLE SchedulePeriod (
shedule_period_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL, 
startDate DATETIME NOT NULL,
endDate DATETIME NOT NULL,
last_period_id BIGINT REFERENCES SchedulePeriod(shedule_period_id)
);

INSERT INTO SchedulePeriod(startDate, endDate) VALUES('20140601', '20140615');
INSERT INTO SchedulePeriod(startDate, endDate, last_period_id) VALUES('20140616', '20140630', 1);
INSERT INTO SchedulePeriod(startDate, endDate, last_period_id) VALUES('20140701', '20140710', 2);
INSERT INTO SchedulePeriod(startDate, endDate, last_period_id) VALUES('20140711', '20140715', 3);
INSERT INTO SchedulePeriod(startDate, endDate, last_period_id) VALUES('20140716', '20140731', 4);

CREATE TABLE Club(
club_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL,
title NVARCHAR(255) NOT NULL,
isIndependent BIT NOT NULL
);

INSERT INTO Club(title, isIndependent) VALUES('Тренажёрный зал', 0);
INSERT INTO Club(title, isIndependent) VALUES('Аэробика', 0);
INSERT INTO Club(title, isIndependent) VALUES('Спортзал', 1);

CREATE TABLE DaySchedule (
day_schedule_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL,
dates DATETIME NOT NULL,
halfOfDay TINYINT NOT NULL CHECK(halfOfDay>=0 AND halfOfDay<=2),
users_id BIGINT REFERENCES User(users_id),
club_id BIGINT REFERENCES Club(club_id),
schedule_period_id BIGINT REFERENCES SchedulePeriod(shedule_period_id)
);

INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140630', 0, 1, 1, 2);
INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140702', 1, 2, 2, 3);
INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140703', 2, 3, 3, 3);
INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140703', 0, 1, 3, 3);
INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140704', 0, 1, 3, 3);

CREATE TABLE EmployeeGroups(
group_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL,
title NVARCHAR(255) NOT NULL
);

INSERT INTO EmployeeGroups(title) VALUES('admin');
INSERT INTO EmployeeGroups(title) VALUES('teacher');
INSERT INTO EmployeeGroups(title) VALUES('worker');

CREATE TABLE Employees(
employee_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL,
name NVARCHAR(64) NOT NULL,
sureName NVARCHAR(64) NOT NULL,
threadName NVARCHAR(64) NOT NULL,
group_id BIGINT REFERENCES EmployeeGroups(group_id)
);

INSERT INTO Employees(name, sureName, threadName, group_id) VALUES('Ivan', 'Ivanovych', 'Ivanov', 1);
INSERT INTO Employees(name, sureName, threadName, group_id) VALUES('Boris', 'Ivanovych', 'Ivanov', 2);
INSERT INTO Employees(name, sureName, threadName, group_id) VALUES('Oleg', 'Stepanovych', 'Ivanov', 3);
INSERT INTO Employees(name, sureName, threadName, group_id) VALUES('Petr', 'Petrovych', 'Ivanov', 1);
INSERT INTO Employees(name, sureName, threadName, group_id) VALUES('Dmytryj', 'Stepanovych', 'Petrov', 3);

CREATE TABLE Users(
users_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL,
login NVARCHAR(64) NOT NULL,
pwdHash NVARCHAR(128) NOT NULL,
email NVARCHAR(64) NOT NULL,
employee_id BIGINT REFERENCES Employees(employee_id)
);

INSERT INTO Users(login, pwdHash, email, employee_id) VALUES('login1', 'password1', 'mail1@mail.ru', 1);
INSERT INTO Users(login, pwdHash, email, employee_id) VALUES('login2', 'password2', 'mail2@mail.ru', 2);
INSERT INTO Users(login, pwdHash, email, employee_id) VALUES('login3', 'password3', 'mail3@mail.ru', 3);
INSERT INTO Users(login, pwdHash, email, employee_id) VALUES('login4', 'password4', 'mail4@mail.ru', 4);
INSERT INTO Users(login, pwdHash, email, employee_id) VALUES('login5', 'password5', 'mail5@mail.ru', 5);

CREATE TABLE EmployeePref (
pref_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL, 
minimum TINYINT DEFAULT 0 CHECK(minimum>=0 AND minimum<=7),
maximum TINYINT DEFAULT 0 CHECK(maximum>=0 AND maximum<=7),
employee_id BIGINT REFERENCES Employees(employee_id),
CONSTRAINT equalConstr CHECK(maximum>=minimum)
);

INSERT INTO EmployeePref(minimum, maximum, employee_id) VALUES(1, 4, 1);
INSERT INTO EmployeePref(minimum, maximum, employee_id) VALUES(2, 5, 2);
INSERT INTO EmployeePref(minimum, maximum, employee_id) VALUES(4, 5, 3);
INSERT INTO EmployeePref(minimum, maximum, employee_id) VALUES(3, 4, 4);
INSERT INTO EmployeePref(minimum, maximum, employee_id) VALUES(2, 6, 5);

CREATE TABLE ClubPref (
schedule_period_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL, 
club_id BIGINT REFERENCES Club(club_id),
employee_id BIGINT REFERENCES Employees(employee_id)
);

INSERT INTO ClubPref(club_id, employee_id) VALUES(1, 1);
INSERT INTO ClubPref(club_id, employee_id) VALUES(2, 2);
INSERT INTO ClubPref(club_id, employee_id) VALUES(3, 3);
INSERT INTO ClubPref(club_id, employee_id) VALUES(1, 4);
INSERT INTO ClubPref(club_id, employee_id) VALUES(3, 5);
