export function selectItem(component, selector) {
    document.querySelectorAll(selector).forEach(item =>
        item.classList.remove("selected")
    );
    component.classList.add("selected");
}

export function clearContainers(...containers) {
    containers.forEach(item => item.replaceChildren());
}

export function openModalWindow(window) {
    window.classList.remove("hidden");

    window.addEventListener('click', event => {
        if (event.target === window) {
            window.classList.add("hidden");
        }
    });
}

export function closeModalWindow(window) {
    window.classList.add("hidden");
}


export function showError(error, errorBlock, errorMessage) {
    errorMessage.textContent = `Error: ${error.message}`;
    errorBlock.style.display = "block";
}

export function handleInputAvailableByCheckbox(event, targetInput) {
    if (event.target.checked) {
        targetInput.removeAttribute("disabled");
    } else {
        targetInput.setAttribute("disabled", "true");
    }
}

export function getConfirmationFromUser(message) {
    return new Promise((resolve) => {
        const components = {
            window: document.getElementById("confirmation-window"),
            message: document.getElementById("confirmation-window__message"),
            yesBtn: document.getElementById("confirmation-window__yes-btn"),
            noBtn: document.getElementById("confirmation-window__no-btn"),
        };
        components.message.textContent = message || "Are you sure?"
        components.window.classList.remove("hidden");

        const clenUp = () => {
            components.window.classList.add("hidden");
            components.yesBtn.removeEventListener("click", onYes);
            components.noBtn.removeEventListener("click", onNo);
        };

        const onYes = () => {
            clenUp();
            resolve(true);
        };

        const onNo = () => {
            clenUp();
            resolve(false);
        };

        components.yesBtn.addEventListener("click", onYes);
        components.noBtn.addEventListener("click", onNo);
    });
}

export function startOAuthFlow(event) {
    event.preventDefault();

    location.href = "/oauth2/authorization/google"

    console.log("OAuth2 flow by Google was started")
}