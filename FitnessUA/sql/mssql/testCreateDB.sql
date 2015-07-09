use master;
go


IF EXISTS( select * from sys.databases where name='FitnessUA' )
BEGIN

--All incomplete transactions will be rolled back and any other connections to the FitnessUA sample database will be immediately disconnected.
--The database is closed, shut down cleanly, and marked offline. The database cannot be modified while it is offline.
ALTER DATABASE FitnessUA SET OFFLINE WITH ROLLBACK IMMEDIATE

--The database is open and available for use.
ALTER DATABASE FitnessUA SET ONLINE

drop database  FitnessUA

END

create database FitnessUA
go