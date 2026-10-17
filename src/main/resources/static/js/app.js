const API_BASE = '/api';

let currentUser = null;
let currentToken = localStorage.getItem('token') || null;
let allMedia = [];
let allPermissions = [];
let activeCategory = 'ALL';

// Initialize
document.addEventListener('DOMContentLoaded', async () => {
    setupAuthTabs();
    if (currentToken) {
        await fetchCurrentUser();
    } else {
        renderLoggedOutView();
    }
});

// Toast notification helper
function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 4000);
}

// HTTP request helper with JWT header
async function apiFetch(endpoint, options = {}) {
    const headers = options.headers || {};
    headers['Content-Type'] = 'application/json';
    if (currentToken) {
        headers['Authorization'] = `Bearer ${currentToken}`;
    }

    try {
        const res = await fetch(`${API_BASE}${endpoint}`, { ...options, headers });
        const data = await res.json().catch(() => null);

        if (!res.ok) {
            const errorMsg = data?.message || `HTTP error ${res.status}`;
            throw new Error(errorMsg);
        }
        return data;
    } catch (err) {
        showToast(err.message, 'error');
        throw err;
    }
}

// Authentication Tab Toggling
function setupAuthTabs() {
    const tabLogin = document.getElementById('tab-login-btn');
    const tabRegister = document.getElementById('tab-register-btn');
    const formLogin = document.getElementById('login-form');
    const formRegister = document.getElementById('register-form');

    if (!tabLogin) return;

    tabLogin.addEventListener('click', () => {
        tabLogin.classList.add('active');
        tabRegister.classList.remove('active');
        formLogin.style.display = 'block';
        formRegister.style.display = 'none';
    });

    tabRegister.addEventListener('click', () => {
        tabRegister.classList.add('active');
        tabLogin.classList.remove('active');
        formRegister.style.display = 'block';
        formLogin.style.display = 'none';
    });
}

function quickFill(userType) {
    const usernameInput = document.getElementById('login-username');
    const passwordInput = document.getElementById('login-password');
    if (userType === 'admin') {
        usernameInput.value = 'admin@example.com';
        passwordInput.value = 'admin123';
    } else {
        usernameInput.value = 'user@example.com';
        passwordInput.value = 'user123';
    }
}

// Login
async function handleLogin(e) {
    e.preventDefault();
    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;

    try {
        const res = await apiFetch('/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username, password })
        });

        if (res.success) {
            currentToken = res.data.token;
            localStorage.setItem('token', currentToken);
            showToast('Login successful! Welcome ' + res.data.username, 'success');
            await fetchCurrentUser();
        }
    } catch (err) {
        console.error(err);
    }
}

// Register
async function handleRegister(e) {
    e.preventDefault();
    const username = document.getElementById('reg-username').value.trim();
    const email = document.getElementById('reg-email').value.trim();
    const password = document.getElementById('reg-password').value;

    try {
        const res = await apiFetch('/auth/register', {
            method: 'POST',
            body: JSON.stringify({ username, email, password })
        });

        if (res.success) {
            currentToken = res.data.token;
            localStorage.setItem('token', currentToken);
            showToast('Account created successfully!', 'success');
            await fetchCurrentUser();
        }
    } catch (err) {
        console.error(err);
    }
}

// Logout
function handleLogout() {
    currentToken = null;
    currentUser = null;
    localStorage.removeItem('token');
    showToast('Logged out successfully', 'info');
    renderLoggedOutView();
}

// Fetch Logged-in User Profile
async function fetchCurrentUser() {
    try {
        const res = await apiFetch('/auth/me');
        if (res.success) {
            currentUser = res.data;
            renderLoggedInView();
            loadPermissions();
            loadMediaCatalog();
        }
    } catch (err) {
        handleLogout();
    }
}

function renderLoggedOutView() {
    document.getElementById('auth-section').style.display = 'block';
    document.getElementById('user-app-section').style.display = 'none';
    document.getElementById('nav-user-info').style.display = 'none';
    document.getElementById('admin-nav-btn').style.display = 'none';
}

function renderLoggedInView() {
    document.getElementById('auth-section').style.display = 'none';
    document.getElementById('user-app-section').style.display = 'block';
    document.getElementById('nav-user-info').style.display = 'flex';

    document.getElementById('nav-username').textContent = currentUser.username;
    const rolePill = document.getElementById('nav-role');
    const isAdmin = currentUser.roles.includes('ROLE_ADMIN');
    rolePill.textContent = isAdmin ? 'ADMIN' : 'USER';
    rolePill.className = `role-pill ${isAdmin ? 'role-admin' : 'role-user'}`;

    if (isAdmin) {
        document.getElementById('admin-nav-btn').style.display = 'inline-block';
    } else {
        document.getElementById('admin-nav-btn').style.display = 'none';
    }

    // Render active permissions strip
    renderUserPermissionsStrip();
}

