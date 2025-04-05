import {
    createCaseForStepById,
    createFlowForAccountById, createStepForFlowById,
    fetchCurrentAccountId,
    fetchFlowsByAccountId, fetchToEditFlow
} from "./api.js";

import {
    cloneFlowNotFoundLabelTemplate,
    cloneFlowItemTemplate,
    cloneStepItemTemplate,
    cloneCaseTemplate,
    cloneCreateCaseBtnTemplate,
    cloneCreateStepBtnTemplate, cloneFlowDetailsTemplate, cloneFlowDetailsEditTemplate
} from "../../template-loader.js";

import {
    selectItem,
    clearContainers,
    openModalWindow,
    closeModalWindow,
    showError
} from "../../util.js";

import {validateCreatedCase, validateFlow} from "../../validation.js";
import {Flow, Step, Case} from "./classes.js";

// components
const DOM = {
    flowContainer: document.getElementById("flow-container"),
    stepsContainer: document.getElementById("steps-container"),
    caseContainer: document.getElementById("case-container"),
    buttonHolderCreateCase: document.getElementById("create-case-btn-holder"),
    buttonHolderCreateStep: document.getElementById("create-step-btn-holder"),
    buttonCreateFlow: document.getElementById("create-flow-btn"),
    modal: {
        createFlow: {
            window: document.getElementById("create-flow-modal-window"),
            inputTitle: document.getElementById("create-flow-title"),
            inputDescription: document.getElementById("create-flow-description"),
            inputTargetPercentage: document.getElementById("create-flow-percentage"),
            submitButton: document.getElementById("submit-create-flow-btn"),
            errorBlock: document.getElementById("create-flow-error-block"),
            errorMessage: document.getElementById("create-flow-error-message"),
        },
        createStep: {
            window: document.getElementById("create-step-modal-window"),
            inputFlowId: document.getElementById("create-step-flowId"),
            inputDate: document.getElementById("create-step-date"),
            submitButton: document.getElementById("submit-create-step-btn"),
            errorBlock: document.getElementById("create-step-error-block"),
            errorMessage: document.getElementById("create-step-error-message")
        },
        createCase: {
            window: document.getElementById("create-case-modal-window"),
            inputStepId: document.getElementById("create-case-stepId"),
            inputDescription: document.getElementById("create-case-description"),
            inputCounting: document.getElementById("create-case-counting"),
            inputPercent: document.getElementById("create-case-percent"),
            errorBlock: document.getElementById("create-case-error-block"),
            errorMessage: document.getElementById("create-case-error-message"),
            submitButton: document.getElementById("submit-create-case-btn"),
        }
    }
};

// initialization
let currentAccount = null;
let flowsCache = [];

window.onload = loadPage;
DOM.buttonCreateFlow.addEventListener("click", openCreateFlowModalWindow);
DOM.modal.createFlow.submitButton.addEventListener("click", event => createNewFlow(event));
DOM.modal.createStep.submitButton.addEventListener("click", event => createNewStep(event));
DOM.modal.createCase.submitButton.addEventListener("click", event => createNewCase(event));
DOM.modal.createCase.inputCounting.addEventListener("change", event => handlePercentInputAvailable(event));
document.querySelectorAll(".flexible").forEach(item =>
    item.addEventListener("input", () => adjustTextarea(item))
);

async function loadPage() {
    try {
        currentAccount = await fetchCurrentAccountId();
        flowsCache = (await fetchFlowsByAccountId(currentAccount)).map(Flow.flowFromJSON);

        loadFlows(flowsCache);
    } catch (error) {
        console.log("Error", error.detail);
    }
}

function loadFlows(flows) {
    clearContainers(
        DOM.flowContainer,
        DOM.caseContainer
    );

    if (flows.length > 0) {
        for (const flow of flows) {
            const clone = cloneFlowItemTemplate();
            const flowItem = clone.querySelector("#flow-item");

            flowItem.textContent = flow.title.length < 15 ? flow.title : flow.title.substring(0, 15) + "...";
            flowItem.addEventListener("click", () => {
                selectItem(flowItem, "#flow-item")
                loadSteps(flow.steps, flow.id);
                showFlowDetails(flow);
            });

            DOM.flowContainer.appendChild(clone);
        }
    } else {
        DOM.flowContainer.appendChild(cloneFlowNotFoundLabelTemplate());
    }
}

