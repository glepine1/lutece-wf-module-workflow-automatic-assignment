
ALTER TABLE workflow_auto_assignment_cf MODIFY message LONG VARCHAR DEFAULT NULL;

ALTER TABLE workflow_auto_assignment_cf MODIFY subject VARCHAR(255) DEFAULT NULL;

ALTER TABLE workflow_auto_assignment_cf ADD sender_name VARCHAR(255) DEFAULT NULL;