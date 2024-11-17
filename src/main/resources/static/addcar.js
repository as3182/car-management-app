window.onload = function () {
    // Initialize event listener for the form
    const addCarForm = document.getElementById("addCarForm");

    addCarForm.addEventListener("submit", function (event) {
        event.preventDefault(); // Prevent form submission
        addCar();
    });
};

function addCar() {
    const title = document.getElementById("title").value;
    const description = document.getElementById("description").value;
    const tags = document.getElementById("tags").value.split(",").map(tag => tag.trim());
    const images = document.getElementById("images").files;
    const responseMessage = document.getElementById("responseMessage");

    if (images.length === 0) {
        responseMessage.textContent = "Please upload at least one image.";
        responseMessage.className = "error";
        return;
    }

    const formData = new FormData();
    formData.append("carDetails", JSON.stringify({ title, description, tags }));

    for (const image of images) {
        formData.append("images", image);
    }

    const token = localStorage.getItem("jwtToken");
    if (!token) {
        responseMessage.textContent = "You are not logged in. Redirecting to login page...";
        responseMessage.className = "error";
        setTimeout(() => {
            window.location.href = "/login.html";
        }, 2000);
        return;
    }

    fetch("http://localhost:8080/user/addcar", {
        method: "POST",
        headers: {
            "Authorization": `Bearer ${token}`
        },
        body: formData
    })
    .then(response => {
        if (response.ok) {
            responseMessage.textContent = "Car added successfully!";
            responseMessage.className = "success";
            document.getElementById("addCarForm").reset();
        } else {
            return response.text().then(error => {
                throw new Error(error);
            });
        }
    })
    .catch(error => {
        responseMessage.textContent = `Error: ${error.message}`;
        responseMessage.className = "error";
    });
}
