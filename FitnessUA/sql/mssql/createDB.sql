use master;
go


IF EXISTS( select * from sys.databases where name='FitnessUA' )
BEGIN

drop database  FitnessUA 

END

create database FitnessUA
go