1. select * from pc
where price > 500::money

2. select distinct maker from printer join product 
on printer.model = product.model

3. select model, hd, screen from laptop
where price > 1000::money

4. select * from printer
where color='y'

5. select model, speed, hd from pc
where (cd = '12x' or cd = '24x')
and price < 600::money

6. select maker, speed from product p join laptop l
on p.model = l.model
where hd >= 100

7. select p.model, price from product p 
join pc on p.model = pc.model
where maker = 'B'
UNION
select p.model, price from product p 
join laptop l on p.model = l.model
where maker = 'B'
UNION
select p.model, price from product p 
join printer on p.model = printer.model
where maker = 'B'

8. select distinct p1.maker from product p1
where p1.type='PC'
and not exists (
select 1 from product p2
where p2.type = 'Laptop'
and p1.maker = p2.maker
)

9. select distinct maker from product p 
join pc on p.model = pc.model
where speed >= 450

10. select model, price from printer
where price = (
select max(price) from printer
)

11. select avg(speed) as average_speed from pc

12. select avg(speed) as average_speed from laptop
where price > 1000::money

13. select avg(speed) as average_speed from pc
join product p on p.model = pc.model
where maker='A'

14. select speed, avg(price::numeric) as average_price from pc
group by speed

15. select hd from pc
group by hd
having count(*) > 1

16. select * from pc p1
join pc p2 
on p1.speed = p2.speed
and p1.ram = p2.ram
and p1.model > p2.model

17. select p.model, type, speed from laptop l
join product p on p.model = l.model
where speed < all(
select speed from pc
)

18. select maker, price from printer
join product p on p.model = printer.model
where color='y'
and price = (
select min(price) from printer
where color='y'
)

19. select maker, avg(screen) as average_screen_size from laptop l
join product p on p.model = l.model
group by maker

20. select maker, count(distinct model) as model_count from product
where type='PC'
group by maker
having count(distinct model) > 2

21. select maker, max(price) from pc
join product p on p.model = pc.model
group by maker

22. select speed, avg(price::numeric) as average_price from pc
where speed > 600
group by speed

23. select distinct maker from product p
where exists (
select 1 from product p1
join pc on pc.model = p1.model
where p1.maker = p.maker
and speed >= 750
)
and exists (
select 1 from product p2
join laptop l on l.model = p2.model
where p2.maker = p.maker
and speed >= 750
)

24. with all_products as (
	select p.model, price from product p 
	join pc on p.model = pc.model
	UNION
	select p.model, price from product p 
	join laptop l on p.model = l.model
	UNION
	select p.model, price from product p 
	join printer on p.model = printer.model
)
select model from all_products
where price = (
	select max(price) from all_products
)

25. select distinct maker from product p
join pc on pc.model = p.model
where ram = (
	select min(ram) from pc
)
and speed = (
	select max(speed) from pc
	where ram = (
		select min(ram) from pc
	)
)
and exists (
	select 1 from product p1
	where p1.maker = p.maker
	and type='Printer'
)