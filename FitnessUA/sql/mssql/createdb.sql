create database FitnessUA;
use FitnessUA;
create table GroupEnum (id bigint, admin_id bigint, assigned_id bigint);
create table Users (users_id bigint, login nvarchar, pwdHash nvarchar, email nvarchar, employee_id bigint);
create table EmployeePref (pref_id bigint, minimum tinyint, maximum tinyint, employee_id bigint);
create table EmployeeGroups (group_id bigint, title nvarchar);
create table Employees (employee_id bigint, name nvarchar, sureName nvarchar, threadName nvarchar, group_id bigint);
create table ClubPrefs (shedule_period_id bigint, club_id bigint, employee_id bigint);
create table DayShedule (day_shedule_id bigint, dates datetime, halfOfDay tinyint, users_id bigint, club_id bigint, shedule_period_id bigint);
create table Club (club_id bigint, title nvarchar, isIndependent bit);

CREATE TABLE SchedulePeriod (
shedule_period_id BIGINT PRIMARY KEY IDENTITY (1, 1) NOT NULL, 
startDate DATETIME NOT NULL,
endDate DATETIME NOT NULL,
last_period_id BIGINT REFERENCES SchedulePeriod(shedule_period_id),
);

INSERT INTO SchedulePeriod(startDate, endDate) VALUES('20140601', '20140615');
INSERT INTO SchedulePeriod(startDate, endDate, last_period_id) VALUES('20140616', '20140630', 1);
INSERT INTO SchedulePeriod(startDate, endDate, last_period_id) VALUES('20140701', '20140710', 2);
INSERT INTO SchedulePeriod(startDate, endDate, last_period_id) VALUES('20140711', '20140715', 3);
INSERT INTO SchedulePeriod(startDate, endDate, last_period_id) VALUES('20140716', '20140731', 4);
