select pl.*, count(lp.entry)
from np_users.protein_lists pl
inner join np_users.list_proteins lp on pl.list_id = lp.list_id
inner join np_users.users u on u.user_id = pl.owner_id
where u.username = :username
group by pl.list_id
order by name;