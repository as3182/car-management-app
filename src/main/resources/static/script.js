window.onload = () => {
    showLoading();
    setupSidebarToggle();
    fetchUserData();
    fetchCarData();
    setupLogout();
};

function setupSidebarToggle() {
    const sidebar = document.getElementById('sidebar');
    const toggleBtn = document.getElementById('toggleBtn');
    const mainContent = document.querySelector('.main-content');

    toggleBtn.addEventListener('click', () => {
        sidebar.classList.toggle('hidden');
        mainContent.style.marginLeft = sidebar.classList.contains('hidden') ? '20px' : '300px';
    });
}

function fetchUserData() {
    const token = localStorage.getItem('jwtToken');
    if (!token) return redirectToLogin();

    fetch('https://car-management-app-ayzd.onrender.com/user/details', {
        headers: { 'Authorization': `Bearer ${token}` },
    })
        .then(res => res.json())
        .then(data => {
            document.getElementById('userName').textContent = data.name;
            document.getElementById('userUsername').textContent = data.username;
            document.getElementById('userNameInfo').textContent = data.name;
        })
        .catch(error => {
                    console.error('Error fetching cars:', error);
                    alert('Error loading cars. Please try again.');
                })
                .finally(() => {
                    hideLoading(); // Hide the spinner after the fetch completes
                });
}

function fetchCarData() {
    const token = localStorage.getItem('jwtToken');
    if (!token) return redirectToLogin();

    fetch('https://car-management-app-ayzd.onrender.com/user/cars', {
        headers: { 'Authorization': `Bearer ${token}` },
    })
        .then(res => res.json())
        .then(data => {
            document.getElementById('totalCars').textContent = data.length;
        }).catch(error => {
                      console.error('Error fetching cars:', error);
                      alert('Error loading cars. Please try again.');
                  })
                  .finally(() => {
                      hideLoading(); // Hide the spinner after the fetch completes
                  });
}

function setupLogout() {
    document.getElementById('logoutBtn').addEventListener('click', () => {
        localStorage.removeItem('jwtToken');
        window.location.href = '/login.html';
    });
}

function redirectToLogin() {
    window.location.href = '/login.html';
}

function showLoading() {
    document.getElementById('loading').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}
