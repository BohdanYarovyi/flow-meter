const registrationForm = document.getElementById("registration-form");

registrationForm.addEventListener("submit", register);


async function register(e) {
    e.preventDefault();

    const submitButton = document.querySelector("#registrationBtn");
    submitButton.disabled = true;

    const data = {
        login: document.getElementById("login").value,
        email: document.getElementById("email").value,
        password: document.getElementById("password").value,
        firstname: document.getElementById("firstname").value,
        lastname: document.getElementById("lastname").value,
        patronymic: document.getElementById("patronymic").value,
        phone: document.getElementById("phone").value,
        dateOfBirth: document.getElementById("date-of-birth").value
    };
    const passwordConfirmation = document.getElementById("password-confirmation").value;

    try {
        validateRegistrationForm(data, passwordConfirmation);

        const response = await fetch("/api/public/registration", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(data)
        });

        if (!response.ok) { // від 200 до 299
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || "Registration failed");
        }

        document.location.href = "/login";
    } catch (error) {
        console.log("Registration error: ", error);
        showError(error);
    } finally {
        submitButton.disabled = false;
    }
}


function validateRegistrationForm(data, passwordConfirmation) {
    const loginRegex = /^[^@ ]+$/;
    if (!data.login || data.login.length < 3) {
        if (!loginRegex.test(data.login)) {
            throw new Error("Login cannot have @ or whitespaces");
        }

        throw new Error("Login length must be greater than 2 symbols");
    }

    const emailRegex = /^[A-Za-z0-9.]{2,30}@(gmail|googlemail)\.com$/i;
    if (!data.email || !emailRegex.test(data.email)) {
        throw new Error("Enter valid email like: my.email20@gmail.com")
    }

    if (!data.password || data.password.length < 8) {
        throw new Error("Password length must be at least 8 symbols");
    }

    if (data.password !== passwordConfirmation) {
        throw new Error("Passwords don't matches");
    }
}

function showError(error) {
    const registrationError = document.getElementsByClassName("registration-error");
    const errorHolder = document.getElementById("registration-error-holder");
    errorHolder.textContent = `Error: ${error.message}`;
    registrationError.item(0).style.display = "block";
}