document.addEventListener('DOMContentLoaded', async () => {
    if (!(await ensureAuth())) return;

    const role = localStorage.getItem('userRole');
    const employeeId = localStorage.getItem('employeeId');
    const form = document.getElementById('employeeIdForm');
    const profileDiv = document.getElementById('employeeProfile');
    const errorMsgEl = document.getElementById('searchErrorMsg');
    const infoMsgEl = document.getElementById('searchInfoMsg');
    const searchSection = document.getElementById('searchSection');

    function showSearchError(msg) {
        if (errorMsgEl) { errorMsgEl.textContent = msg; errorMsgEl.classList.remove('hidden'); }
        if (infoMsgEl) { infoMsgEl.textContent = ''; infoMsgEl.classList.add('hidden'); }
    }


    function showSearchInfo(msg) {
        if (infoMsgEl) { infoMsgEl.textContent = msg; infoMsgEl.classList.remove('hidden'); }
        if (errorMsgEl) { errorMsgEl.textContent = ''; errorMsgEl.classList.add('hidden'); }
    }

    function clearSearchMessages() {
        if (errorMsgEl) { errorMsgEl.textContent = ''; errorMsgEl.classList.add('hidden'); }
        if (infoMsgEl) { infoMsgEl.textContent = ''; infoMsgEl.classList.add('hidden'); }
    }


    async function loadEmployee(empId) {
        clearSearchMessages();
        profileDiv.classList.add('hidden');

        try {

            const response = await ApiService.get(`/employee/employeeId?employeeId=${encodeURIComponent(empId)}`);

            const data = response.data || response;

            if (data && (data.id || data.employeeId || data.firstName)) {
                profileDiv.classList.remove('hidden');
                document.getElementById('p-id').textContent = data.id || empId;
                document.getElementById('p-name').textContent = `${data.firstName || ''} ${data.lastName || ''}`.trim() || '—';
                document.getElementById('p-phone').textContent = data.phoneNumber || '—';
                document.getElementById('p-address').textContent = data.address || '—';
                document.getElementById('p-salary').textContent = data.salary != null
                    ? `₹${Number(data.salary).toLocaleString('en-IN')}`
                    : '—';
                document.getElementById('p-dept').textContent = data.departmentName
                    || (data.department && data.department.departmentName)
                    || '—';
                document.getElementById('p-status').textContent = data.active || '—';
            } else {
                showSearchError('Employee not found. Please check the ID and try again.');
            }
        } catch (err) {

            if (role === 'ROLE_EMPLOYEE') {
                showSearchError('Employee are not added, Please contact with ADMIN');
            } else {
                const msg = err.message || '';
                if (msg.includes('404') || msg.toLowerCase().includes('not found')) {
                    showSearchError('Employee not found. Please check the ID and try again.');
                } else if (msg.includes('403') || msg.toLowerCase().includes('denied') || msg.toLowerCase().includes('authorized')) {
                    showSearchError('Employee Not Found.');
                } else {
                    showSearchError('Something went wrong. Please try again later.');
                }
            }
        }
    }



    if (role === 'ROLE_EMPLOYEE') {
        // Hide Dashboard button for employees
        const dashboardBtn = document.querySelector('.navbar .btn-outline[href="dashboard.html"]');
        if (dashboardBtn) {
            dashboardBtn.style.display = 'none';
        }
        
        // Update navbar brand link for employees
        const navBrand = document.querySelector('.navbar .navbar-brand');
        if (navBrand) {
            navBrand.href = 'employee.html';
        }

        if (employeeId) {

            if (searchSection) {
                searchSection.classList.add('hidden');
            }
            // Update heading
            const heading = document.getElementById('pageHeading');
            if (heading) heading.textContent = 'My Profile';

            loadEmployee(employeeId);
        } else {
            // Employee not linked yet — show a message
            showSearchError('Employee are not added, Please contact with ADMIN');
            if (searchSection) {
                searchSection.classList.add('hidden');
            }
        }
    } else {
        // ADMIN: show the search form, allow searching any employee
        if (form) {
            form.addEventListener('submit', async (e) => {
                e.preventDefault();
                const empId = document.getElementById('empIdInput').value.trim();
                if (!empId || isNaN(empId)) {
                    showSearchError('Please enter a valid numeric Employee ID.');
                    return;
                }
                await loadEmployee(empId);
            });
        }
    }
});
