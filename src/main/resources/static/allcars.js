window.onload = function () {
    showLoading(); // Show the loading spinner
    fetchAllCars(); // Fetch all cars
};

function fetchAllCars() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = '/login.html'; // Redirect if no token
        return;
    }

    // Fetch cars using the API
    fetch(`http://localhost:8080/user/allcars?page=0&size=10`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch cars: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            console.log(data);
            renderCars(data.content); // Assuming `content` holds the list of cars
        })
        .catch(error => {
            console.error('Error fetching cars:', error);
            alert('Error loading cars. Please try again.');
        })
        .finally(() => {
            hideLoading(); // Hide the spinner after the fetch completes
        });
}

function fetchCarsByTag(tag) {
    showLoading();
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = '/login.html'; // Redirect if no token
        return;
    }

    showLoading(); // Show the loading spinner for search

    fetch(`http://localhost:8080/user/search?tag=${encodeURIComponent(tag)}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch cars: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => {
            renderCars(data); // Render cars based on the search query
        })
        .catch(error => {
            console.error('Error fetching cars:', error);
            alert('Error loading cars. Please try again.');
        })
        .finally(() => {
            hideLoading(); // Hide the spinner after search fetch completes
        });
}

// Event listener for the search button
document.getElementById('searchBtn').addEventListener('click', function () {
    const tag = document.getElementById('searchBar').value.trim();
    if (!tag) {
        // If the search bar is empty, load all cars
        fetchAllCars();
    } else {
        // Fetch cars based on the search tag
        fetchCarsByTag(tag);
    }
});

function renderCars(cars) {
    const carContainer = document.getElementById('carContainer');
    carContainer.innerHTML = ''; // Clear any previous content

    if (!cars || cars.length === 0) {
        carContainer.innerHTML = `<p>No cars found.</p>`;
        return;
    }

    cars.forEach(car => {
        const carCard = document.createElement('div');
        carCard.classList.add('car-card');

        // Render car images
        const carImage = car.images && car.images.length > 0
                    ? `<img src="data:image/jpeg;base64,${car.images[0]}" alt="Car Image">`
                    : `<img src="defaultimg.png" alt="Default Car Image">`;

        carCard.innerHTML = `
            ${carImage}
            <h3>${car.title}</h3>
            <p>${car.description}</p>
            <p>${car.id}</p>
            <div class="actions">
                <a href="viewcar.html?id=${car.id}">View Details</a>
            </div>
        `;
        carContainer.appendChild(carCard);
    });
}

function showLoading() {
    document.getElementById('loading').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}