function renderUserPermissionsStrip() {
    const strip = document.getElementById('active-perms-strip');
    strip.innerHTML = '';

    if (currentUser.roles.includes('ROLE_ADMIN')) {
        strip.innerHTML = `<span class="perm-badge">★ ALL_ACCESS_ADMIN (Universal)</span>`;
        return;
    }

    if (!currentUser.activePermissions || currentUser.activePermissions.length === 0) {
        strip.innerHTML = `<span style="font-size: 0.85rem; color: var(--text-muted);">No active stream permissions. Unlock or request access below!</span>`;
        return;
    }

    currentUser.activePermissions.forEach(p => {
        const badge = document.createElement('span');
        badge.className = `perm-badge ${p.temporary ? 'temporary' : ''}`;
        let expText = '';
        if (p.expiresAt) {
            const exp = new Date(p.expiresAt);
            expText = ` (Expires: ${exp.toLocaleDateString()} ${exp.toLocaleTimeString()})`;
        }
        badge.textContent = `✔ ${p.name} [${p.grantType || 'GRANT'}]${expText}`;
        strip.appendChild(badge);
    });
}

// Load Permissions
async function loadPermissions() {
    try {
        const res = await apiFetch('/permissions');
        if (res.success) {
            allPermissions = res.data;
            populatePermissionDropdowns();
        }
    } catch (err) {
        console.error(err);
    }
}

function populatePermissionDropdowns() {
    const reqSelect = document.getElementById('req-permission-select');
    const buySelect = document.getElementById('buy-permission-select');
    const adminPermSelect = document.getElementById('admin-grant-perm-select');

    if (reqSelect) {
        reqSelect.innerHTML = allPermissions.map(p => `<option value="${p.id}">${p.name} - ${p.description}</option>`).join('');
    }
    if (buySelect) {
        buySelect.innerHTML = allPermissions.map(p => `<option value="${p.id}">${p.name} ($${p.price.toFixed(2)})</option>`).join('');
    }
    if (adminPermSelect) {
        adminPermSelect.innerHTML = allPermissions.map(p => `<option value="${p.id}">${p.name}</option>`).join('');
    }
}

// Media Catalog
async function loadMediaCatalog() {
    try {
        const res = await apiFetch('/media');
        if (res.success) {
            allMedia = res.data;
            renderMediaGrid();
        }
    } catch (err) {
        console.error(err);
    }
}

function filterCategory(cat) {
    activeCategory = cat;
    document.querySelectorAll('.filter-chip').forEach(c => {
        c.classList.toggle('active', c.dataset.category === cat);
    });
    renderMediaGrid();
}

function renderMediaGrid() {
    const grid = document.getElementById('media-grid');
    grid.innerHTML = '';

    const filtered = (activeCategory === 'ALL')
        ? allMedia
        : allMedia.filter(m => m.category.toUpperCase() === activeCategory);

    if (filtered.length === 0) {
        grid.innerHTML = `<p style="grid-column: 1/-1; color: var(--text-muted); text-align: center; padding: 3rem;">No media content found in this category.</p>`;
        return;
    }

    filtered.forEach(m => {
        const card = document.createElement('div');
        card.className = 'media-card';

        const accessBadge = m.accessible
            ? `<span style="color: var(--success); font-weight: 600; font-size: 0.8rem;">🟢 Unlocked</span>`
            : `<span style="color: var(--danger); font-weight: 600; font-size: 0.8rem;">🔒 Locked (${m.requiredPermission})</span>`;

        let actionButtons = '';
        if (m.accessible) {
            actionButtons = `<button class="btn btn-primary btn-block btn-sm" onclick="streamMediaItem(${m.id})">▶ Stream Now</button>`;
        } else {
            actionButtons = `
                <button class="btn btn-success btn-sm" onclick="openPurchaseModal('${m.requiredPermission}')">💳 Buy Access</button>
                <button class="btn btn-secondary btn-sm" onclick="openRequestModal('${m.requiredPermission}')">⏱ Request Access</button>
            `;
        }

        card.innerHTML = `
            <img class="media-thumb" src="${m.thumbnailUrl || 'https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500'}" alt="${m.title}" onerror="this.src='https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=500'">
            <div class="media-body">
                <div class="media-header">
                    <span class="media-category-pill">${m.category}</span>
                    ${accessBadge}
                </div>
                <h3 class="media-title">${m.title}</h3>
                <p class="media-desc">${m.description || ''}</p>
                <div class="media-meta">
                    <span>${m.releaseYear || 2024}</span>
                    <span>${m.durationMinutes ? m.durationMinutes + ' min' : 'Feature'}</span>
                </div>
                <div class="card-actions">${actionButtons}</div>
            </div>
        `;
        grid.appendChild(card);
    });
}

