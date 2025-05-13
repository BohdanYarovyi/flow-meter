import {
    createCaseForStepById,
    createFlowForAccountById,
    createStepForFlowById,
    fetchCurrentAccountId,
    fetchFlowsByAccountId, fetchToDeleteCaseById,
    fetchToDeleteFlowById, fetchToDeleteStepById,
    fetchToEditCase,
    fetchToEditFlow, getStepById, getTargetPercentageByStepId
} from "./api.js";

import {
    cloneCaseEditTemplate,
    cloneCaseTemplate,
    cloneCaseWrapperTemplate,
    cloneFlowDetailsEditTemplate,
    cloneFlowDetailsTemplate,
    cloneFlowItemTemplate,
    cloneFlowNotFoundLabelTemplate, cloneStepHeaderTemplate,
    cloneStepItemTemplate, cloneTimelinePanelTemplate
} from "../../pub/js/template-loader.js";

import {
    clearContainers,
    closeModalWindow, getConfirmationFromUser,
    handleInputAvailableByCheckbox,
    openModalWindow,
    selectItem,
    showError
} from "../../pub/js/util.js";

import {validateCase, validateFlow} from "../../pub/js/validation.js";
import {Case, Flow, Step} from "./classes.js";

// components
const DOM = {
    flowContainer: document.getElementById("flow-container"),
    timelinePanel: document.getElementById("timeline-panel"),
    stepHeader: document.getElementById("step-header"),
    caseContainer: document.getElementById("case-container"),
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
let flowsCache = [];    // it can be not a simple array, it can be Tree structure for optimization

window.onload = loadPage;
DOM.buttonCreateFlow.addEventListener("click", openCreateFlowModalWindow);
DOM.modal.createFlow.submitButton.addEventListener("click", event => createNewFlow(event));
DOM.modal.createStep.submitButton.addEventListener("click", event => createNewStep(event));
DOM.modal.createCase.submitButton.addEventListener("click", event => createNewCase(event));
DOM.modal.createCase.inputCounting.addEventListener("change", event => handleInputAvailableByCheckbox(event, DOM.modal.createCase.inputPercent));
document.querySelectorAll(".flexible").forEach(item =>
    item.addEventListener("input", () => adjustTextarea(item))
);

async function loadPage() {
    try {
        currentAccount = await fetchCurrentAccountId();
        flowsCache = (await fetchFlowsByAccountId(currentAccount)).map(Flow.flowFromJSON);

        loadFlows();
    } catch (error) {
        console.log("Error", error.detail);
    }
}


// flow
function loadFlows() {
    clearContainers(
        DOM.flowContainer,
        DOM.caseContainer
    );

    if (flowsCache.length > 0) {
        for (const flow of flowsCache) {
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


// editing flow
function openFlowDetailsEditor(flow) {
    clearContainers(DOM.caseContainer);

    const clone = cloneFlowDetailsEditTemplate();

    const editForm = {
        title: clone.querySelector("#flow-details-edit__title"),
        description: clone.querySelector("#flow-details-edit__description"),
        percentage: clone.querySelector("#flow-details-edit__percentage-value"),
        saveBtn: clone.querySelector("#flow-details-edit__save-btn"),
        cancelBtn: clone.querySelector("#flow-details-edit__cancel-btn"),
        deleteBtn: clone.querySelector("#flow-details-edit__delete-btn")
    }

    editForm.title.value = flow.title;
    editForm.description.value = flow.description;
    editForm.percentage.value = flow.targetPercentage;

    editForm.description.addEventListener("input", () => adjustTextarea(editForm.description));
    editForm.deleteBtn.addEventListener("click", (event) => deleteFlow(event, flow));
    editForm.saveBtn.addEventListener("click", (event) => editFlow(event, flow));
    editForm.cancelBtn.addEventListener("click", (event) => {
        event.preventDefault();
        showFlowDetails(flow);
    });

    DOM.caseContainer.appendChild(clone);
    adjustTextarea(editForm.description)
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

async function deleteFlow(event, flow) {
    event.preventDefault();

    const confirmationMessage = `Are you really want to delete "${flow.title}" flow?`;
    const confirmation = await getConfirmationFromUser(confirmationMessage);
    if (confirmation) {
        try {
            await fetchToDeleteFlowById(flow.id);
            removeFlowFromCache(flow.id);
            loadFlows();
        } catch (error) {
            showError(
                error,
                document.querySelector(".flow-details-edit__error"),
                document.querySelector("#flow-details-edit__error-message")
            );
        }
    }
}

function removeFlowFromCache(flowId) {
    const index = flowsCache.findIndex(flow => flow.id === flowId);
    if (index !== -1) {
        flowsCache.splice(index, 1);
    } else {
        console.warn(`Such flow not found in cache`);
    }
}


// step
function loadSteps(steps, flowId) {
    clearContainers(
        DOM.timelinePanel,
        DOM.caseContainer,
        DOM.stepHeader
    );

    const clone = cloneTimelinePanelTemplate();
    const stepContainer = clone.querySelector(".timeline");
    clone.querySelector("#create-step-btn")
        .addEventListener("click", () => openCreateStepModalWindow(flowId));

    DOM.timelinePanel.appendChild(clone);

    steps.sort((a, b) => a.day.getTime() - b.day.getTime());
    for (const step of steps) {
        const clone = cloneStepItemTemplate();
        const stepItem = clone.querySelector("#step");

        stepItem.textContent = step.getFormatDate();
        stepItem.addEventListener("click", () => {
            selectItem(stepItem, "#step");
            loadCases(step.cases, step.id);
        });

        stepContainer.appendChild(clone);
    }
}

async function deleteStep(stepId) {
    const flow = flowsCache.find(flow => {
        return flow.steps.some(step => step.id === stepId);
    });

    const confirmationMessage = `Are you really want to delete this step?`;
    const confirmation = await getConfirmationFromUser(confirmationMessage);
    if (confirmation) {
        try {
            await fetchToDeleteStepById(stepId);
            removeStepFromCache(flow.id, stepId);

            loadSteps(flow.steps, flow.id);
        } catch (error) {
            console.log(error);
            showError(
                error,
                document.getElementById("case-block__error"),
                document.getElementById("case-block__error-message"),
            );
        }
    }
}

function removeStepFromCache(flowId, stepId) {
    for (const flow of flowsCache) {
        if (flow.id === flowId) {
            const stepIndex = flow.steps.findIndex(step => step.id === stepId);
            if (stepIndex !== -1) {
                flow.steps.splice(stepIndex, 1);
                return;
            }
        }
    }
    console.warn(`Such step not found in cache`);
}


// case
function loadCases(cases, stepId) {
    clearContainers(
        DOM.caseContainer,
    );

    cases.forEach(case1 => {
        const clone = cloneCaseWrapperTemplate();
        putCaseInWrapper(case1, clone.querySelector("#case"), stepId);
        DOM.caseContainer.appendChild(clone);
    });

    addStepHeader(stepId)
}

function putCaseInWrapper(case1, wrapper, stepId) {
    clearContainers(wrapper);

    const clone = cloneCaseTemplate();
    const editBtn = clone.querySelector("#case__edit-btn");
    const deleteBtn = clone.querySelector("#case__delete-btn");

    fillCaseTemplate(case1, clone);
    editBtn.addEventListener("click", () => openCaseEditor(case1, wrapper, stepId));
    deleteBtn.addEventListener("click", () => deleteCase(case1.id, stepId, wrapper));

    wrapper.appendChild(clone);
}

function fillCaseTemplate(case1, clone) {
    clone.querySelector("#case__text").textContent = case1.text;
    clone.querySelector("#case__percent").textContent = case1.counting ? case1.percent : "";
}

function addStepHeader(stepId) {
    clearContainers(DOM.stepHeader);

    const headerClone = cloneStepHeaderTemplate();

    headerClone
        .querySelector("#create-case-btn")
        .addEventListener("click", () => openCreateCaseModalWindow(stepId));
    headerClone
        .querySelector("#delete-current-step-btn")
        .addEventListener("click", () => deleteStep(stepId));
    calculateDayProductivity(
        headerClone.querySelector("#statistics-block"),
        stepId
    );

    DOM.stepHeader.appendChild(headerClone);
}

async function calculateDayProductivity(productivityDisplay, stepId) {
    const response = await getStepById(stepId);
    const goal = Number(await getTargetPercentageByStepId(stepId)) | 0;
    const step = Step.stepFromJSON(response);
    const avgPercent = getAveragePercent(step.cases) | 0;
    const different = avgPercent - goal;

    productivityDisplay.querySelector("#metric__avg-value").textContent = avgPercent;
    productivityDisplay.querySelector("#metric__goal-value").textContent = goal;
    productivityDisplay.querySelector("#metric__different-value").textContent = different;

    paintStatisticsDisplay(different);
}

function paintStatisticsDisplay(different) {
    if (different > 0) {
        setColor("title_green","metric__value_green");
    } else if (different < 0) {
        setColor("title_red","metric__value_red");
    } else {
        setColor("title_yellow","metric__value_yellow");
    }

    function setColor(titleClass, metricTotalClass) {
        const block = DOM.stepHeader.querySelector("#statistics-block");

        block.querySelector(".title").classList.add(titleClass);
        block.querySelector(".metric-total .metric__value").classList.add(metricTotalClass);
    }
}

function getAveragePercent(cases) {
    let counter = 0;
    let sum = 0;

    for (const caseItem of cases) {
        if (caseItem.counting) {
            sum += caseItem.percent;
            counter++;
        }
    }
    const avg = sum / counter;
    return Number(avg.toFixed(2));
}

async function deleteCase(caseId, stepId, wrapper) {
    try {
        await fetchToDeleteCaseById(caseId);

        removeCaseFromCache(caseId, stepId);
        loadCasesByStepId(stepId);
    } catch (error) {
        console.log("Cached error: " + error);
        showError(
            error,
            wrapper.querySelector(".case__error"),
            wrapper.querySelector("#case__error-message")
        );
    }
}

function removeCaseFromCache(case1, stepId) {
    for (const flow of flowsCache) {
        for (const step of flow.steps) {
            if (step.id === stepId) {
                const caseIndex = step.cases.findIndex(c => c.id === case1);

                if (caseIndex !== -1) {
                    step.cases.splice(caseIndex, 1);
                    return;
                }
            }
        }
    }
    console.warn(`Such case not found in cache`);
}


// editing case
function openCaseEditor(case1, wrapper, stepId) {
    clearContainers(wrapper);

    const clone = cloneCaseEditTemplate();

    const editForm = {
        text: clone.querySelector("#case-edit__text"),
        percent: clone.querySelector("#case-edit__percent"),
        counting: clone.querySelector("#case-edit__counting"),
        saveBtn: clone.querySelector("#case-edit__save-btn"),
        cancelBtn: clone.querySelector("#case-edit__cancel-btn"),
    }

    editForm.text.value = case1.text;
    editForm.percent.value = case1.counting ? case1.percent : "";
    editForm.percent.disabled = !case1.counting;
    editForm.counting.checked = case1.counting;

    editForm.text.addEventListener("input", () => adjustTextarea(editForm.text));
    editForm.counting.addEventListener("change", (event) => handleInputAvailableByCheckbox(event,  editForm.percent));
    editForm.cancelBtn.addEventListener("click", () => putCaseInWrapper(case1, wrapper, stepId));
    editForm.saveBtn.addEventListener("click", (event) => editCase(event, case1, wrapper, stepId));

    wrapper.appendChild(clone);
    adjustTextarea(editForm.text);
}

async function editCase(event, case1, wrapper, stepId) {
    event.preventDefault();

    const edited = new Case(
        case1.id,
        case1.createdAt,
        case1.updatedAt,
        wrapper.querySelector("#case-edit__text").value,
        wrapper.querySelector("#case-edit__percent").value,
        wrapper.querySelector("#case-edit__counting").checked
    );

    try {
        validateCase(edited);

        const response = await fetchToEditCase(edited);
        Object.assign(case1, Case.caseFromJSON(response));
        updateCaseInCache(case1);

        putCaseInWrapper(case1, wrapper, stepId);
        addStepHeader(stepId);
    } catch (error) {
        console.log("Error", error);
        showError(
            error,
            wrapper.querySelector(".case-edit__error"),
            wrapper.querySelector("#case-edit__error-message")
        );
    }
}

function updateCaseInCache(updatedCase) {
    for (const flow of flowsCache) {
        for (const step of flow.steps) {
            const caseIndex = step.cases.findIndex(c => c.id === updatedCase.id);
            if (caseIndex !== -1) {
                step.cases[caseIndex] = updatedCase;
                return;
            }
        }
    }
    console.warn(`Case with id ${updatedCase.id} not found in flowsCache`);
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

    const flowId = Number(DOM.modal.createStep.inputFlowId.value);
    const step = Step.simpleStep(
        DOM.modal.createStep.inputDate.value
    );

    try {
        let responseStep = await createStepForFlowById(step, flowId);
        responseStep = Step.stepFromJSON(responseStep);

        insertNewStepInCache(responseStep, flowId);
        loadStepsByFlowId(flowId);

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

function insertNewStepInCache(step, flowId) {
    for (const flow of flowsCache) {
        if (flow.id === flowId) {
            flow.steps.push(step);
            return;
        }
    }
}

function loadStepsByFlowId(flowId) {
    const flow = flowsCache.find(flow => flow.id === flowId);
    loadSteps(flow.steps ,flowId);
}

function clearStepModalWindow() {
    DOM.modal.createStep.inputFlowId.value = "";
    DOM.modal.createStep.inputDate.value = "";
}


// modal - create case
function openCreateCaseModalWindow(stepId) {
    DOM.modal.createCase.errorBlock.style.display = "none";
    DOM.modal.createCase.inputStepId.value = stepId;
    DOM.modal.createCase.inputPercent.disabled = true;

    openModalWindow(DOM.modal.createCase.window);
}

async function createNewCase(event) {
    event.preventDefault();
    DOM.modal.createCase.submitButton.disabled = true;

    const stepId = Number(DOM.modal.createCase.inputStepId.value);
    const newCase = Case.simpleCase(
        DOM.modal.createCase.inputDescription.value,
        DOM.modal.createCase.inputPercent.value,
        DOM.modal.createCase.inputCounting.checked
    );

    try {
        validateCase(newCase);
        let responseCase = await createCaseForStepById(newCase, stepId);
        responseCase = Case.caseFromJSON(responseCase);

        insertNewCaseInCache(responseCase, stepId);
        loadCasesByStepId(stepId);

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

function insertNewCaseInCache(case1, stepId) {
    // it's creepy
    for (const flow of flowsCache) {
        for (const step of flow.steps) {
            if (step.id === stepId) {
                step.cases.push(case1);
                return;
            }
        }
    }
}

function loadCasesByStepId(stepId) {
    const step = flowsCache
        .flatMap(flow => flow.steps)
        .find(step => step.id === stepId);

    loadCases(step.cases, stepId);
}

function clearCaseModalWindow() {
    DOM.modal.createCase.inputStepId.value = "";
    DOM.modal.createCase.inputDescription.value = "";
    DOM.modal.createCase.inputPercent.value = "";
    DOM.modal.createCase.inputCounting.checked = false;
}
