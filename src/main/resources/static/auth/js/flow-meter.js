import {
    createFlowForAccountById, createStepForFlowById,
    fetchCurrentAccountId,
    fetchFlowsByAccountId
} from "./api.js";

import {
    cloneFlowNotFoundLabelTemplate,
    cloneFlowItemTemplate,
    cloneStepItemTemplate,
    cloneCaseTemplate,
    cloneCreateCaseBtnTemplate,
    cloneCreateStepBtnTemplate
} from "../../template-loader.js";

import {
    selectItem,
    clearContainers,
    openModalWindow,
    closeModalWindow,
    showError
} from "../../util.js";

import {validateCreatedFlow} from "../../validation.js";

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
        }
    }
};

// initialization
window.onload = loadPage;
DOM.buttonCreateFlow.addEventListener("click", openCreateFlowModalWindow);
DOM.modal.createFlow.inputDescription.addEventListener("input", adjustTextarea);
DOM.modal.createFlow.submitButton.addEventListener("click", event=> createNewFlow(event));
DOM.modal.createStep.submitButton.addEventListener("click", event => createNewStep(event));

async function loadPage() {
    try {
        const currentAccount = await fetchCurrentAccountId();
        await loadFlows(currentAccount);
    } catch (error) {
        console.log("Error: ", error.message);
    }
}

async function loadFlows(accountId) {
    const flows = (await fetchFlowsByAccountId(accountId)).map(Flow.flowFromJSON);

    if (flows.length > 0) {
        for (const flow of flows) {
            const clone = cloneFlowItemTemplate();
            const flowItem = clone.querySelector("#flow-item");

            flowItem.textContent = flow.title;
            flowItem.addEventListener("click", () => {
                selectItem(flowItem, "#flow-item")
                loadSteps(flow);
            });

            DOM.flowContainer.appendChild(clone);
        }
    } else {
        DOM.flowContainer.appendChild(cloneFlowNotFoundLabelTemplate());
    }
}

function loadSteps(flow) {
    clearContainers(
        DOM.stepsContainer,
        DOM.caseContainer,
        DOM.buttonHolderCreateStep,
        DOM.buttonHolderCreateCase
    );

    for (const step of flow.steps) {
        const clone = cloneStepItemTemplate();
        const stepItem = clone.querySelector("#step");

        stepItem.textContent = step.getFormatDate();
        stepItem.addEventListener("click", () => {
            selectItem(stepItem, "#step");
            loadCases(step);
        });

        DOM.stepsContainer.appendChild(clone);
    }

    const createStepBtn = cloneCreateStepBtnTemplate();
    createStepBtn.querySelector("#create-step-btn")
        .addEventListener("click", () => openCreateStepModalWindow(flow.id));
    DOM.buttonHolderCreateStep.appendChild(createStepBtn);
}

function loadCases(step) {
    clearContainers(
        DOM.caseContainer,
        DOM.buttonHolderCreateCase
    );

    for (const caseEntity of step.cases) {
        const clone = cloneCaseTemplate();

        clone.querySelector("#case-text").textContent = caseEntity.text;
        clone.querySelector("#case-percent").textContent = caseEntity.percent;

        DOM.caseContainer.appendChild(clone);
    }

    DOM.buttonHolderCreateCase.appendChild(cloneCreateCaseBtnTemplate());
}


// modal - create flow
function openCreateFlowModalWindow() {
    DOM.modal.createFlow.errorBlock.style.display = "none";
    openModalWindow(DOM.modal.createFlow.window);
}

function adjustTextarea() {
    const description = DOM.modal.createFlow.inputDescription;

    description.style.height = "auto";
    description.style.height = description.scrollHeight + "px";
}

async function createNewFlow(event) {
    event.preventDefault();
    DOM.modal.createFlow.submitButton.disabled = true;

    const currentAccountId = await fetchCurrentAccountId();
    const createdFLow = Flow.simpleFlow(
        DOM.modal.createFlow.inputTitle.value,
        DOM.modal.createFlow.inputDescription.value,
        DOM.modal.createFlow.inputTargetPercentage.value
    );

    try {
        validateCreatedFlow(createdFLow);
        await createFlowForAccountById(createdFLow, currentAccountId);
        closeModalWindow(DOM.modal.createFlow.window);

        window.location.reload();
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
    const step = Step.simpleStep(
        DOM.modal.createStep.inputDate.value
    );

    try {
        // todo: here can be some validation for duplicates
        await createStepForFlowById(step, flowId);
        closeModalWindow(DOM.modal.createStep.window);

        window.location.reload();
    } catch (error) {
        console.log(error)
        showError(
            error,
            DOM.modal.createStep.errorBlock,
            DOM.modal.createStep.errorMessage
        );
    } finally {
        DOM.modal.createStep.submitButton.disabled = false;
    }
}

// classes
class Flow {
    constructor(id, createdAt, updatedAt, title, description, targetPercentage, steps) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.title = title;
        this.description = description;
        this.targetPercentage = targetPercentage;
        this.steps = steps;
    }

    static simpleFlow(title, description, targetPercentage) {
        return new Flow(
            null,
            null,
            null,
            title,
            description,
            targetPercentage,
            null
        );
    }

    static flowFromJSON(jsonObject) {
        const steps = jsonObject.steps.map(Step.stepFromJSON);

        return new Flow(
            jsonObject.id,
            jsonObject.createdAt,
            jsonObject.updatedAt,
            jsonObject.title,
            jsonObject.description,
            jsonObject.targetPercentage,
            steps
        );
    }
}

class Step {
    constructor(id, createdAt, updatedAt, day, cases) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt
        this.day = new Date(day);
        this.cases = cases;
    }

    getFormatDate() {
        let day = this.day.getDate();
        let month = this.day.getMonth() + 1;
        let year = this.day.getFullYear() + "";

        day = day > 9 ? day : `0${day}`;
        month = month > 9 ? month : `0${month}`;
        year = year.substring(2, 4);

        return `${day}.${month}.${year}`;
    }

    static stepFromJSON(jsonObject) {
        const cases = jsonObject.cases.map(Case.caseFromJSON);

        return new Step(
            jsonObject.id,
            jsonObject.createdAt,
            jsonObject.updatedAt,
            jsonObject.day,
            cases
        );
    }

    static simpleStep(day) {
        return new Step(
            null,
            null,
            null,
            day,
            null
        );
    }
}

class Case {
    constructor(text, percent) {
        this.text = text;
        this.percent = percent;
    }

    static caseFromJSON(jsonObject) {
        return new Case(
            jsonObject.text,
            jsonObject.percent
        );
    }
}