// Stream Media (Plays HTML5 Video in modal)
async function streamMediaItem(mediaId) {
    try {
        const res = await apiFetch(`/media/${mediaId}/stream`);
        if (res.success) {
            const media = res.data;
            document.getElementById('stream-title').textContent = media.title;
            const videoElem = document.getElementById('stream-video-player');
            videoElem.src = media.videoUrl;
            videoElem.play();
            openModal('stream-modal');
        }
    } catch (err) {
        console.error(err);
    }
}

function closeStreamModal() {
    const videoElem = document.getElementById('stream-video-player');
    videoElem.pause();
    videoElem.src = '';
    closeModal('stream-modal');
}

// Modal Helpers
function openModal(id) {
    document.getElementById(id).classList.add('active');
}

function closeModal(id) {
    document.getElementById(id).classList.remove('active');
}

// Purchase Modal
function openPurchaseModal(requiredPermissionName) {
    if (requiredPermissionName) {
        const perm = allPermissions.find(p => p.name === requiredPermissionName);
        if (perm) {
            document.getElementById('buy-permission-select').value = perm.id;
        }
    }
    openModal('purchase-modal');
}

async function handlePurchaseSubmit(e) {
    e.preventDefault();
    const permissionId = document.getElementById('buy-permission-select').value;
    const paymentMethod = document.getElementById('payment-method-select').value;

    try {
        const res = await apiFetch('/permissions/purchase', {
            method: 'POST',
            body: JSON.stringify({ permissionId: Number(permissionId), paymentMethod })
        });

        if (res.success) {
            showToast('Payment Successful! Permission granted for 30 days! Tx: ' + res.data.transactionRef, 'success');
            closeModal('purchase-modal');
            await fetchCurrentUser();
        }
    } catch (err) {
        console.error(err);
    }
}

// Request Access Modal
function openRequestModal(requiredPermissionName) {
    if (requiredPermissionName) {
        const perm = allPermissions.find(p => p.name === requiredPermissionName);
        if (perm) {
            document.getElementById('req-permission-select').value = perm.id;
        }
    }
    openModal('request-modal');
}

async function handleRequestSubmit(e) {
    e.preventDefault();
    const permissionId = document.getElementById('req-permission-select').value;
    const requestedHours = document.getElementById('req-hours-select').value;
    const reason = document.getElementById('req-reason').value;

    try {
        const res = await apiFetch('/permissions/request-access', {
            method: 'POST',
            body: JSON.stringify({
                permissionId: Number(permissionId),
                requestedHours: Number(requestedHours),
                reason
            })
        });

        if (res.success) {
            showToast('Request submitted to Admin for ' + requestedHours + ' hours access!', 'success');
            closeModal('request-modal');
            document.getElementById('req-reason').value = '';
            loadMyRequests();
        }
    } catch (err) {
        console.error(err);
    }
}

