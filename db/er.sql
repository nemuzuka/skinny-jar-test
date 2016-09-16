
/* Drop Tables */

DROP TABLE IF EXISTS staff;


/* Create Tables */

-- 社員
CREATE TABLE staff
(
	-- id(自動採番)
	id bigserial NOT NULL,
	-- 社員名
	staff_name varchar(256) NOT NULL,
	PRIMARY KEY (id)
) WITHOUT OIDS;

COMMENT ON TABLE staff IS '社員';
COMMENT ON COLUMN staff.id IS 'id(自動採番)';
COMMENT ON COLUMN staff.staff_name IS '社員名';