function loadSteps(steps, flowId) {
    clearContainers(
        DOM.stepsContainer,
        DOM.caseContainer,
        DOM.buttonHolderCreateStep,
        DOM.buttonHolderCreateCase
    );

    steps.sort((a, b) => a.day.getTime() - b.day.getTime());
    for (const step of steps) {
        const clone = cloneStepItemTemplate();
        const stepItem = clone.querySelector("#step");

        stepItem.textContent = step.getFormatDate();
        stepItem.addEventListener("click", () => {
            selectItem(stepItem, "#step");
            loadCases(step.cases, step.id);
        });

        DOM.stepsContainer.appendChild(clone);
    }

    const createStepBtn = cloneCreateStepBtnTemplate();
    createStepBtn.querySelector("#create-step-btn")
        .addEventListener("click", () => openCreateStepModalWindow(flowId));
    DOM.buttonHolderCreateStep.appendChild(createStepBtn);
}

function loadCases(cases, stepId) {
    clearContainers(
        DOM.caseContainer,
        DOM.buttonHolderCreateCase
    );

    for (const case1 of cases) {
        const clone = cloneCaseTemplate();

        clone.querySelector("#case-text").textContent = case1.text;
        clone.querySelector("#case-percent").textContent = case1.counting ? case1.percent : "";

        DOM.caseContainer.appendChild(clone);
    }

    const createCaseTmp = cloneCreateCaseBtnTemplate();
    createCaseTmp.querySelector("#create-case-btn")
        .addEventListener("click", () => openCreateCaseModalWindow(stepId));
    DOM.buttonHolderCreateCase.appendChild(createCaseTmp);
}

// editing flow
function showFlowDetails(flow) {
    clearContainers(DOM.caseContainer);

    const clone = cloneFlowDetailsTemplate();
    clone.querySelector("#flow-details__title").textContent = flow.title;
    clone.querySelector("#flow-details__description").textContent = flow.description;
    clone.querySelector("#flow-details__percentage-value").textContent = flow.targetPercentage;
    clone.querySelector("#flow-details__edit-btn")
        .addEventListener("click", () => openFlowDetailsEditor(flow));

    DOM.caseContainer.appendChild(clone);
}

function openFlowDetailsEditor(flow) {
    clearContainers(DOM.caseContainer);

    const clone = cloneFlowDetailsEditTemplate();

    const title = clone.querySelector("#flow-details-edit__title");
    const description = clone.querySelector("#flow-details-edit__description");
    const percentage = clone.querySelector("#flow-details-edit__percentage-value");
    const saveBtn = clone.querySelector("#flow-details-edit__save-btn");
    const cancelBtn = clone.querySelector("#flow-details-edit__cancel-btn");

    title.value = flow.title;
    description.value = flow.description;
    description.addEventListener("input", () => adjustTextarea(description));
    percentage.value = flow.targetPercentage;
    cancelBtn.addEventListener("click", (event) => {
        event.preventDefault();
        showFlowDetails(flow)
    });
    saveBtn.addEventListener("click", (event) => editFlow(event, flow));

    DOM.caseContainer.appendChild(clone);
}

async function editFlow(event, flow) {
    event.preventDefault();

    const flowDetails = Flow.simpleFlow(
        document.querySelector("#flow-details-edit__title").value,
        document.querySelector("#flow-details-edit__description").value,
        document.querySelector("#flow-details-edit__percentage-value").value
    );

    try {
        validateFlow(flowDetails);
        flow.title = flowDetails.title;
        flow.description = flowDetails.description;
        flow.targetPercentage = flowDetails.targetPercentage;

        const editedFlow = await fetchToEditFlow(flow);
        await loadPage();
        showFlowDetails(editedFlow);
    } catch (error) {
        showError(
            error,
            document.querySelector(".flow-details-edit__error"),
            document.querySelector("#flow-details-edit__error-message")
        );
    }
}


// modal - create flow
function openCreateFlowModalWindow() {
    DOM.modal.createFlow.errorBlock.style.display = "none";
    openModalWindow(DOM.modal.createFlow.window);
}

function adjustTextarea(item) {
    item.style.height = "auto";
    item.style.height = item.scrollHeight + "px";
}

