create or replace function random_string(length integer) returns text as 
$$
declare
  chars text[] := '{0,1,2,3,4,5,6,7,8,9,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}';
  result text := '';
  i integer := 0;
begin
  if length < 0 then
    raise exception 'Given length cannot be less than 0';
  end if;
  for i in 1..length loop
    result := result || chars[1+random()*(array_length(chars, 1)-1)];
  end loop;
  return result;
end;
$$ language plpgsql;

ALTER TABLE np_users.user_protein_lists ADD COLUMN public_id varchar(20);
ALTER TABLE np_users.user_queries ADD COLUMN public_id varchar(20);

UPDATE np_users.user_protein_lists set public_id = random_string(8) where public_id is null;
UPDATE np_users.user_queries set public_id = random_string(8) where public_id is null;

ALTER TABLE np_users.user_protein_lists ALTER COLUMN public_id set NOT NULL;
ALTER TABLE np_users.user_queries ALTER COLUMN public_id set NOT NULL;

ALTER TABLE np_users.user_protein_lists ADD CONSTRAINT user_protein_lists_pubid_udx UNIQUE (public_id);
ALTER TABLE np_users.user_queries ADD CONSTRAINT user_queries_pubid_udx UNIQUE (public_id);
