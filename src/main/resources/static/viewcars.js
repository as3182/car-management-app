window.onload = function () {
    showLoading();
    fetchCars();

};

function fetchCars() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    fetch('https://car-management-app-ayzd.onrender.com/user/cars', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => response.json())
        .then(data => renderCars(data))
        .catch(error => console.error('Error fetching cars:', error))
        .finally(() => {
                hideLoading(); // Hide the spinner after fetch completes (success or failure)
            });
}

function renderCars(cars) {
    const carContainer = document.getElementById('carContainer');
    carContainer.innerHTML = ''; // Clear previous content

if (!cars || cars.length === 0) {
        carContainer.innerHTML = `<p>No cars found.</p>`;
        return;
    }

    cars.forEach(car => {
        const carCard = document.createElement('div');
        carCard.classList.add('car-card');

        const carImage = car.images && car.images.length > 0
            ? `<img src="data:image/jpeg;base64,${car.images[0]}" alt="Car Image">`
            : `<img src="static/images/default-car.png" alt="Default Car Image">`;

        carCard.innerHTML = `
            ${carImage}
            <h3>${car.title}</h3>
            <p>${car.description}</p>
            <div class="actions">
                <a href="viewcar.html?id=${car.id}">View Details</a>
                <a href="updatecar.html?id=${car.id}">Edit</a>
                <button onclick="deleteCar(${car.id})">Delete</button>
            </div>
        `;

        carContainer.appendChild(carCard);
    });
}

function deleteCar(carId) {
    const token = localStorage.getItem('jwtToken');

    // Confirm before deleting
    if (!confirm('Are you sure you want to delete this car?')) {
        return;
    }

    // Make the DELETE request with the carId as a query parameter
    fetch(`https://car-management-app-ayzd.onrender.com/user/cars/delete?carId=${carId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
    .then(response => {
        if (response.ok) {
            alert('Car deleted successfully!');
            fetchCars(); // Refresh car list after deletion
        } else {
            return response.text().then(text => {
                throw new Error(text);
            });
        }
    })
    .catch(error => alert('Error deleting car: ' + error.message));
}

function showLoading() {
    document.getElementById('loading').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

