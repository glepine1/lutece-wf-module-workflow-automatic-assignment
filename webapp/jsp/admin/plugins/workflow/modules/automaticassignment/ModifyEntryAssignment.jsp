<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />
<jsp:useBean id="workflowAutomaticAssignment" scope="session" class="fr.paris.lutece.plugins.workflow.modules.automaticassignment.web.AutomaticAssignmentJspBean" />
<% workflowAutomaticAssignment.init( request, fr.paris.lutece.plugins.workflow.web.WorkflowJspBean.RIGHT_MANAGE_WORKFLOW); %>
<%= workflowAutomaticAssignment.getModifyEntryAssignments( request ) %>
<%@ include file="../../../../AdminFooter.jsp" %>