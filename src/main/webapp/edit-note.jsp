<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Note — SmartNotes AI</title>

    <!-- Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <!-- Material Icons -->
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!-- Bootstrap 5 -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
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
                <input type="text" name="query" placeholder="Search notes...">
            </form>
        </div>

        <!-- Notes List -->
        <div class="notes-list">
            <c:choose>
                <c:when test="${not empty notes}">
                    <c:forEach items="${notes}" var="n">
                        <a href="${pageContext.request.contextPath}/update?id=${n.id}"
                           class="note-item <c:if test='${n.id == note.id}'>active</c:if>">
                            <div class="note-title">
                                <c:choose>
                                    <c:when test="${not empty n.title}">${fn:escapeXml(n.title)}</c:when>
                                    <c:otherwise>Untitled Note</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="note-preview">
                                <c:choose>
                                    <c:when test="${fn:length(n.content) > 80}">
                                        ${fn:escapeXml(fn:substring(n.content, 0, 80))}...
                                    </c:when>
                                    <c:when test="${not empty n.content}">
                                        ${fn:escapeXml(n.content)}
                                    </c:when>
                                    <c:otherwise>No additional text</c:otherwise>
                                </c:choose>
                            </div>
                            <div class="note-date" data-date="${n.updatedAt}">
                                ${n.updatedAt}
                            </div>
                        </a>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="empty-state" style="padding: 40px 20px;">
                        <span class="material-icons" style="font-size: 40px;">description</span>
                        <p style="margin-top: 12px; font-size: 14px;">No notes yet</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <c:if test="${not empty notes}">
            <div class="notes-count">${fn:length(notes)} note<c:if test="${fn:length(notes) != 1}">s</c:if></div>
        </c:if>
    </aside>

    <!-- ═══════════════ MAIN CONTENT ═══════════════ -->
    <main class="content">
        <form action="${pageContext.request.contextPath}/update" method="post">
            <input type="hidden" name="id" value="${note.id}">

            <!-- Toolbar -->
            <div class="toolbar">
                <div class="toolbar-left">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-ghost">
                        <span class="material-icons">arrow_back</span>
                        <span>Back</span>
                    </a>

                    <div class="toolbar-divider"></div>

                    <!-- AI Action Buttons -->
                    <button type="button" class="btn btn-ai" onclick="summarizeNote(getNoteContent())" title="Summarize">
                        <span class="material-icons">auto_awesome</span>
                        <span>Summarize</span>
                    </button>

                    <div class="language-dropdown">
                        <button type="button" class="btn btn-ai" id="translate-toggle-btn" title="Translate">
                            <span class="material-icons">translate</span>
                            <span>Translate</span>
                        </button>
                        <div class="language-dropdown-menu" id="language-menu">
                            <button type="button" onclick="selectLanguage('Hindi')">🇮🇳 Hindi</button>
                            <button type="button" onclick="selectLanguage('Malayalam')">🇮🇳 Malayalam</button>
                            <button type="button" onclick="selectLanguage('Tamil')">🇮🇳 Tamil</button>
                            <button type="button" onclick="selectLanguage('Telugu')">🇮🇳 Telugu</button>
                            <button type="button" onclick="selectLanguage('Bengali')">🇮🇳 Bengali</button>
                            <button type="button" onclick="selectLanguage('Spanish')">🇪🇸 Spanish</button>
                            <button type="button" onclick="selectLanguage('French')">🇫🇷 French</button>
                            <button type="button" onclick="selectLanguage('German')">🇩🇪 German</button>
                            <button type="button" onclick="selectLanguage('Arabic')">🇸🇦 Arabic</button>
                            <button type="button" onclick="selectLanguage('Chinese')">🇨🇳 Chinese</button>
                            <button type="button" onclick="selectLanguage('Japanese')">🇯🇵 Japanese</button>
                            <button type="button" onclick="selectLanguage('Korean')">🇰🇷 Korean</button>
                        </div>
                    </div>

                    <button type="button" class="btn btn-ai" onclick="generateStudyNotes(getNoteContent())" title="Generate Study Notes">
                        <span class="material-icons">school</span>
                        <span>Study Notes</span>
                    </button>
                </div>

                <div class="toolbar-right">
                    <button type="button" class="btn btn-danger" onclick="showDeleteConfirm('${note.id}')" title="Delete Note">
                        <span class="material-icons">delete</span>
                        <span>Delete</span>
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <span class="material-icons">save</span>
                        <span>Save</span>
                    </button>
                </div>
            </div>

            <!-- Note Editor -->
            <div class="note-editor">
                <div class="note-editor-body">
                    <input type="text" name="title" id="note-title-field"
                           class="note-title-input" placeholder="Note title"
                           value="${fn:escapeXml(note.title)}" autocomplete="off">
                    <textarea name="content" id="note-content-field"
                              class="note-content-textarea"
                              placeholder="Start writing...">${fn:escapeXml(note.content)}</textarea>
                </div>
            </div>
        </form>
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

<!-- ═══════════════ DELETE CONFIRMATION ═══════════════ -->
<div class="confirm-dialog-backdrop" id="delete-confirm">
    <div class="confirm-dialog">
        <span class="material-icons">warning</span>
        <h3>Delete this note?</h3>
        <p>This action cannot be undone. The note will be permanently removed.</p>
        <div class="confirm-dialog-actions">
            <button class="btn btn-outline" onclick="hideDeleteConfirm()">Cancel</button>
            <form id="delete-form" action="${pageContext.request.contextPath}/delete?id=${note.id}" method="post" style="flex:1;">
                <button type="submit" class="btn btn-danger" style="width:100%;">Delete</button>
            </form>
        </div>
    </div>
</div>

<!-- Toast Container -->
<div class="toast-container" id="toast-container"></div>

<!-- Server Messages -->
<c:if test="${not empty message}">
    <div id="server-message" data-message="${fn:escapeXml(message)}" data-type="${not empty messageType ? messageType : 'success'}" style="display:none;"></div>
</c:if>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<!-- Marked.js for Markdown parsing -->
<script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
<!-- App JS -->
<script src="${pageContext.request.contextPath}/js/app.js"></script>
</body>
</html>
