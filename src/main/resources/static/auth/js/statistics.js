window.onload = setupPage;

function setupPage() {
    setupButtons();
}

function setupButtons() {
    const tabButtons = document.querySelectorAll(".tab-button");
    const tabs = document.querySelectorAll(".tab");
    const tabIds = Array.from(tabs).map(tab => tab.id);

    tabButtons.forEach((button, index) => {
        button.addEventListener("click",() => {
            tabButtons.forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");
            swapTab(tabIds[index]);
        });
    });
}

function swapTab(tabId) {
    const allTabs = document.querySelectorAll('.tab');


    allTabs.forEach(tab => {
        tab.classList.toggle("hidden", tab.id !== tabId);
    });
}
