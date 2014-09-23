--
-- PostgreSQL database dump
--

-- Dumped from database version 9.2.4
-- Dumped by pg_dump version 9.2.2
-- Started on 2014-09-15 16:51:07 CEST

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 9 (class 2615 OID 198606)
-- Name: np_users; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA np_users;


ALTER SCHEMA np_users OWNER TO postgres;

SET search_path = np_users, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 177 (class 1259 OID 203406)
-- Name: list_protein_assoc; Type: TABLE; Schema: np_users; Owner: postgres; Tablespace: 
--

CREATE TABLE list_protein_assoc (
    list_id integer NOT NULL,
    protein_id integer NOT NULL
);


ALTER TABLE np_users.list_protein_assoc OWNER TO postgres;

--
-- TOC entry 176 (class 1259 OID 198645)
-- Name: np_accessions; Type: TABLE; Schema: np_users; Owner: postgres; Tablespace: 
--

CREATE TABLE np_accessions (
    identifier_id bigint NOT NULL,
    unique_name character varying(100),
    display_name character varying(200)
);


ALTER TABLE np_users.np_accessions OWNER TO postgres;

--
-- TOC entry 175 (class 1259 OID 198621)
-- Name: protein_lists; Type: TABLE; Schema: np_users; Owner: postgres; Tablespace: 
--

CREATE TABLE protein_lists (
    list_id integer NOT NULL,
    name character varying(200),
    description character varying(2000),
    owner_id integer
);


ALTER TABLE np_users.protein_lists OWNER TO postgres;

--
-- TOC entry 174 (class 1259 OID 198619)
-- Name: protein_lists_list_id_seq; Type: SEQUENCE; Schema: np_users; Owner: postgres
--

CREATE SEQUENCE protein_lists_list_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE np_users.protein_lists_list_id_seq OWNER TO postgres;

--
-- TOC entry 3266 (class 0 OID 0)
-- Dependencies: 174
-- Name: protein_lists_list_id_seq; Type: SEQUENCE OWNED BY; Schema: np_users; Owner: postgres
--

ALTER SEQUENCE protein_lists_list_id_seq OWNED BY protein_lists.list_id;


--
-- TOC entry 321 (class 1259 OID 493954)
-- Name: user_applications; Type: TABLE; Schema: np_users; Owner: postgres; Tablespace: 
--

CREATE TABLE user_applications (
    application_id bigint NOT NULL,
    application_name character varying(100) NOT NULL,
    description character varying(100) NOT NULL,
    organisation character varying(100),
    responsible_name character varying(100) NOT NULL,
    responsible_email character varying(100) NOT NULL,
    website character varying(100),
    owner character varying(100) NOT NULL,
    token character varying(1024) NOT NULL,
    state character varying(10)
);


ALTER TABLE np_users.user_applications OWNER TO postgres;

--
-- TOC entry 320 (class 1259 OID 493952)
-- Name: user_applications_application_id_seq; Type: SEQUENCE; Schema: np_users; Owner: postgres
--

CREATE SEQUENCE user_applications_application_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE np_users.user_applications_application_id_seq OWNER TO postgres;

--
-- TOC entry 3267 (class 0 OID 0)
-- Dependencies: 320
-- Name: user_applications_application_id_seq; Type: SEQUENCE OWNED BY; Schema: np_users; Owner: postgres
--

ALTER SEQUENCE user_applications_application_id_seq OWNED BY user_applications.application_id;


--
-- TOC entry 173 (class 1259 OID 198609)
-- Name: users; Type: TABLE; Schema: np_users; Owner: postgres; Tablespace: 
--

CREATE TABLE users (
    user_id integer NOT NULL,
    username character varying(200)
);


ALTER TABLE np_users.users OWNER TO postgres;

--
-- TOC entry 172 (class 1259 OID 198607)
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: np_users; Owner: postgres
--

CREATE SEQUENCE users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE np_users.users_user_id_seq OWNER TO postgres;

--
-- TOC entry 3268 (class 0 OID 0)
-- Dependencies: 172
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: np_users; Owner: postgres
--

ALTER SEQUENCE users_user_id_seq OWNED BY users.user_id;


--
-- TOC entry 3236 (class 2604 OID 198624)
-- Name: list_id; Type: DEFAULT; Schema: np_users; Owner: postgres
--

ALTER TABLE ONLY protein_lists ALTER COLUMN list_id SET DEFAULT nextval('protein_lists_list_id_seq'::regclass);


--
-- TOC entry 3237 (class 2604 OID 493957)
-- Name: application_id; Type: DEFAULT; Schema: np_users; Owner: postgres
--

ALTER TABLE ONLY user_applications ALTER COLUMN application_id SET DEFAULT nextval('user_applications_application_id_seq'::regclass);


--
-- TOC entry 3235 (class 2604 OID 198612)
-- Name: user_id; Type: DEFAULT; Schema: np_users; Owner: postgres
--

ALTER TABLE ONLY users ALTER COLUMN user_id SET DEFAULT nextval('users_user_id_seq'::regclass);


--
-- TOC entry 3259 (class 0 OID 203406)
-- Dependencies: 177
-- Data for Name: list_protein_assoc; Type: TABLE DATA; Schema: np_users; Owner: postgres
--

