
export function loadTemplateById(templateId) {
    const template = document.getElementById(templateId);
    if (!template) {
        throw new Error(`Profile template with ID: ${templateId} not found in DOM`);
    }

    return template.content.cloneNode(true);
}

export function cloneProfileTitleTemplate() {
    return loadTemplateById("profile-title-template");
}

export function cloneProfileItemTemplate() {
    return loadTemplateById("profile-item-template");
}

export function cloneProfileButtonTemplate() {
    return loadTemplateById("profile-btn-template");
}

export function cloneFlowItemTemplate() {
    return loadTemplateById("flow-item-template")
}

export function cloneFlowNotFoundLabelTemplate() {
    return loadTemplateById("flow-not-found-label-template");
}

export function cloneStepItemTemplate() {
    return loadTemplateById("step-item-template");
}

export function cloneCaseWrapperTemplate() {
    return loadTemplateById("case-wrapper-template");
}

export function cloneCaseTemplate() {
    return loadTemplateById("case-template");
}

export function cloneCaseEditTemplate() {
    return loadTemplateById("case-edit-template");
}

export function cloneCreateStepBtnTemplate() {
    return loadTemplateById("create-step-btn-template");
}

export function cloneCreateCaseBtnTemplate() {
    return loadTemplateById("create-case-btn-template");
}

export function cloneDeleteStepBtnTemplate() {
    return loadTemplateById("delete-step-btn-template");
}

export function cloneFlowDetailsTemplate() {
    return loadTemplateById("flow-details-template");
}

export function cloneFlowDetailsEditTemplate() {
    return loadTemplateById("flow-details-edit-template");
}


