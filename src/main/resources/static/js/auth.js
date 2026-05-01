document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const errorMsg = document.getElementById('errorMsg');
    const successMsg = document.getElementById('successMsg');
    const logoutBtn = document.getElementById('logoutBtn');


    const oauthErrorMsg = document.getElementById('oauthErrorMsg');
    const params = new URLSearchParams(window.location.search);
    if (params.get("error") === "true" && oauthErrorMsg) {
        oauthErrorMsg.textContent = "Your Google account is not registered. Please register first, then use Google login.";
        oauthErrorMsg.classList.remove('hidden');
    }

    // Login
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            clearMessages();

            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value.trim();

            if (!email || !password) {
                showError('Please enter both email and password.');
                return;
            }

            setButtonLoading('loginBtn', 'loginBtnText', 'loginSpinner', true);

            try {
                const response = await ApiService.post('/user/login', { email, password });

                if (response && response.status === true) {
                    const role = response.data.role;  // "ROLE_ADMIN" or "ROLE_EMPLOYEE"
                    //Store only non-sensitive data for UI routing
                    localStorage.setItem('userEmail', email);
                    localStorage.setItem('userRole', role);
                    localStorage.setItem('userId', response.data.id);
                    if (response.data.employeeId) {
                        localStorage.setItem('employeeId', response.data.employeeId);
                    } else {
                        localStorage.removeItem('employeeId');
                    }


                    //Role-based direct redirect
                    if (role === 'ROLE_ADMIN') {
                        window.location.href = 'admin.html';
                    } else {
                        window.location.href = 'employee.html';
                    }
                } else {
                    throw new Error(response.message || 'Login failed');
                }
            } catch (err) {
                showError('Invalid credentials. Please register first.');
                setButtonLoading('loginBtn', 'loginBtnText', 'loginSpinner', false);
            }
        });
    }

    // Register
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            clearMessages();

            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value.trim();

            if (!email || !password) {
                showError('Please fill in all fields.');
                return;
            }
            if (password.length < 6) {
                showError('Password must be at least 6 characters.');
                return;
            }

            setButtonLoading('registerBtn', 'registerBtnText', 'registerSpinner', true);

            try {
                // Role is always EMPLOYEE — admin is seeded via DataInitializer on server startup
                const response = await ApiService.post('/user/register', {
                    email,
                    password,
                    role: 'EMPLOYEE',
                    enabled: true
                });

                if (response && response.status === true) {
                    showSuccess(`Registered Successfully!`);
                    registerForm.reset();
                } else {
                    throw new Error(response.message || 'Registration failed');
                }
            } catch (err) {
                showError(err.message || 'Registration failed. Email may already be in use.');
            } finally {
                setButtonLoading('registerBtn', 'registerBtnText', 'registerSpinner', false);
            }
        });
    }

    // Logout
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            //Call the backend logout endpoint to invalidate the server session
            try {
                await fetch('/logout', { method: 'POST', credentials: 'same-origin' });
            } catch (e) {

            }
            localStorage.clear();
            window.location.href = 'login.html';
        });
    }


    function clearMessages() {
        if (errorMsg) { errorMsg.textContent = ''; errorMsg.classList.add('hidden'); }
        if (successMsg) { successMsg.textContent = ''; successMsg.classList.add('hidden'); }
    }


    function showError(msg) {
        if (errorMsg) { errorMsg.textContent = msg; errorMsg.classList.remove('hidden'); }
    }

    function showSuccess(msg) {
        if (successMsg) { successMsg.textContent = msg; successMsg.classList.remove('hidden'); }
        if (errorMsg) { errorMsg.classList.add('hidden'); }
    }
});

function setButtonLoading(btnId, textSpanId, spinnerId, loading) {
    const btn = document.getElementById(btnId);
    const txtSpan = document.getElementById(textSpanId);
    const spinner = document.getElementById(spinnerId);
    if (!btn) return;

    if (loading) {
        btn.disabled = true;
        if (txtSpan) txtSpan.classList.add('hidden');
        if (spinner) spinner.classList.remove('hidden');
    } else {
        btn.disabled = false;
        if (txtSpan) txtSpan.classList.remove('hidden');
        if (spinner) spinner.classList.add('hidden');
    }
}

function checkAuth(requiredRole = null) {
    const email = localStorage.getItem('userEmail');
    const role = localStorage.getItem('userRole');

    if (!email) {
        // Don't redirect immediately — might be an OAuth2 session.
        // Pages should use ensureAuth() instead for full support.
        window.location.href = 'login.html';
        return false;
    }

    if (requiredRole && role !== requiredRole) {
        // Silent redirect — no alert popup
        window.location.href = role === 'ROLE_EMPLOYEE' ? 'employee.html' : 'dashboard.html';
        return false;
    }


    return true;
}

async function ensureAuth(requiredRole = null) {
    let email = localStorage.getItem('userEmail');
    let role = localStorage.getItem('userRole');

    if (!email) {
        try {
            const res = await fetch('/user/me', { credentials: 'same-origin' });
            if (res.ok) {
                const body = await res.json();
                if (body && body.status && body.data) {
                    localStorage.setItem('userEmail', body.data.email);
                    localStorage.setItem('userRole', body.data.role);
                    localStorage.setItem('userId', body.data.id);
                    if (body.data.employeeId) {
                        localStorage.setItem('employeeId', body.data.employeeId);
                    } else {
                        localStorage.removeItem('employeeId');
                    }
                    email = body.data.email;
                    role = body.data.role;
                }
            }
        } catch (e) {

        }
    }

    if (!email) {
        window.location.href = 'login.html';
        return false;
    }

    if (requiredRole && role !== requiredRole) {
        window.location.href = role === 'ROLE_EMPLOYEE' ? 'employee.html' : 'dashboard.html';
        return false;
    }


    return true;
}

