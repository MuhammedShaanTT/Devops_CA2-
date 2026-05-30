<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>SmartNotes AI</title>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Material Icons -->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!-- Bootstrap 5 -->
    <link href="//cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>

<!-- Mobile Menu Button -->
<button class="mobile-menu-btn" id="mobile-menu-btn">
    <span class="material-icons">menu</span>
</button>

<!-- Sidebar Overlay (Mobile) -->
<div class="sidebar-overlay" id="sidebar-overlay"></div>

<div class="app-container">

    <!-- ═══════════════ SIDEBAR ═══════════════ -->
    <aside class="sidebar">
        <div class="sidebar-header">
            <h1>SmartNotes<span class="ai-badge">AI</span></h1>
            <a href="${pageContext.request.contextPath}/add" class="new-note-btn">
                <span class="material-icons">add</span> New
            </a>
        </div>

        <!-- Search Bar -->
        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/search" method="get" autocomplete="off">
                <span class="material-icons search-icon">search</span>
                <input type="text" name="query" placeholder="Search notes..."
                       value="${fn:escapeXml(searchQuery)}">
            </form>
        </div>

        <!-- Search Results Header -->
        <c:if test="${not empty searchQuery}">
            <div class="search-results-header">
                <span>Results for "<strong>${fn:escapeXml(searchQuery)}</strong>"</span>
                <a href="${pageContext.request.contextPath}/dashboard">Clear</a>
            </div>
        </c:if>

        <!-- Notes List -->
        <div class="notes-list">
            <c:choose>
                <c:when test="${not empty notes}">
                    <c:forEach items="${notes}" var="note">
                        <a href="${pageContext.request.contextPath}/update?id=${note.id}" class="note-item">
                            <div class="note-title">
                                <c:choose>
                                    <c:when test="${not empty note.title}">${fn:escapeXml(note.title)}</c:when>
                                    <c:otherwise>Untitled Note</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="note-preview">
                                <c:choose>
                                    <c:when test="${fn:length(note.content) > 80}">
                                        ${fn:escapeXml(fn:substring(note.content, 0, 80))}...
                                    </c:when>
                                    <c:when test="${not empty note.content}">
                                        ${fn:escapeXml(note.content)}
                                    </c:when>
                                    <c:otherwise>No additional text</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="note-date" data-date="${note.updatedAt}">
                                ${note.updatedAt}
                            </div>
                        </a>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-state" style="padding: 40px 20px;">
                        <span class="material-icons" style="font-size: 40px;">description</span>
                        <p style="margin-top: 12px; font-size: 14px;">
                            <c:choose>
                                <c:when test="${not empty searchQuery}">No notes match your search</c:when>
                                <c:otherwise>No notes yet</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Notes Count -->
        <c:if test="${not empty notes}">
            <div class="notes-count">${fn:length(notes)} note<c:if test="${fn:length(notes) != 1}">s</c:if></div>
        </c:if>
    </aside>

    <!-- ═══════════════ MAIN CONTENT ═══════════════ -->
    <main class="content">
        <div class="empty-state">
            <span class="material-icons">note_add</span>
            <h2>Select a note or create a new one</h2>
            <p>Your notes will appear in the sidebar. Click one to view or edit it.</p>
            <a href="${pageContext.request.contextPath}/add" class="btn btn-primary">
                <span class="material-icons">add</span> Create Note
            </a>
        </div>
    </main>
</div>

<!-- ═══════════════ AI RESULT MODAL ═══════════════ -->
<div class="modal-backdrop-custom" id="ai-modal">
    <div class="modal-dialog-custom">
        <div class="modal-header-custom">
            <h3>
                <span class="material-icons" id="ai-modal-icon">auto_awesome</span>
                <span id="ai-modal-title">AI Result</span>
            </h3>
            <button class="modal-close-btn" onclick="hideAiModal()">
                <span class="material-icons">close</span>
            </button>
        </div>
        <div class="modal-body-custom" id="ai-modal-body">
            <div class="ai-result-content">Results will appear here...</div>
        </div>
        <div class="modal-footer-custom">
            <button class="btn btn-outline" onclick="hideAiModal()">Close</button>
        </div>
    </div>
</div>

<!-- Toast Container -->
<div class="toast-container" id="toast-container"></div>

<!-- Server Messages (for JSP-originated toasts) -->
<c:if test="${not empty message}">
    <div id="server-message" data-message="${fn:escapeXml(message)}" data-type="${not empty messageType ? messageType : 'success'}" style="display:none;"></div>
</c:if>

<!-- Bootstrap JS -->
<script src="//cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<!-- App JS -->
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
