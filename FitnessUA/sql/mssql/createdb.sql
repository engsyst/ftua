create database FitnessUA;
use FitnessUA;
create table GroupEnum (id bigint, admin_id bigint, assigned_id bigint);
create table Users (users_id bigint, login nvarchar, pwdHash nvarchar, email nvarchar, employee_id bigint);
create table EmployeePref (pref_id bigint, minimum tinyint, maximum tinyint, employee_id bigint);
create table EmployeeGroups (group_id bigint, title nvarchar);
create table Employees (employee_id bigint, name nvarchar, sureName nvarchar, threadName nvarchar, group_id bigint);
create table ClubPrefs (shedule_period_id bigint, club_id bigint, employee_id bigint);

CREATE TABLE Club(
club_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL,
title NVARCHAR(255) NOT NULL,
isIndependent BIT NOT NULL
);

INSERT INTO Club(title, isIndependent) VALUES('Тренажёрный зал', 0);
INSERT INTO Club(title, isIndependent) VALUES('Аэробика', 0);
INSERT INTO Club(title, isIndependent) VALUES('Спортзал', 1);

CREATE TABLE DaySchedule (
day_shedule_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL,
dates DATETIME NOT NULL,
halfOfDay TINYINT NOT NULL,
users_id BIGINT REFERENCES User(users_id),
club_id BIGINT REFERENCES Club(club_id),
schedule_period_id BIGINT REFERENCES SchedulePeriod(shedule_period_id)
);

INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140630', 0, 1, 1, 2);
INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140702', 1, 2, 2, 3);
INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140703', 2, 3, 3, 3);
INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140703', 0, 1, 3, 3);
INSERT INTO DaySchedule(dates, halfOfDay, users_id, club_id, schedule_period_id) VALUES('20140704', 0, 1, 3, 3);

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
