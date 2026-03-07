create table order_book (
     order_id integer not null references orders(id) on delete cascade,
     book_id integer not null references books(id),
     quantity integer not null check (quantity > 0),
     primary key (order_id, book_id)
);