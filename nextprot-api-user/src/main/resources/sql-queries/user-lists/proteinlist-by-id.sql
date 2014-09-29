select protein_list.*
fom np_users.protein_lists pl, np_users.users u
where pl.owner_id = u.user_id	
and pl.list_id = :listId;