/* ============================================
   SmartNotes AI — Application JavaScript
   ============================================ */

(function () {
  'use strict';

  // ─── Context Path ───
  const CTX = '';  // ROOT deployment, context path is empty

  // ─── DOM Ready ───
  document.addEventListener('DOMContentLoaded', function () {
    initSidebar();
    initTextareaAutoResize();
    initSearchForm();
    initLanguageDropdown();
    formatAllDates();
    initToastsFromServer();
    closeDropdownsOnClickOutside();
  });

  /* =============================================
     Toast Notification System
     ============================================= */

  function getToastContainer() {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      container.className = 'toast-container';
      document.body.appendChild(container);
    }
    return container;
  }

  window.showToast = function (message, type) {
    type = type || 'info';
    const container = getToastContainer();

    const icons = {
      success: 'check_circle',
      error: 'error',
      info: 'info'
    };

    const toast = document.createElement('div');
    toast.className = 'toast toast-' + type;
    toast.innerHTML =
      '<span class="material-icons toast-icon">' + (icons[type] || 'info') + '</span>' +
      '<span>' + escapeHtml(message) + '</span>';

    container.appendChild(toast);

    // Auto-dismiss after 3 seconds
    setTimeout(function () {
      toast.classList.add('toast-exit');
      setTimeout(function () {
        if (toast.parentNode) toast.parentNode.removeChild(toast);
      }, 300);
    }, 3000);
  };

  function initToastsFromServer() {
    var msgEl = document.getElementById('server-message');
    if (msgEl) {
      var msg = msgEl.getAttribute('data-message');
      var type = msgEl.getAttribute('data-type') || 'info';
      if (msg) showToast(msg, type);
    }
  }

  /* =============================================
     AI Feature — Summarize
     ============================================= */

  window.summarizeNote = function (text) {
    if (!text || !text.trim()) {
      showToast('Note content is empty. Write something first.', 'error');
      return;
    }

    showAiModal('Summarize', 'auto_awesome');
    setAiModalLoading(true);

    fetch(CTX + '/summarize', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text: text })
    })
      .then(handleResponse)
      .then(function (data) {
        setAiModalLoading(false);
        setAiModalContent(data.summary || data.result || JSON.stringify(data));
      })
      .catch(function (err) {
        setAiModalLoading(false);
        setAiModalContent('An error occurred while summarizing. Please try again.\n\nError: ' + err.message);
        showToast('Summarization failed', 'error');
      });
  };

  /* =============================================
     AI Feature — Translate
     ============================================= */

  window.translateNote = function (text, language) {
    if (!text || !text.trim()) {
      showToast('Note content is empty. Write something first.', 'error');
      return;
    }
    if (!language) {
      showToast('Please select a language.', 'error');
      return;
    }

    showAiModal('Translate to ' + language, 'translate');
    setAiModalLoading(true);

    fetch(CTX + '/translate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ text: text, language: language })
    })
      .then(handleResponse)
      .then(function (data) {
        setAiModalLoading(false);
        setAiModalContent(data.translatedText || data.translation || data.result || JSON.stringify(data));
      })
      .catch(function (err) {
        setAiModalLoading(false);
        setAiModalContent('An error occurred while translating. Please try again.\n\nError: ' + err.message);
        showToast('Translation failed', 'error');
      });
  };

  /* =============================================
     AI Feature — Generate Study Notes
     ============================================= */

  window.generateStudyNotes = function (topic) {
    if (!topic || !topic.trim()) {
      showToast('Note content is empty. Write something first.', 'error');
      return;
    }

    showAiModal('Study Notes', 'school');
    setAiModalLoading(true);

    fetch(CTX + '/generate-study-notes', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ topic: topic })
    })
      .then(handleResponse)
      .then(function (data) {
        setAiModalLoading(false);
        setAiModalContent(data.studyNotes || data.notes || data.result || JSON.stringify(data));
      })
      .catch(function (err) {
        setAiModalLoading(false);
        setAiModalContent('An error occurred while generating study notes. Please try again.\n\nError: ' + err.message);
        showToast('Study notes generation failed', 'error');
      });
  };

  /* =============================================
     AI Modal Management
     ============================================= */

  window.showAiModal = function (title, icon) {
    var modal = document.getElementById('ai-modal');
    if (!modal) return;

    var titleEl = document.getElementById('ai-modal-title');
    var iconEl = document.getElementById('ai-modal-icon');

    if (titleEl) titleEl.textContent = title || 'AI Result';
    if (iconEl) iconEl.textContent = icon || 'auto_awesome';

    modal.classList.add('show');
    document.body.style.overflow = 'hidden';
  };

  window.hideAiModal = function () {
    var modal = document.getElementById('ai-modal');
    if (!modal) return;

    modal.classList.remove('show');
    document.body.style.overflow = '';
  };

  window.setAiModalLoading = function (loading) {
    var body = document.getElementById('ai-modal-body');
    if (!body) return;

    if (loading) {
      body.innerHTML =
        '<div class="loading-container">' +
        '  <div class="spinner"></div>' +
        '  <p>AI is thinking...</p>' +
        '</div>';
    }
  };

  window.setAiModalContent = function (content) {
    var body = document.getElementById('ai-modal-body');
    if (!body) return;

    // Use marked.js if available to render Markdown, otherwise fallback to plain text
    var htmlContent = '';
    if (typeof marked !== 'undefined') {
      htmlContent = marked.parse(content);
    } else {
      htmlContent = escapeHtml(content);
    }

    body.innerHTML = '<div class="ai-result-content markdown-body">' + htmlContent + '</div>';
  };

  // Close modal on backdrop click
  document.addEventListener('click', function (e) {
    if (e.target && e.target.id === 'ai-modal') {
      hideAiModal();
    }
  });

  // Close modal on Escape key
  document.addEventListener('keydown', function (e) {
    if (e.key === 'Escape') {
      hideAiModal();
      hideDeleteConfirm();
    }
  });

  /* =============================================
     Delete Confirmation
     ============================================= */

  window.showDeleteConfirm = function (noteId) {
    var dialog = document.getElementById('delete-confirm');
    if (!dialog) return;

    var form = document.getElementById('delete-form');
    if (form) {
      form.action = CTX + '/delete?id=' + noteId;
    }

    dialog.classList.add('show');
    document.body.style.overflow = 'hidden';
  };

  window.hideDeleteConfirm = function () {
    var dialog = document.getElementById('delete-confirm');
    if (!dialog) return;

    dialog.classList.remove('show');
    document.body.style.overflow = '';
  };

  window.confirmDelete = function () {
    var form = document.getElementById('delete-form');
    if (form) form.submit();
  };

  /* =============================================
     Sidebar
     ============================================= */

  function initSidebar() {
    var menuBtn = document.getElementById('mobile-menu-btn');
    var sidebar = document.querySelector('.sidebar');
    var overlay = document.getElementById('sidebar-overlay');

    if (menuBtn && sidebar) {
      menuBtn.addEventListener('click', function () {
        sidebar.classList.toggle('open');
        if (overlay) overlay.classList.toggle('show');
      });
    }

    if (overlay) {
      overlay.addEventListener('click', function () {
        if (sidebar) sidebar.classList.remove('open');
        overlay.classList.remove('show');
      });
    }

    // Highlight active note in sidebar
    highlightActiveNote();
  }

  function highlightActiveNote() {
    var noteItems = document.querySelectorAll('.note-item');
    var currentUrl = window.location.href;

    noteItems.forEach(function (item) {
      var href = item.getAttribute('href');
      if (href && currentUrl.indexOf(href) !== -1 && href.indexOf('id=') !== -1) {
        item.classList.add('active');
      }
    });
  }

  /* =============================================
     Textarea Auto-Resize
     ============================================= */

  function initTextareaAutoResize() {
    var textareas = document.querySelectorAll('.note-content-textarea');
    textareas.forEach(function (textarea) {
      autoResize(textarea);
      textarea.addEventListener('input', function () {
        autoResize(textarea);
      });
    });
  }

  function autoResize(textarea) {
    textarea.style.height = 'auto';
    var computed = Math.max(textarea.scrollHeight, 300);
    textarea.style.height = computed + 'px';
  }

  /* =============================================
     Search
     ============================================= */

  function initSearchForm() {
    var searchInput = document.querySelector('.search-bar input');
    if (searchInput) {
      // Add clear-on-escape
      searchInput.addEventListener('keydown', function (e) {
        if (e.key === 'Escape') {
          searchInput.value = '';
          searchInput.blur();
        }
      });
    }
  }

  /* =============================================
     Language Dropdown
     ============================================= */

  function initLanguageDropdown() {
    var toggleBtn = document.getElementById('translate-toggle-btn');
    var menu = document.getElementById('language-menu');

    if (toggleBtn && menu) {
      toggleBtn.addEventListener('click', function (e) {
        e.stopPropagation();
        menu.classList.toggle('show');
      });
    }
  }

  function closeDropdownsOnClickOutside() {
    document.addEventListener('click', function () {
      var menus = document.querySelectorAll('.language-dropdown-menu.show');
      menus.forEach(function (m) { m.classList.remove('show'); });
    });
  }

  window.selectLanguage = function (language) {
    var contentEl = document.getElementById('note-content-field');
    var text = '';
    if (contentEl) {
      text = contentEl.value || contentEl.textContent || '';
    }

    // Close dropdown
    var menu = document.getElementById('language-menu');
    if (menu) menu.classList.remove('show');

    translateNote(text.trim(), language);
  };

  /* =============================================
     Date Formatting
     ============================================= */

  function formatAllDates() {
    var dateEls = document.querySelectorAll('.note-date, .note-card-date, [data-date]');
    dateEls.forEach(function (el) {
      var raw = el.getAttribute('data-date') || el.textContent.trim();
      var formatted = formatDate(raw);
      if (formatted) el.textContent = formatted;
    });
  }

  function formatDate(dateStr) {
    if (!dateStr) return '';
    try {
      var date = new Date(dateStr);
      if (isNaN(date.getTime())) return dateStr;

      var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
        'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
      var now = new Date();
      var isToday = date.toDateString() === now.toDateString();

      if (isToday) {
        var hours = date.getHours();
        var minutes = date.getMinutes();
        var ampm = hours >= 12 ? 'PM' : 'AM';
        hours = hours % 12 || 12;
        minutes = minutes < 10 ? '0' + minutes : minutes;
        return 'Today, ' + hours + ':' + minutes + ' ' + ampm;
      }

      var yesterday = new Date(now);
      yesterday.setDate(yesterday.getDate() - 1);
      if (date.toDateString() === yesterday.toDateString()) {
        return 'Yesterday';
      }

      return months[date.getMonth()] + ' ' + date.getDate() + ', ' + date.getFullYear();
    } catch (e) {
      return dateStr;
    }
  }

  /* =============================================
     Helpers
     ============================================= */

  function handleResponse(response) {
    if (!response.ok) {
      throw new Error('Server returned ' + response.status);
    }
    return response.json();
  }

  function escapeHtml(str) {
    if (!str) return '';
    var div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
  }

  /* =============================================
     Get note content helper (used by onclick)
     ============================================= */

  window.getNoteContent = function () {
    var el = document.getElementById('note-content-field');
    if (el) return (el.value || el.textContent || '').trim();
    return '';
  };

  window.getNoteTitle = function () {
    var el = document.getElementById('note-title-field');
    if (el) return (el.value || el.textContent || '').trim();
    return '';
  };

})();
