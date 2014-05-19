<%@page import="fr.paris.lutece.plugins.workflow.web.WorkflowJspBean"%>
<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:useBean id="workflowAutomaticAssignment" scope="session" class="fr.paris.lutece.plugins.workflow.modules.automaticassignment.web.AutomaticAssignmentJspBean" />
<% 
	workflowAutomaticAssignment.init( request, WorkflowJspBean.RIGHT_MANAGE_WORKFLOW); 
	response.sendRedirect( workflowAutomaticAssignment.doDeleteAssignment(request) );
%>
