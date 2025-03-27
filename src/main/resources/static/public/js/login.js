const form = document.getElementById("login-form");

form.addEventListener("submit", login);


async function login(e) {
    e.preventDefault();
    const submitButton = document.querySelector("#login-btn");
    submitButton.disabled = true;

    const data = {
        username: document.getElementById("username").value,
        password: document.getElementById("password").value
    }

    try {
        validateLoginFields(data);

        const response = await fetch("/api/public/login", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const errData = await response.json();
            throw new Error(errData.detail || "Login failed");
        }

        const result = await response.json();
        console.log("Login successful: ", result)
        window.location.href = "/";
    } catch (error) {
        console.log("Error: ", error)
        showError(error);
    } finally {
        submitButton.disabled = false;
    }
}

function validateLoginFields(data) {
    if (!data.username || data.username.length < 3) {
        throw new Error("Username length must be at least 3 symbols ");
    }

    if (!data.password || data.password.length < 8) {
        throw new Error("Password length must be at least 8 symbols ");
    }
}

function showError(error) {
    const loginError = document.querySelector(".login-error");
    const errorHolder = document.querySelector("#login-error-holder");
    errorHolder.textContent = `Error: ${error.message}`;
    loginError.style.display = "block";
}