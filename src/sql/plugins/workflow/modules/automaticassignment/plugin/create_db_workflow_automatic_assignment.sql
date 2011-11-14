DROP TABLE IF EXISTS workflow_auto_assignment;
DROP TABLE IF EXISTS workflow_auto_assignment_cf;

/*==============================================================*/
/* Table structure for table workflow_auto_assignment			*/
/*==============================================================*/

CREATE TABLE  workflow_auto_assignment (
  id_task INT  NOT NULL ,
  id_entry INT  NOT NULL ,
  value INT  NOT NULL ,
  workgroup_key varchar(255) NOT NULL,
  PRIMARY KEY  (id_task,id_entry,value,workgroup_key)
) ;

/*==============================================================*/
/* Table structure for table workflow_auto_assignment_cf		*/
/*==============================================================*/

CREATE TABLE  workflow_auto_assignment_cf (
  id_task INT  NOT NULL ,
  id_directory INT  NOT NULL ,
  title VARCHAR(255) DEFAULT NULL, 
  is_notify SMALLINT DEFAULT 0,
  sender_name VARCHAR(255) DEFAULT NULL, 
  message LONG VARCHAR DEFAULT NULL,
  subject VARCHAR(255) DEFAULT NULL,
  is_view_record SMALLINT(6) NOT NULL DEFAULT 0,
  label_link_view_record VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY  (id_task)
) ;