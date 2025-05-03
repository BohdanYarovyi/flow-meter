import {validateEmail, validatePassword, validatePasswordMatches} from "./validation.js";
import {showError, startOAuthFlow} from "./util.js";
import {fetchToRegister} from "./open-api.js";

const registrationForm = document.getElementById("registration-form");
const googleAuthBtn = document.querySelector("#google-oauthentication__block");

registrationForm.addEventListener("submit", register);
googleAuthBtn.addEventListener("click", startOAuthFlow);

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
        await fetchToRegister(data);

        document.location.href = "/login";
    } catch (error) {
        console.log("Registration error: ", error);
        showError(error,
            document.querySelector(".registration-error"),
            document.querySelector("#registration-error-message")
            );
    } finally {
        submitButton.disabled = false;
    }
}


function validateRegistrationForm(data, passwordConfirmation) {
    const loginRegex = /^[^@ ]+$/;

    if (!data.login || data.login.length < 3) {
        throw new Error("Login length must be greater than 2 symbols");
    }

    if (!loginRegex.test(data.login)) {
        throw new Error("Login cannot have @ or whitespaces");
    }

    validateEmail(data.email);
    validatePassword(data.password);
    validatePasswordMatches(data.password, passwordConfirmation);
}