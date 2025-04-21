import {validatePassword, validateUsername} from "./validation.js";

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
    validateUsername(data.username);
    validatePassword(data.password);
}

function showError(error) {
    const loginError = document.querySelector(".login-error");
    const errorHolder = document.querySelector("#login-error-message");
    errorHolder.textContent = `Error: ${error.message}`;
    loginError.style.display = "block";
}