async function createNewFlow(event) {
    event.preventDefault();
    DOM.modal.createFlow.submitButton.disabled = true;

    const createdFlow = Flow.simpleFlow(
        DOM.modal.createFlow.inputTitle.value,
        DOM.modal.createFlow.inputDescription.value,
        DOM.modal.createFlow.inputTargetPercentage.value
    );

    try {
        validateFlow(createdFlow);
        let responseFlow = await createFlowForAccountById(createdFlow, currentAccount);
        responseFlow = Flow.flowFromJSON(responseFlow);

        flowsCache.push(responseFlow);
        loadFlows(flowsCache);
        closeModalWindow(DOM.modal.createFlow.window);
        clearFlowModalWindow();
    } catch (error) {
        console.log("Error: ", error);
        showError(
            error,
            DOM.modal.createFlow.errorBlock,
            DOM.modal.createFlow.errorMessage
        );
    } finally {
        DOM.modal.createFlow.submitButton.disabled = false;
    }
}

function clearFlowModalWindow() {
    DOM.modal.createFlow.inputTitle.value = "";
    DOM.modal.createFlow.inputDescription.value = "";
    DOM.modal.createFlow.inputTargetPercentage.value = "";
}


// modal - create step
function openCreateStepModalWindow(flowId) {
    DOM.modal.createStep.errorBlock.style.display = "none";
    DOM.modal.createStep.inputFlowId.value = flowId;
    openModalWindow(DOM.modal.createStep.window);
}

async function createNewStep(event) {
    event.preventDefault();
    DOM.modal.createStep.submitButton.disabled = true;

    const flowId = DOM.modal.createStep.inputFlowId.value;
    const currentFlow = flowsCache.find(flow => flow.id === Number(flowId));
    const step = Step.simpleStep(
        DOM.modal.createStep.inputDate.value
    );

    if (!currentFlow) {
        throw new Error(`Flow with id ${flowId} not found`);    // never happen
    }

    try {
        let responseStep = await createStepForFlowById(step, flowId);
        responseStep = Step.stepFromJSON(responseStep);

        currentFlow.steps.push(responseStep);
        loadSteps(currentFlow.steps, flowId);
        closeModalWindow(DOM.modal.createStep.window);
        clearStepModalWindow();
    } catch (error) {
        console.log(error);
        showError(
            error,
            DOM.modal.createStep.errorBlock,
            DOM.modal.createStep.errorMessage
        );
    } finally {
        DOM.modal.createStep.submitButton.disabled = false;
    }
}

function clearStepModalWindow() {
    DOM.modal.createStep.inputFlowId.value = "";
    DOM.modal.createStep.inputDate.value = "";
}


//modal - create case
function openCreateCaseModalWindow(stepId) {
    DOM.modal.createCase.errorBlock.style.display = "none";
    DOM.modal.createCase.inputStepId.value = stepId;
    openModalWindow(DOM.modal.createCase.window);
}

function handlePercentInputAvailable(event) {
    const inputPercent = DOM.modal.createCase.inputPercent;

    if (event.target.checked) {
        inputPercent.removeAttribute("disabled");
    } else {
        inputPercent.setAttribute("disabled", "true");
    }
}

async function createNewCase(event) {
    event.preventDefault();
    DOM.modal.createCase.submitButton.disabled = true;

    const stepId = DOM.modal.createCase.inputStepId.value;
    const currentStep = flowsCache
        .flatMap(flow => flow.steps)
        .find(step => step.id === Number(stepId));
    const newCase = Case.simpleCase(
        DOM.modal.createCase.inputDescription.value,
        DOM.modal.createCase.inputPercent.value,
        DOM.modal.createCase.inputCounting.checked
    );

    if (!currentStep) {
        throw new Error(`Step with id ${stepId} not found`);    // never happen
    }

    try {
        validateCreatedCase(newCase);
        let responseCase = await createCaseForStepById(newCase, stepId);
        responseCase = Case.caseFromJSON(responseCase);

        currentStep.cases.push(responseCase);
        loadCases(currentStep.cases, stepId);
        closeModalWindow(DOM.modal.createCase.window);
        clearCaseModalWindow();
    } catch (error) {
        console.log(error);
        showError(
            error,
            DOM.modal.createCase.errorBlock,
            DOM.modal.createCase.errorMessage
        );
    } finally {
        DOM.modal.createCase.submitButton.disabled = false;
    }
}

function clearCaseModalWindow() {
    DOM.modal.createCase.inputStepId.value = "";
    DOM.modal.createCase.inputDescription.value = "";
    DOM.modal.createCase.inputPercent.value = "";
    DOM.modal.createCase.inputCounting.checked = false;
}


