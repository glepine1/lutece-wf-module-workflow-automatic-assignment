--
-- WORKFLOWAUTOMATICASSIGNMENT-13 : Add a recipients list for blind carbon copies
--
ALTER TABLE workflow_auto_assignment_cf ADD COLUMN recipients_cc VARCHAR(255) DEFAULT '' NOT NULL;
ALTER TABLE workflow_auto_assignment_cf ADD COLUMN recipients_bcc VARCHAR(255) DEFAULT '' NOT NULL;
