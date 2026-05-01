let currentPage      = 0;
const PAGE_SIZE      = 7;
let totalPages       = 1;
let currentSortField = 'id';
let currentSortDir   = 'ASC';

//DOMContentLoaded browser event (fire When HTML fully loaded BUT before images, CSS, videos are fully loaded)
document.addEventListener('DOMContentLoaded', async () => {
    if (!(await ensureAuth('ROLE_ADMIN'))) return;

    loadEmployees(currentPage);

    document.getElementById('prevBtn').addEventListener('click', () => {
        if (currentPage > 0) { currentPage--; loadEmployees(currentPage); }
    });

    document.getElementById('nextBtn').addEventListener('click', () => {
        if (currentPage < totalPages - 1) { currentPage++; loadEmployees(currentPage); }
    });

    document.getElementById('searchForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const type  = document.getElementById('searchType').value;
        const query = document.getElementById('searchInput').value.trim();
        if (!query) { loadEmployees(0); return; }
        await performSearch(type, query);
    });

    document.getElementById('resetSearchBtn').addEventListener('click', () => {
        document.getElementById('searchInput').value = '';
        currentPage = 0;
        loadEmployees(currentPage);
    });

    document.getElementById('sortSelect').addEventListener('change', () => {
        currentSortField = document.getElementById('sortSelect').value;
        currentSortDir   = 'ASC';
        currentPage      = 0;
        loadEmployees(currentPage);
    });

    document.getElementById('toggleAddForm').addEventListener('click', () => {
        const panel    = document.getElementById('addEmployeePanel');
        const icon     = document.querySelector('.toggle-icon');
        const isHidden = panel.classList.contains('hidden');
        panel.classList.toggle('hidden', !isHidden);
        icon.textContent = isHidden ? '▲' : '▼';
    });

    document.getElementById('addEmployeeForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const msgEl = document.getElementById('addFormMsg');
        msgEl.className = 'hidden';

        const payload = {
            userId:      parseInt(document.getElementById('add-userId').value),
            firstName:   document.getElementById('add-firstName').value.trim(),
            lastName:    document.getElementById('add-lastName').value.trim(),
            phoneNumber: document.getElementById('add-phone').value.trim(),
            address:     document.getElementById('add-address').value.trim(),
            salary:      parseFloat(document.getElementById('add-salary').value),
            departmentId: parseInt(document.getElementById('add-deptId').value),
            active:      document.getElementById('add-status').value,
        };

        try {
            await ApiService.post('/employee/addEmployee', payload);
            showMsg(msgEl, '✅ Employee added successfully!', 'success-msg');
            document.getElementById('addEmployeeForm').reset();
            currentPage = 0;
            loadEmployees(currentPage);
        } catch (err) {
            showMsg(msgEl, '❌ ' + err.message, 'error-msg');
        }
    });

    document.getElementById('closeModal').addEventListener('click',  closeUpdateModal);
    document.getElementById('cancelModal').addEventListener('click', closeUpdateModal);
    document.getElementById('updateModal').addEventListener('click', (e) => {
        if (e.target === document.getElementById('updateModal')) closeUpdateModal();
    });

    document.getElementById('updateEmployeeForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const msgEl = document.getElementById('updFormMsg');
        msgEl.className = 'hidden';

        const empId = document.getElementById('upd-empId').value;
        const firstName = document.getElementById('upd-firstName').value.trim();
        const lastName = document.getElementById('upd-lastName').value.trim();
        const phoneNumber = document.getElementById('upd-phone').value.trim();
        const address = document.getElementById('upd-address').value.trim();


        if (!firstName || !lastName || !phoneNumber || !address) {
            showMsg(msgEl, '❌ All fields are required.', 'error-msg');
            return;
        }

        const payload = {
            employeeId:  parseInt(empId),
            firstName,
            lastName,
            phoneNumber,
            address,
        };

        try {
            await ApiService.put('/employee/update', payload);
            showMsg(msgEl, '✅ Employee updated!', 'success-msg');
            setTimeout(async () => {
                closeUpdateModal();
                await loadEmployees(currentPage);
            }, 1200);
        } catch (err) {
            const msg = err.message || 'Update failed. Please try again.';
            showMsg(msgEl, '❌ ' + msg, 'error-msg');
        }
    });

    document.getElementById('toggleSalaryForm').addEventListener('click', () => {
        const panel    = document.getElementById('salaryCalculationPanel');
        const icon     = document.querySelector('#toggleSalaryForm .toggle-icon');
        const isHidden = panel.classList.contains('hidden');
        panel.classList.toggle('hidden', !isHidden);
        icon.textContent = isHidden ? '▲' : '▼';
    });

    document.getElementById('salaryCalcForm').addEventListener('submit', async (e) => {
        e.preventDefault();
        const msgEl = document.getElementById('salaryCalcMsg');
        const resultPanel = document.getElementById('salaryResultPanel');
        msgEl.className = 'hidden';
        resultPanel.classList.add('hidden');

        const payload = {
            employeeId: parseInt(document.getElementById('calc-employeeId').value),
            percentage: parseFloat(document.getElementById('calc-percentage').value)
        };

        try {
            const data = await ApiService.post('/employee/dynamic-salary', payload);

            document.getElementById('res-name').textContent = `${data.firstName || ''} ${data.lastName || ''}`;
            document.getElementById('res-dept').textContent = data.departmentName;
            document.getElementById('res-base').textContent = `₹${Number(data.baseSalary).toLocaleString('en-IN')}`;
            document.getElementById('res-calc').textContent = `₹${Number(data.calculatedSalary).toLocaleString('en-IN')}`;
            
            resultPanel.classList.remove('hidden');
            showMsg(msgEl, '✅ Salary calculated successfully!', 'success-msg');
        } catch (err) {
            const msg = err.message || 'Calculation failed.';
            showMsg(msgEl, '❌ ' + msg, 'error-msg');
        }
    });
});


