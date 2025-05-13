import {fetchCurrentAccountId, fetchShortFlowsByAccountId, fetchUniqueMonthsByFlowId} from "./api.js";
import {
    cloneFlowItemStatisticsTemplate,
    cloneFlowNotFoundLabelTemplate,
    cloneMonthItemTemplate
} from "../../pub/js/template-loader.js";
import {clearContainers} from "../../pub/js/util.js";

const DOM = {
    optionPanel: {
        flows: document.querySelector("#option-flow-list"),
        moths: document.querySelector("#months-list"),
        flowTabBtn: document.querySelector("#flow-tab-btn"),
        scopeTabBtn: document.querySelector("#scope-tab-btn"),
    }
};
let currentAccountId = null;
let selectedFlow = null;
let selectedScope = null;

// init
window.onload = setupPage;

async function setupPage() {
    await initCurrentAccountId();
    await fillFlows();
    await fillMonths(1);
    setupButtons();
    initQuickSelectionButtons();
}

async function initCurrentAccountId() {
    currentAccountId = await fetchCurrentAccountId();
}


// tabs
function setupButtons() {
    const tabButtons = document.querySelectorAll(".tab-button");
    const tabs = document.querySelectorAll(".tab");
    const tabIds = Array.from(tabs).map(tab => tab.id);

    tabButtons.forEach((button, index) => {
        button.addEventListener("click", () => {
            tabButtons.forEach(btn => btn.classList.remove("tab-button--active"));
            button.classList.add("tab-button--active");
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


// flows tab
async function fillFlows() {
    const flows = await fetchShortFlowsByAccountId(currentAccountId);

    if (!flows || flows.length === 0) {
        const clone = cloneFlowNotFoundLabelTemplate();
        DOM.optionPanel.flows.appendChild(clone);
        disableOptionPane();
    } else {
        flows.forEach(flow => {
            const clone = cloneFlowItemStatisticsTemplate();
            const flowItem = clone.querySelector(".flow");

            flowItem.textContent = flow.title;
            flowItem.addEventListener("click", () => {
                setFlowSelected(flowItem, flow.id)
            });

            DOM.optionPanel.flows.appendChild(clone);
        });

        const firstFlow = DOM.optionPanel.flows.querySelector(".flow");
        await setFlowSelected(firstFlow, flows[0].id);
    }
}

async function setFlowSelected(flowItem, flowId) {
    const allFlows = DOM.optionPanel.flows.querySelectorAll(".flow");

    allFlows.forEach(flow => flow.classList.remove("selected"));
    flowItem.classList.add("selected");
    selectedFlow = flowId;
    await fillMonths(flowId);
}

function disableOptionPane() {
    const scopeTabBtn = DOM.optionPanel.scopeTabBtn;
    scopeTabBtn.disabled = true;
    scopeTabBtn.classList.toggle("disabled");
}


// scope tab
function initQuickSelectionButtons() {
    const quickBtns = [
        document.querySelector("#last-week-qb"),
        document.querySelector("#last-month-qb"),
        document.querySelector("#last-year-qb"),
    ];

    quickBtns.forEach(btn => {
        btn.addEventListener("click", () => setQuickButtonSelected(btn));
    });
}

function setQuickButtonSelected(btn) {
    clearSelections();

    btn.classList.toggle("selected");
    selectedScope = btn.textContent;
}

async function fillMonths(flowId) {
    clearContainers(DOM.optionPanel.moths);
    const months = await fetchUniqueMonthsByFlowId(flowId);

    months.forEach(month => {
        const clone = cloneMonthItemTemplate();

        clone.querySelector(".month-item__month").textContent = month.month;
        clone.querySelector(".month-item__year").textContent = month.year;

        const btn = clone.querySelector(".month-picker__month-item");
        btn.addEventListener("click", () => setMonthSelected(btn, month));

        DOM.optionPanel.moths.appendChild(clone);
    });
}

function setMonthSelected(btn, monthInfo) {
    clearSelections();

    btn.classList.toggle("selected");
    selectedScope = monthInfo;
}

function clearSelections() {
    const allMonthBtns = document.querySelectorAll(".month-picker__month-item");
    const allQuickRangeBtns = document.querySelectorAll(".quick-range-button");

    [...allQuickRangeBtns, ...allMonthBtns].forEach(btn => btn.classList.remove("selected"));
}