// My Requests View
async function loadMyRequests() {
    try {
        const res = await apiFetch('/permissions/my-requests');
        if (res.success) {
            const tbody = document.getElementById('my-requests-tbody');
            tbody.innerHTML = '';
            if (res.data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted);">No access requests submitted yet.</td></tr>`;
                return;
            }
            res.data.forEach(r => {
                const tr = document.createElement('tr');
                let statusColor = 'var(--warning)';
                if (r.status === 'APPROVED') statusColor = 'var(--success)';
                if (r.status === 'REJECTED') statusColor = 'var(--danger)';

                tr.innerHTML = `
                    <td>#${r.id}</td>
                    <td><strong>${r.permissionName}</strong></td>
                    <td>${r.requestedHours} Hours</td>
                    <td>${r.reason || '-'}</td>
                    <td><span style="color: ${statusColor}; font-weight: 700;">${r.status}</span></td>
                    <td>${r.adminNotes || '-'}</td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (err) {
        console.error(err);
    }
}

// Admin Console Functions
async function openAdminConsole() {
    switchView('admin-view');
    await loadAdminStats();
    await loadAdminUsers();
    await loadAdminRequests();
}

async function loadAdminStats() {
    try {
        const res = await apiFetch('/admin/stats');
        if (res.success) {
            const s = res.data;
            document.getElementById('stat-total-users').textContent = s.totalUsers;
            document.getElementById('stat-pending-requests').textContent = s.pendingRequests;
            document.getElementById('stat-total-media').textContent = s.totalMedia;
            document.getElementById('stat-total-revenue').textContent = '$' + s.totalRevenue.toFixed(2);
        }
    } catch (err) {
        console.error(err);
    }
}

function escapeHtml(str) {
    if (!str) return '';
    return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
}

async function loadAdminUsers() {
    try {
        const res = await apiFetch('/admin/users');
        if (res.success) {
            const tbody = document.getElementById('admin-users-tbody');
            tbody.innerHTML = '';
            res.data.forEach(u => {
                const tr = document.createElement('tr');
                const permNames = u.activePermissions.map(p => `${p.name} (${p.isTemporary ? 'Temp' : 'Perm'})`).join(', ') || 'None';
                const roleBadges = u.roles.map(r => {
                    if (r === 'ROLE_ADMIN') return `<span class="role-pill role-admin">ADMIN</span>`;
                    if (r === 'ROLE_USER') return `<span class="role-pill role-user">USER</span>`;
                    return `<span class="role-pill">${escapeHtml(r)}</span>`;
                }).join(' ');

                tr.innerHTML = `
                    <td>#${u.id}</td>
                    <td><strong>${escapeHtml(u.username)}</strong><br><small style="color: var(--text-muted);">${escapeHtml(u.email)}</small></td>
                    <td><div style="display: flex; gap: 0.35rem; flex-wrap: wrap;">${roleBadges}</div></td>
                    <td><small>${escapeHtml(permNames)}</small></td>
                    <td>
                        <div style="display: flex; gap: 0.4rem; flex-wrap: wrap;">
                            <button class="btn btn-primary btn-sm" onclick="openAdminRoleModal(${u.id}, '${escapeHtml(u.username)}', '${u.roles.join(',')}')">🛡️ Assign Roles</button>
                            <button class="btn btn-secondary btn-sm" onclick="openAdminGrantModal(${u.id}, '${escapeHtml(u.username)}')">+ Grant</button>
                        </div>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (err) {
        console.error(err);
    }
}

let targetUserIdForRoles = null;
function openAdminRoleModal(userId, username, rolesStr) {
    targetUserIdForRoles = userId;
    document.getElementById('role-modal-user').textContent = `User: ${username} (ID: #${userId})`;
    const roles = rolesStr ? rolesStr.split(',') : [];
    document.getElementById('role-cb-user').checked = roles.includes('ROLE_USER');
    document.getElementById('role-cb-admin').checked = roles.includes('ROLE_ADMIN');
    openModal('admin-role-modal');
}

function setRolePresets(preset) {
    if (preset === 'USER_ONLY') {
        document.getElementById('role-cb-user').checked = true;
        document.getElementById('role-cb-admin').checked = false;
    } else if (preset === 'PROMOTE_ADMIN') {
        document.getElementById('role-cb-user').checked = true;
        document.getElementById('role-cb-admin').checked = true;
    }
}

async function handleAdminRoleSubmit(e) {
    e.preventDefault();
    if (!targetUserIdForRoles) return;

    const roles = [];
    if (document.getElementById('role-cb-user').checked) roles.push('ROLE_USER');
    if (document.getElementById('role-cb-admin').checked) roles.push('ROLE_ADMIN');

    if (roles.length === 0) {
        showToast('Please select at least one role (e.g. ROLE_USER)', 'error');
        return;
    }

    try {
        const res = await apiFetch(`/admin/users/${targetUserIdForRoles}/roles`, {
            method: 'PUT',
            body: JSON.stringify({ roles })
        });
        if (res.success) {
            showToast(`Roles updated for #${targetUserIdForRoles}: ${roles.join(', ')}`, 'success');
            closeModal('admin-role-modal');
            await loadAdminUsers();
            await loadAdminStats();
            if (currentUser && currentUser.id === targetUserIdForRoles) {
                await fetchCurrentUser();
            }
        }
    } catch (err) {
        console.error(err);
    }
}

let targetUserIdForGrant = null;
function openAdminGrantModal(userId, username) {
    targetUserIdForGrant = userId;
    document.getElementById('grant-modal-user').textContent = `User: ${username} (ID: #${userId})`;
    openModal('admin-grant-modal');
}

async function handleAdminGrantSubmit(e) {
    e.preventDefault();
    if (!targetUserIdForGrant) return;
    const permId = document.getElementById('admin-grant-perm-select').value;
    const hours = document.getElementById('admin-grant-hours').value;

    try {
        const res = await apiFetch(`/admin/users/${targetUserIdForGrant}/permissions/${permId}/grant?hours=${hours}`, {
            method: 'POST'
        });
        if (res.success) {
            showToast('Permission granted to user!', 'success');
            closeModal('admin-grant-modal');
            loadAdminUsers();
            loadAdminStats();
        }
    } catch (err) {
        console.error(err);
    }
}

async function loadAdminRequests() {
    try {
        const res = await apiFetch('/admin/requests');
        if (res.success) {
            const tbody = document.getElementById('admin-requests-tbody');
            tbody.innerHTML = '';
            if (res.data.length === 0) {
                tbody.innerHTML = `<tr><td colspan="7" style="text-align: center; color: var(--text-muted);">No requests pending.</td></tr>`;
                return;
            }
            res.data.forEach(r => {
                const tr = document.createElement('tr');
                let actions = '-';
                if (r.status === 'PENDING') {
                    actions = `
                        <button class="btn btn-success btn-sm" onclick="approveRequest(${r.id})">✔ Approve</button>
                        <button class="btn btn-danger btn-sm" onclick="rejectRequest(${r.id})">✖ Reject</button>
                    `;
                }
                tr.innerHTML = `
                    <td>#${r.id}</td>
                    <td><strong>${r.username}</strong></td>
                    <td>${r.permissionName}</td>
                    <td>${r.requestedHours} hrs</td>
                    <td>${r.reason || '-'}</td>
                    <td><strong>${r.status}</strong></td>
                    <td>${actions}</td>
                `;
                tbody.appendChild(tr);
            });
        }
    } catch (err) {
        console.error(err);
    }
}

async function approveRequest(requestId) {
    const notes = prompt("Enter optional admin notes for approval:", "Approved by Admin");
    try {
        const res = await apiFetch(`/admin/requests/${requestId}/approve?adminNotes=${encodeURIComponent(notes || 'Approved')}`, {
            method: 'POST'
        });
        if (res.success) {
            showToast('Request Approved and Temporary Access Granted!', 'success');
            loadAdminRequests();
            loadAdminStats();
        }
    } catch (err) {
        console.error(err);
    }
}

async function rejectRequest(requestId) {
    const notes = prompt("Enter reason for rejection:", "Access rejected at this time");
    try {
        const res = await apiFetch(`/admin/requests/${requestId}/reject?adminNotes=${encodeURIComponent(notes || 'Rejected')}`, {
            method: 'POST'
        });
        if (res.success) {
            showToast('Request Rejected', 'info');
            loadAdminRequests();
            loadAdminStats();
        }
    } catch (err) {
        console.error(err);
    }
}

// Add Media Content (Admin)
async function handleAddMediaSubmit(e) {
    e.preventDefault();
    const title = document.getElementById('media-title-input').value;
    const category = document.getElementById('media-category-input').value;
    const requiredPermission = document.getElementById('media-perm-input').value;
    const videoUrl = document.getElementById('media-url-input').value;
    const thumbnailUrl = document.getElementById('media-thumb-input').value;
    const durationMinutes = document.getElementById('media-duration-input').value;
    const description = document.getElementById('media-desc-input').value;

    try {
        const res = await apiFetch('/media', {
            method: 'POST',
            body: JSON.stringify({
                title,
                category,
                requiredPermission,
                videoUrl,
                thumbnailUrl,
                durationMinutes: Number(durationMinutes) || 120,
                description
            })
        });

        if (res.success) {
            showToast('New media added successfully!', 'success');
            document.getElementById('add-media-form').reset();
            loadMediaCatalog();
            loadAdminStats();
        }
    } catch (err) {
        console.error(err);
    }
}

// Navigation View Switcher
function switchView(viewName) {
    document.querySelectorAll('.app-view').forEach(v => v.style.display = 'none');
    document.querySelectorAll('.nav-btn').forEach(b => b.classList.remove('active'));

    if (viewName === 'catalog-view') {
        document.getElementById('catalog-view').style.display = 'block';
        document.getElementById('catalog-nav-btn').classList.add('active');
    } else if (viewName === 'requests-view') {
        document.getElementById('requests-view').style.display = 'block';
        document.getElementById('requests-nav-btn').classList.add('active');
        loadMyRequests();
    } else if (viewName === 'admin-view') {
        document.getElementById('admin-view').style.display = 'block';
        document.getElementById('admin-nav-btn').classList.add('active');
    }
}
