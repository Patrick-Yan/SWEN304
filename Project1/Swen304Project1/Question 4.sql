Question 4                                                
(1)
SELECT DISTINCT BankName
FROM Banks NATURAL JOIN hasAccounts NATURAL JOIN Robbers WHERE Nickname ='Calamity Jane';
                                        
(2)
SELECT BankName, Security
FROM Banks
WHERE City='Chicago' AND NoAccounts>9000;
                                                
(3)                                
SELECT BankName, City
FROM Banks
WHERE BankName NOT IN (SELECT BankName FROM Banks WHERE City = 'Chicago') ORDER BY NoAccounts ASC;
                                                
(4)
SELECT BankName, City FROM Robberies WHERE Date = (SELECT MIN(Date) From Robberies);
                                                
(5)
SELECT RobberId,Nickname,earnings
FROM (SELECT RobberId,SUM(Share) AS earnings FROM Accomplices GROUP BY RobberId) AS TotalEarning NATURAL JOIN Robbers
WHERE Earnings>30000 ORDER BY Earnings DESC;
                                        
(6)
SELECT Robberid,NickName,NoYears FROM Robbers WHERE NoYears>3 ORDER BY RobberId;
                                        
(7)
SELECT RobberId , Nickname, Description
FROM Robbers NATURAL JOIN HasSkills NATURAL JOIN Skills
                                        
ORDER BY Description;
                                                                        
                                                
(8)
SELECT RobberId, NickName , (Age-NoYears) AS NumberOfYears FROM Robbers
WHERE NoYears > (Age/2);