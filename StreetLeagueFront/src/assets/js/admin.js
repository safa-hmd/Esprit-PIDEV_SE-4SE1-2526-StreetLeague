// ============================================================
//  StreetLeague – BackOffice Main JS
// ============================================================

/* ── Sidebar Toggle ── */
const sidebar = document.getElementById('adminSidebar');
const mainContent = document.getElementById('adminMain');
const overlay = document.getElementById('sidebarOverlay');
const menuToggle = document.getElementById('menuToggle');

function isMobile() { return window.innerWidth < 1024; }

function initSidebar() {
    if (isMobile()) {
        sidebar && sidebar.classList.add('collapsed');
        mainContent && mainContent.classList.add('full');
    }
}

menuToggle && menuToggle.addEventListener('click', () => {
    if (isMobile()) {
        sidebar.classList.toggle('collapsed');
        overlay.classList.toggle('open');
    } else {
        sidebar.classList.toggle('collapsed');
        mainContent.classList.toggle('full');
    }
});

overlay && overlay.addEventListener('click', () => {
    sidebar.classList.add('collapsed');
    overlay.classList.remove('open');
});

window.addEventListener('resize', initSidebar);
initSidebar();

/* ── Active Sidebar Link ── */
(function() {
    const current = window.location.pathname.split('/').pop() || 'dashboard.html';
    document.querySelectorAll('.sidebar-link').forEach(link => {
        const href = link.getAttribute('href') || '';
        if (href === current) link.classList.add('active');
    });
})();

/* ── User Name ── */
const userNameEl = document.getElementById('adminUserName');
if (userNameEl) {
    try {
        const user = JSON.parse(localStorage.getItem('streetleague_user') || '{}');
        if (user.name) userNameEl.textContent = user.name;
    } catch {}
}

/* ── Logout ── */
const adminLogoutBtn = document.getElementById('adminLogout');
if (adminLogoutBtn) {
    adminLogoutBtn.addEventListener('click', () => {
        localStorage.removeItem('streetleague_token');
        localStorage.removeItem('streetleague_user');
        window.location.href = '../../login.html';
    });
}

/* ── Modal Helpers ── */
function openModal(id) {
    const el = document.getElementById(id);
    if (el) { el.classList.add('open');
        document.body.style.overflow = 'hidden'; }
}

function closeModal(id) {
    const el = document.getElementById(id);
    if (el) { el.classList.remove('open');
        document.body.style.overflow = ''; }
}
document.querySelectorAll('.a-modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', function(e) {
        if (e.target === this) { this.classList.remove('open');
            document.body.style.overflow = ''; }
    });
});
document.querySelectorAll('[data-close]').forEach(btn => {
    btn.addEventListener('click', () => closeModal(btn.getAttribute('data-close')));
});

/* ── Search Filter ── */
function initSearch(inputId, selector, attr) {
    const inp = document.getElementById(inputId);
    if (!inp) return;
    inp.addEventListener('input', function() {
        const q = this.value.toLowerCase();
        document.querySelectorAll(selector).forEach(el => {
            const txt = (el.getAttribute(attr) || el.textContent || '').toLowerCase();
            el.closest('tr') ? el.closest('tr').style.display = txt.includes(q) ? '' : 'none' :
                el.style.display = txt.includes(q) ? '' : 'none';
        });
    });
}

/* ── Toast ── */
function showToast(msg, type) {
    const t = document.createElement('div');
    t.style.cssText = 'position:fixed;bottom:1.5rem;right:1.5rem;z-index:9999;padding:0.8rem 1.2rem;border-radius:10px;font-family:Barlow,sans-serif;font-weight:700;font-size:0.88rem;background:' + (type === 'error' ? '#991b1b' : '#166534') + ';color:white;box-shadow:0 8px 30px rgba(0,0,0,0.4);transition:all 0.3s;transform:translateY(10px);opacity:0;';
    t.textContent = msg;
    document.body.appendChild(t);
    requestAnimationFrame(() => { t.style.transform = 'translateY(0)';
        t.style.opacity = '1'; });
    setTimeout(() => { t.style.transform = 'translateY(10px)';
        t.style.opacity = '0';
        setTimeout(() => t.remove(), 300); }, 3000);
}

/* ── Confirm Delete ── */
function confirmDelete(msg) {
    return confirm(msg || 'Supprimer cet élément ?');
}