COPY list_protein_assoc (list_id, protein_id) FROM stdin;
\.


--
-- TOC entry 3258 (class 0 OID 198645)
-- Dependencies: 176
-- Data for Name: np_accessions; Type: TABLE DATA; Schema: np_users; Owner: postgres
--

COPY np_accessions (identifier_id, unique_name, display_name) FROM stdin;
\.


--
-- TOC entry 3257 (class 0 OID 198621)
-- Dependencies: 175
-- Data for Name: protein_lists; Type: TABLE DATA; Schema: np_users; Owner: postgres
--

COPY protein_lists (list_id, name, description, owner_id) FROM stdin;
\.


--
-- TOC entry 3269 (class 0 OID 0)
-- Dependencies: 174
-- Name: protein_lists_list_id_seq; Type: SEQUENCE SET; Schema: np_users; Owner: postgres
--

SELECT pg_catalog.setval('protein_lists_list_id_seq', 579, true);


--
-- TOC entry 3261 (class 0 OID 493954)
-- Dependencies: 321
-- Data for Name: user_applications; Type: TABLE DATA; Schema: np_users; Owner: postgres
--

COPY user_applications (application_id, application_name, description, organisation, responsible_name, responsible_email, website, owner, token, state) FROM stdin;
\.


--
-- TOC entry 3270 (class 0 OID 0)
-- Dependencies: 320
-- Name: user_applications_application_id_seq; Type: SEQUENCE SET; Schema: np_users; Owner: postgres
--

SELECT pg_catalog.setval('user_applications_application_id_seq', 1, false);


--
-- TOC entry 3255 (class 0 OID 198609)
-- Dependencies: 173
-- Data for Name: users; Type: TABLE DATA; Schema: np_users; Owner: postgres
--

COPY users (user_id, username) FROM stdin;
\.


--
-- TOC entry 3271 (class 0 OID 0)
-- Dependencies: 172
-- Name: users_user_id_seq; Type: SEQUENCE SET; Schema: np_users; Owner: postgres
--

SELECT pg_catalog.setval('users_user_id_seq', 536, true);


--
-- TOC entry 3248 (class 2606 OID 203410)
-- Name: list_protein_assoc_pkey; Type: CONSTRAINT; Schema: np_users; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY list_protein_assoc
    ADD CONSTRAINT list_protein_assoc_pkey PRIMARY KEY (list_id, protein_id);


--
-- TOC entry 3246 (class 2606 OID 198659)
-- Name: np_accessions_pkey; Type: CONSTRAINT; Schema: np_users; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY np_accessions
    ADD CONSTRAINT np_accessions_pkey PRIMARY KEY (identifier_id);


--
-- TOC entry 3250 (class 2606 OID 493962)
-- Name: pk_user_applications; Type: CONSTRAINT; Schema: np_users; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY user_applications
    ADD CONSTRAINT pk_user_applications PRIMARY KEY (application_id);


--
-- TOC entry 3243 (class 2606 OID 198629)
-- Name: protein_lists_pkey; Type: CONSTRAINT; Schema: np_users; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY protein_lists
    ADD CONSTRAINT protein_lists_pkey PRIMARY KEY (list_id);


--
-- TOC entry 3239 (class 2606 OID 203424)
-- Name: username_u; Type: CONSTRAINT; Schema: np_users; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT username_u UNIQUE (username);


--
-- TOC entry 3241 (class 2606 OID 198614)
-- Name: users_pkey; Type: CONSTRAINT; Schema: np_users; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (user_id);


--
-- TOC entry 3244 (class 1259 OID 409694)
-- Name: protein_lists_unique_listname_user; Type: INDEX; Schema: np_users; Owner: postgres; Tablespace: 
--

CREATE UNIQUE INDEX protein_lists_unique_listname_user ON protein_lists USING btree (name, owner_id);


--
-- TOC entry 3253 (class 2606 OID 203411)
-- Name: list_protein_assoc_list_id_fkey; Type: FK CONSTRAINT; Schema: np_users; Owner: postgres
--

ALTER TABLE ONLY list_protein_assoc
    ADD CONSTRAINT list_protein_assoc_list_id_fkey FOREIGN KEY (list_id) REFERENCES protein_lists(list_id);


--
-- TOC entry 3252 (class 2606 OID 203416)
-- Name: list_protein_assoc_protein_id_fkey; Type: FK CONSTRAINT; Schema: np_users; Owner: postgres
--

ALTER TABLE ONLY list_protein_assoc
    ADD CONSTRAINT list_protein_assoc_protein_id_fkey FOREIGN KEY (protein_id) REFERENCES np_accessions(identifier_id);


--
-- TOC entry 3251 (class 2606 OID 198630)
-- Name: protein_lists_owner_id_fkey; Type: FK CONSTRAINT; Schema: np_users; Owner: postgres
--

ALTER TABLE ONLY protein_lists
    ADD CONSTRAINT protein_lists_owner_id_fkey FOREIGN KEY (owner_id) REFERENCES users(user_id);


-- Completed on 2014-09-15 16:51:08 CEST

--
-- PostgreSQL database dump complete
--

