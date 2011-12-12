--
-- WORKFLOWAUTOMATICASSIGNMENT-13 : Add a recipients list for blind carbon copies
--
ALTER TABLE workflow_auto_assignment_cf ADD COLUMN recipients_cc VARCHAR(255) DEFAULT '' NOT NULL;
ALTER TABLE workflow_auto_assignment_cf ADD COLUMN recipients_bcc VARCHAR(255) DEFAULT '' NOT NULL;

--
-- WORKFLOWAUTOMATICASSIGNMENT-15 : Add the possibility to send files/images of the record as attachments in the notification
--
CREATE TABLE workflow_auto_assignment_ef(
  id_task INT DEFAULT NULL,
  position_directory_entry_file INT DEFAULT NULL,
  PRIMARY KEY (id_task, position_directory_entry_file)
);
