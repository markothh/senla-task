create table product (
maker varchar(10) not null,
model varchar(50) primary key,
type varchar(50) not null,
check (type in ('PC', 'Laptop', 'Printer')))

create table pc (
code int primary key,
model varchar(50) references product(model),
speed smallint not null,
ram smallint not null,
hd real not null,
cd varchar(10) not null,
price money
)

create table laptop (
code int primary key,
model varchar(50) references product(model),
speed smallint not null,
ram smallint not null,
hd real not null,
price money,
screen smallint not null
)

create table printer (
code int primary key,
model varchar(50) references product(model),
color char(1),
type varchar(10),
price money,
check (type in ('Laser', 'Jet', 'Matrix'))
)