async function loadEmployees(page) {
    setTableLoading();
    try {

        const response = await ApiService.get(
            `/employee/page?page=${page}&size=${PAGE_SIZE}&sort=${encodeURIComponent(currentSortField)}&sort=${encodeURIComponent(currentSortDir)}`
        );
        const pageData  = response;
        const employees = pageData.content      || [];
        totalPages      = pageData.totalPages   || 1;

        renderTable(employees);
        updatePaginationUI(page, totalPages);
    } catch (err) {
        console.error('loadEmployees error:', err);
        document.getElementById('employeeTableBody').innerHTML =
            `<tr><td colspan="7" class="text-center error-msg">Failed to load: ${err.message}</td></tr>`;
    }
}

async function performSearch(type, query) {
    setTableLoading();
    try {
        let endpoint = '';
        if (type === 'id') {
            endpoint = `/employee/employeeId?employeeId=${encodeURIComponent(query)}`;
        } else if (type === 'deptId') {
            endpoint = `/employee/filterByDepartmentId?id=${encodeURIComponent(query)}`;
        } else if (type === 'deptName') {
            endpoint = `/employee/filter?department_name=${encodeURIComponent(query)}`;
        }

        const response = await ApiService.get(endpoint);

        let employees = Array.isArray(response)
            ? response
            : (response ? [response] : []);

        if (employees.length === 0) {
            document.getElementById('employeeTableBody').innerHTML =
                '<tr><td colspan="7" class="text-center">No employees found.</td></tr>';
        } else {
            renderTable(employees);
        }

        document.getElementById('prevBtn').disabled = true;
        document.getElementById('nextBtn').disabled = true;
    } catch (err) {
        document.getElementById('employeeTableBody').innerHTML =
            `<tr><td colspan="7" class="text-center error-msg">Search failed: ${err.message}</td></tr>`;
    }
}

