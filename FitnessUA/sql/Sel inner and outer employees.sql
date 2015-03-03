
CREATE VIEW dbo.ActiveEmpWithUser WITH SCHEMABINDING
AS
SELECT DISTINCT 
                      ee.EmployeeId, ee.Firstname, ee.Secondname, ee.Lastname, ee.Birthday, ee.Address, ee.Passportint, 
                      ee.Idint, ee.CellPhone, ee.WorkPhone, ee.HomePhone, ee.Email, ee.Education, ee.Notes, 
                      ee.PassportIssuedBy, ee.IsDeleted, ee.Colour, Client.UserId, Client.Login, Client.PwdHache
FROM         Employee as ee INNER JOIN
                      EmployeeUserRole ON ee.EmployeeId = EmployeeUserRole.EmployeeId INNER JOIN
                      Client ON EmployeeUserRole.UserId = Client.UserId
WHERE     (ee.IsDeleted = 0)

GO

