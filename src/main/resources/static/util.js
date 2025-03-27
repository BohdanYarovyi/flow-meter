export function selectItem(component, selector) {
    document.querySelectorAll(selector).forEach(item =>
        item.classList.remove("selected")
    );
    component.classList.add("selected");
}

export function clearContainers(...containers) {
    containers.forEach(item => item.replaceChildren());
}