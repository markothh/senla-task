@echo off

set DB_NAME=bookshop
set USER=postgres

createdb -U %USER% %DB_NAME%
psql -U %USER% -d %DB_NAME% -f ddl.sql
psql -U %USER% -d %DB_NAME% -f dml.sql

echo Database installed and filled successfully
pause