function renderTable(employees) {
    const tbody = document.getElementById('employeeTableBody');
    tbody.innerHTML = '';

    if (!employees || employees.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">No records found.</td></tr>';
        return;
    }

    employees.forEach(emp => {
        const empId   = emp.id || emp.employeeId || '';
        const name    = `${emp.firstName || ''} ${emp.lastName || ''}`.trim() || '—';
        const phone   = emp.phoneNumber || '—';
        const dept    = emp.departmentName
                        || (emp.department && emp.department.departmentName)
                        || '—';
        const salary  = emp.salary != null ? `₹${Number(emp.salary).toLocaleString('en-IN')}` : '—';
        const status  = emp.active || '—';
        const statusBadge = `<span class="badge badge-${status === 'ACTIVE' ? 'success' : 'inactive'}">${status}</span>`;

        const tr = document.createElement('tr');
        tr.setAttribute('data-emp-id', empId);
        tr.innerHTML = `
            <td>${empId}</td>
            <td>${name}</td>
            <td>${phone}</td>
            <td>${dept}</td>
            <td>${salary}</td>
            <td>${statusBadge}</td>
            <td class="action-btns">
                <button class="btn btn-outline" onclick="openUpdateModal(${empId}, '${emp.firstName || ''}', '${emp.lastName || ''}', '${phone}', '${emp.address || ''}')">Edit</button>
                <button class="btn btn-danger"  onclick="deleteEmployee(${empId})">Delete</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

async function deleteEmployee(id) {
    if (!id) return;
    // Custom inline confirmation using a toast-style overlay
    const confirmed = await showConfirmToast(`Delete employee ID ${id}? This cannot be undone.`);
    if (!confirmed) return;

    try {
        await ApiService.delete(`/employee/${id}`);
        showToast('Employee deleted successfully.', 'success');

        const tbody = document.getElementById('employeeTableBody');
        const visibleRows = tbody.querySelectorAll('tr[data-emp-id]').length;
        if (visibleRows <= 1 && currentPage > 0) {
            currentPage--;
        }

        //Always await re-fetch to guarantee UI update
        await loadEmployees(currentPage);
    } catch (err) {
        showToast('Delete failed: ' + err.message, 'error');
        //Still try to refresh table even on error (data may have been deleted)
        try { await loadEmployees(currentPage); } catch (_) { /* ignore */ }
    }
}


function openUpdateModal(empId, firstName, lastName, phone, address) {
    document.getElementById('upd-empId').value     = empId;
    document.getElementById('upd-firstName').value = firstName;
    document.getElementById('upd-lastName').value  = lastName;
    document.getElementById('upd-phone').value     = phone;
    document.getElementById('upd-address').value   = address;
    document.getElementById('updFormMsg').className = 'hidden';
    document.getElementById('updateModal').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}


function closeUpdateModal() {
    document.getElementById('updateModal').classList.add('hidden');
    document.body.style.overflow = '';
}

function setTableLoading() {
    document.getElementById('employeeTableBody').innerHTML =
        '<tr><td colspan="7" class="text-center">Loading...</td></tr>';
}

function updatePaginationUI(page, total) {
    document.getElementById('pageNumber').textContent = `Page ${page + 1} of ${total}`;
    document.getElementById('prevBtn').disabled = (page === 0);
    document.getElementById('nextBtn').disabled = (page >= total - 1);
}


function showMsg(el, text, cssClass) {
    el.textContent  = text;
    el.className    = cssClass;
}


function showToast(message, type = 'success') {
    // Remove existing toast
    const existing = document.getElementById('ems-toast');
    if (existing) existing.remove();

    const toast = document.createElement('div');
    toast.id = 'ems-toast';
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        bottom: 1.5rem;
        right: 1.5rem;
        padding: 0.85rem 1.4rem;
        border-radius: 8px;
        font-size: 0.9rem;
        font-weight: 500;
        color: #fff;
        z-index: 9999;
        box-shadow: 0 4px 12px rgba(0,0,0,0.2);
        animation: slideInToast 0.3s ease;
        background: ${type === 'success' ? '#10B981' : '#EF4444'};
        max-width: 320px;
    `;

    if (!document.getElementById('toast-style')) {
        const style = document.createElement('style');
        style.id = 'toast-style';
        style.textContent = `
            @keyframes slideInToast {
                from { opacity: 0; transform: translateY(20px); }
                to   { opacity: 1; transform: translateY(0); }
            }
        `;
        document.head.appendChild(style);
    }

    document.body.appendChild(toast);
    setTimeout(() => { if (toast.parentNode) toast.remove(); }, 3500);
}


function showConfirmToast(message) {

    return new Promise((resolve) => {
        // Remove existing
        const existing = document.getElementById('ems-confirm-overlay');
        if (existing) existing.remove();

        const overlay = document.createElement('div');
        overlay.id = 'ems-confirm-overlay';
        overlay.style.cssText = `
            position: fixed; inset: 0;
            background: rgba(0,0,0,0.45);
            backdrop-filter: blur(3px);
            display: flex; justify-content: center; align-items: center;
            z-index: 9998;
        `;

        overlay.innerHTML = `
            <div style="
                background:#fff; border-radius:10px; padding:2rem; max-width:380px; width:90%;
                box-shadow:0 10px 30px rgba(0,0,0,0.2); text-align:center; animation:slideUp 0.2s ease;
            ">
                <p style="font-size:1rem; font-weight:500; color:#1F2937; margin-bottom:1.5rem;">${message}</p>
                <div style="display:flex; gap:0.75rem; justify-content:center;">
                    <button id="confirm-yes" style="
                        padding:0.6rem 1.5rem; border:none; border-radius:6px;
                        background:#EF4444; color:#fff; font-weight:600; cursor:pointer; font-size:0.9rem;
                    ">Yes, Delete</button>
                    <button id="confirm-no" style="
                        padding:0.6rem 1.5rem; border:1px solid #D1D5DB; border-radius:6px;
                        background:#fff; color:#374151; font-weight:600; cursor:pointer; font-size:0.9rem;
                    ">Cancel</button>
                </div>
            </div>
        `;

        document.body.appendChild(overlay);

        overlay.querySelector('#confirm-yes').addEventListener('click', () => {
            overlay.remove();
            resolve(true);
        });
        overlay.querySelector('#confirm-no').addEventListener('click', () => {
            overlay.remove();
            resolve(false);
        });
        // Click outside to cancel
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) { overlay.remove(); resolve(false); }
        });
    });
}
