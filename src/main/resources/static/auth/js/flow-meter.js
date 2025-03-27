import {
    createFlowForAccountById,
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

import {selectItem, clearContainers} from "../../util.js";
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
        }
    }
};

// initialization
window.onload = loadPage;
DOM.buttonCreateFlow.addEventListener("click", openCreateFlowModalWindow);
DOM.modal.createFlow.inputDescription.addEventListener("input", adjustTextarea);
DOM.modal.createFlow.submitButton.addEventListener("click", event=> createNewFlow(event));

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

    DOM.buttonHolderCreateStep.appendChild(cloneCreateStepBtnTemplate());
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
    const window = DOM.modal.createFlow.window;

    window.classList.remove("hidden");
    window.addEventListener('click', event => {
        if (event.target === window) {
            window.classList.add("hidden");
        }
    });
}

function closeCreateFlowModalWindow() {
    DOM.modal.createFlow.window.classList.add("hidden");
}

function adjustTextarea() {
    const description = DOM.modal.createFlow.inputDescription;

    description.style.height = "auto";
    description.style.height = description.scrollHeight + "px";
}

async function createNewFlow(event) {
    event.preventDefault();

    const currentAccountId = await fetchCurrentAccountId();
    const createdFLow = Flow.createSimpleFlow(
        DOM.modal.createFlow.inputTitle.value,
        DOM.modal.createFlow.inputDescription.value,
        DOM.modal.createFlow.inputTargetPercentage.value
    );

    try {
        validateCreatedFlow(createdFLow);
        await createFlowForAccountById(createdFLow, currentAccountId);
        closeCreateFlowModalWindow();

        window.location.reload();
    } catch (error) {
        // todo:
        console.log("Error: ", error);
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

    static createSimpleFlow(title, description, targetPercentage) {
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
    constructor(id, createdAt, updatedAt, date, cases) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt
        this.date = new Date(date);
        this.cases = cases;
    }

    getFormatDate() {
        let day = this.date.getDate();
        let month = this.date.getMonth() + 1;
        let year = this.date.getFullYear() + "";

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


