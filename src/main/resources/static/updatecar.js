document.getElementById('updateCarForm').addEventListener('submit', async function (event) {
    event.preventDefault(); // Prevent form submission

    const token = localStorage.getItem('jwtToken');
    if (!token) {
        alert('You must be logged in to update a car.');
        window.location.href = '/login.html';
        return;
    }

    // Extract car ID from the URL
    const urlParams = new URLSearchParams(window.location.search);
    const id = urlParams.get('id');
    console.log("Car ID:", id);
    if (!id) {
        alert('Car ID is missing in the URL.');
        window.location.href = 'allcars.html'; // Redirect to all cars page
        return;
    }

    // Collect form data
    const title = document.getElementById('title').value.trim();
    const description = document.getElementById('description').value.trim();
   const tags = document.getElementById('tags').value.trim();


    const imagesInput = document.getElementById('images');
    const images = Array.from(imagesInput.files); // Convert FileList to an array

    // Prepare the update request payload
    const formData = new FormData();
    formData.append('id', id); // Car ID is mandatory
    if (title) formData.append('title', title);
    if (description) formData.append('description', description);
   if (tags) formData.append('tags', tags); // Send tags as a comma-separated string
    images.forEach(image => formData.append('images', image)); // Attach each file

    try {
        // Make the PUT request to the backend
        const response = await fetch(`http://localhost:8080/user/updatecar?id=${id}`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${token}` // JWT token
            },
            body: formData // Send the FormData object
        });

        // Handle the response
        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(`Failed to update car: ${errorMessage}`);
        }

        const message = await response.text();
        document.getElementById('responseMessage').innerText = message;
    } catch (error) {
        console.error('Error updating car:', error);
        document.getElementById('responseMessage').innerText = 'An error occurred. Please try again.';
    }
});
