window.onload = function () {
      showLoading();
    fetchCarDetails();
    setupBackButton();
};

function fetchCarDetails() {
    const token = localStorage.getItem('jwtToken');
    if (!token) {
        window.location.href = '/login.html';
        return;
    }

    // Retrieve the car ID from the URL query parameter

    const params = new URLSearchParams(window.location.search);
    const carId = params.get('id');

    if (!carId) {
        alert('No car ID found in the URL. Params: '+params);
        window.location.href = 'dashboard.html'; // Redirect if no ID is found
        return;
    }

    // Make a GET request with the car ID as a query parameter
    fetch(`http://localhost:8080/user/viewcar?id=${carId}`, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch car details: ${response.statusText}`);
            }
            return response.json();
        })
        .then(data => renderCarDetails(data))
        .catch(error => {
            console.error('Error fetching car details:', error);
            alert('Error loading car details. Please try again.');
        })
        .finally(() => {
                hideLoading(); // Hide the spinner after fetch completes (success or failure)
            });
}

function renderCarDetails(car) {
    const carDetails = document.getElementById('carDetails');

    // Clear any previous content
    carDetails.innerHTML = '';

    // Create carousel slides
    const carSlides = car.images.map(
        (imageBase64, index) => `
        <div class="carousel-slide">
            <img src="data:image/jpeg;base64,${imageBase64}" alt="Car Image ${index + 1}">
        </div>`
    ).join('');

    // Add default image if no images are available
    const defaultSlide = `
        <div class="carousel-slide">
            <img src="static/images/default-car.png" alt="Default Car Image">
        </div>`;

    const carouselSlides = car.images.length ? carSlides : defaultSlide;

    // Render the car details and carousel
    carDetails.innerHTML = `
        <div class="car-detail">
            <div class="carousel-container">
                <div class="carousel-track">
                    ${carouselSlides}
                </div>
                <div class="carousel-nav">
                    <button class="carousel-btn prev-btn">&lt;</button>
                    <button class="carousel-btn next-btn">&gt;</button>
                </div>
            </div>
            <h2>${car.title}</h2>
            <p><strong>Description:</strong> ${car.description}</p>
            <p><strong>Tags:</strong> ${car.tags.join(', ')}</p>
        </div>
    `;

    // Initialize carousel navigation
    setupCarousel();
}

function setupCarousel() {
    const track = document.querySelector('.carousel-track');
    const slides = Array.from(track.children);
    const prevBtn = document.querySelector('.prev-btn');
    const nextBtn = document.querySelector('.next-btn');

    let currentIndex = 0;

    // Show the current slide
    function updateCarousel() {
        track.style.transform = `translateX(-${currentIndex * 100}%)`;
    }

    // Navigate slides
    prevBtn.addEventListener('click', () => {
        if (currentIndex > 0) {
            currentIndex -= 1;
            updateCarousel();
        }
    });

    nextBtn.addEventListener('click', () => {
        if (currentIndex < slides.length - 1) {
            currentIndex += 1;
            updateCarousel();
        }
    });
}


function setupBackButton() {
    const backBtn = document.getElementById('backBtn');
    backBtn.addEventListener('click', function () {
        window.location.href = 'dashboard.html';
    });
}

function showLoading() {
    document.getElementById('loading').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}
