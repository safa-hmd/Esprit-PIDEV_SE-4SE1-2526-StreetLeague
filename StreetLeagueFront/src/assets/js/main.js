// ============================================================
//  StreetLeague – FrontOffice Main JS
//  Utilisé sur toutes les pages du FrontOffice
// ============================================================

/* ── Active Nav Link ── */
(function() {
    const current = window.location.pathname.split('/').pop() || 'index.html';
    document.querySelectorAll('.nav-link, .navbar-mobile a').forEach(link => {
        const href = link.getAttribute('href') || '';
        if (href === current || (current === '' && href === 'index.html')) {
            link.classList.add('active');
        }
    });
})();

/* ── Mobile Menu ── */
const mobileBtn = document.getElementById('mobileMenuBtn');
const mobileMenu = document.getElementById('mobileMenu');
if (mobileBtn && mobileMenu) {
    mobileBtn.addEventListener('click', () => {
        mobileMenu.classList.toggle('open');
        const icon = mobileBtn.querySelector('svg use, svg path');
    });
}

/* ── Modal Helpers ── */
function openModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.add('open');
    document.body.style.overflow = 'hidden';
}

function closeModal(id) {
    const el = document.getElementById(id);
    if (el) el.classList.remove('open');
    document.body.style.overflow = '';
}

// Close modal on overlay click
document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', function(e) {
        if (e.target === this) {
            this.classList.remove('open');
            document.body.style.overflow = '';
        }
    });
});

// Close buttons
document.querySelectorAll('[data-close-modal]').forEach(btn => {
    btn.addEventListener('click', () => {
        const modalId = btn.getAttribute('data-close-modal');
        closeModal(modalId);
    });
});

/* ── Search Filter ── */
function initSearch(inputId, itemsSelector, searchAttr) {
    const input = document.getElementById(inputId);
    if (!input) return;
    input.addEventListener('input', function() {
        const q = this.value.toLowerCase();
        document.querySelectorAll(itemsSelector).forEach(item => {
            const text = (item.getAttribute(searchAttr) || item.textContent || '').toLowerCase();
            item.style.display = text.includes(q) ? '' : 'none';
        });
    });
}

/* ── Tabs ── */
function initTabs(tabsContainerId) {
    const container = document.getElementById(tabsContainerId);
    if (!container) return;
    const tabs = container.querySelectorAll('[data-tab]');
    const panes = container.querySelectorAll('[data-pane]');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const target = tab.getAttribute('data-tab');
            tabs.forEach(t => t.classList.remove('active'));
            panes.forEach(p => p.classList.remove('active'));
            tab.classList.add('active');
            const pane = container.querySelector(`[data-pane="${target}"]`);
            if (pane) pane.classList.add('active');
        });
    });
}

/* ── Toast Notification ── */
function showToast(message, type = 'success') {
    const existing = document.getElementById('sl-toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.id = 'sl-toast';
    toast.style.cssText = `
    position: fixed; bottom: 1.5rem; right: 1.5rem; z-index: 9999;
    padding: 0.85rem 1.25rem; border-radius: 10px;
    font-family: 'Barlow', sans-serif; font-weight: 600; font-size: 0.9rem;
    background: ${type === 'success' ? '#166534' : '#991b1b'};
    color: white; box-shadow: 0 8px 30px rgba(0,0,0,0.2);
    transform: translateY(10px); opacity: 0;
    transition: all 0.3s;
  `;
    toast.textContent = message;
    document.body.appendChild(toast);
    requestAnimationFrame(() => {
        toast.style.transform = 'translateY(0)';
        toast.style.opacity = '1';
    });
    setTimeout(() => {
        toast.style.transform = 'translateY(10px)';
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Assurez-vous que logoutBtn n'est déclaré qu'une seule fois
const logoutBtn = document.getElementById('logoutBtn');

if (logoutBtn) {
    logoutBtn.addEventListener('click', function() {
        // Votre logique de logout
        localStorage.removeItem('token');
        window.location.href = '/login';
    });
}

/* ── User Name Display ── */
const userNameEl = document.getElementById('userName');
if (userNameEl) {
    try {
        const user = JSON.parse(localStorage.getItem('streetleague_user') || '{}');
        if (user.name) userNameEl.textContent = user.name;
    } catch {}
}