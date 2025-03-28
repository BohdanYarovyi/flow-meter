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