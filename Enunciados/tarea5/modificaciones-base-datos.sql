
alter table foto add column eliminada TINYINT(1) not null default 0;

CREATE TABLE log (
    id                  int8           auto_increment,
    fecha               datetime       not null DEFAULT CURRENT_TIMESTAMP,
    mensaje             varchar(300)    not null,
    primary key(id)
) 
ENGINE = INNODB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_bin;


