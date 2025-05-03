import {validatePassword, validateUsername} from "./validation.js";
import {fetchToLogin} from "./open-api.js";
import {showError, startOAuthFlow} from "./util.js";

const form = document.querySelector("#login-form");
const googleAuthBtn = document.querySelector("#google-oauthentication__block");

form.addEventListener("submit", login);
googleAuthBtn.addEventListener("click", startOAuthFlow);


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
        await fetchToLogin(data);

        window.location.href = "/";
    } catch (error) {
        console.log("Error: ", error)
        showError(
            error,
            document.querySelector(".login-error"),
            document.querySelector("#login-error-message")
        );
    } finally {
        submitButton.disabled = false;
    }
}

function validateLoginFields(data) {
    validateUsername(data.username);
    validatePassword(data.password);
}