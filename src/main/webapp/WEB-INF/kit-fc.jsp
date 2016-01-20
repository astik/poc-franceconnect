<% if (session.getAttribute("userInfo") != null) { %>
<script src="http://fcp.integ01.dev-franceconnect.fr/js/franceconnect.js"></script>
<div id="fconnect-profile" data-fc-logout-url="/poc-fc/logout">
    <a href="#"> le nom de l'utilisateur connecté* </a>
</div>
<% } %>