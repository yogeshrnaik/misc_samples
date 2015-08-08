prompt PL/SQL Developer import file
prompt Created on Thursday, November 29, 2007 by yogeshn
set feedback off
set define off
prompt Dropping EVENTS...
drop table EVENTS cascade constraints;
prompt Dropping PERSON...
drop table PERSON cascade constraints;
prompt Dropping PERSON_EMAIL_ADDR...
drop table PERSON_EMAIL_ADDR cascade constraints;
prompt Dropping PERSON_EVENT...
drop table PERSON_EVENT cascade constraints;
prompt Creating EVENTS...
create table EVENTS
(
  EVENT_ID   NUMBER(19) not null,
  EVENT_DATE DATE,
  TITLE      VARCHAR2(255)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
alter table EVENTS
  add primary key (EVENT_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt Creating PERSON...
create table PERSON
(
  PERSON_ID NUMBER not null,
  AGE       NUMBER,
  FIRSTNAME VARCHAR2(50),
  LASTNAME  VARCHAR2(50)
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
alter table PERSON
  add constraint PK_PERSON_ID primary key (PERSON_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt Creating PERSON_EMAIL_ADDR...
create table PERSON_EMAIL_ADDR
(
  PERSON_ID  NUMBER not null,
  EMAIL_ADDR VARCHAR2(100) not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
alter table PERSON_EMAIL_ADDR
  add constraint PK_PERSON_EMAIL_ADDR primary key (PERSON_ID,EMAIL_ADDR)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt Creating PERSON_EVENT...
create table PERSON_EVENT
(
  EVENT_ID  NUMBER not null,
  PERSON_ID NUMBER not null
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );
alter table PERSON_EVENT
  add constraint PK_PERSON_EVENT_ID primary key (PERSON_ID,EVENT_ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );

prompt Disabling triggers for EVENTS...
alter table EVENTS disable all triggers;
prompt Disabling triggers for PERSON...
alter table PERSON disable all triggers;
prompt Disabling triggers for PERSON_EMAIL_ADDR...
alter table PERSON_EMAIL_ADDR disable all triggers;
prompt Disabling triggers for PERSON_EVENT...
alter table PERSON_EVENT disable all triggers;
prompt Loading EVENTS...
insert into EVENTS (EVENT_ID, EVENT_DATE, TITLE)
values (1, to_date('29-11-2007 17:05:27', 'dd-mm-yyyy hh24:mi:ss'), 'My Event');
insert into EVENTS (EVENT_ID, EVENT_DATE, TITLE)
values (2, to_date('29-11-2007 17:07:10', 'dd-mm-yyyy hh24:mi:ss'), 'My Event');
insert into EVENTS (EVENT_ID, EVENT_DATE, TITLE)
values (3, to_date('29-11-2007 17:52:57', 'dd-mm-yyyy hh24:mi:ss'), 'My Event');
insert into EVENTS (EVENT_ID, EVENT_DATE, TITLE)
values (4, to_date('29-11-2007 17:53:43', 'dd-mm-yyyy hh24:mi:ss'), 'My Event');
commit;
prompt 4 records loaded
prompt Loading PERSON...
insert into PERSON (PERSON_ID, AGE, FIRSTNAME, LASTNAME)
values (7, 26, 'Yogesh', 'Naik');
insert into PERSON (PERSON_ID, AGE, FIRSTNAME, LASTNAME)
values (8, 27, 'John', 'Mclane');
commit;
prompt 2 records loaded
prompt Loading PERSON_EMAIL_ADDR...
insert into PERSON_EMAIL_ADDR (PERSON_ID, EMAIL_ADDR)
values (7, 'yogesh.naik@iflexsolutions.com');
commit;
prompt 1 records loaded
prompt Loading PERSON_EVENT...
insert into PERSON_EVENT (EVENT_ID, PERSON_ID)
values (1, 7);
commit;
prompt 1 records loaded
prompt Enabling triggers for EVENTS...
alter table EVENTS enable all triggers;
prompt Enabling triggers for PERSON...
alter table PERSON enable all triggers;
prompt Enabling triggers for PERSON_EMAIL_ADDR...
alter table PERSON_EMAIL_ADDR enable all triggers;
prompt Enabling triggers for PERSON_EVENT...
alter table PERSON_EVENT enable all triggers;
set feedback on
set define on
prompt Done.
