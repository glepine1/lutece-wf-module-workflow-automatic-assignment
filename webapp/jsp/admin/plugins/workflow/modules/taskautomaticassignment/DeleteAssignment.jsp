<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:useBean id="workflowAutomaticAssignment" scope="session" class="fr.paris.lutece.plugins.workflow.modules.taskautomaticassignment.web.AutomaticAssignmentJspBean" />
<% 
	workflowAutomaticAssignment.init( request, fr.paris.lutece.plugins.workflow.web.ManagePluginWorkflowJspBean.RIGHT_MANAGE_WORKFLOW); 
	response.sendRedirect( workflowAutomaticAssignment.doDeleteAssignment(request) );
%>
