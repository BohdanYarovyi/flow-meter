
export function loadTemplateById(templateId) {
    const template = document.getElementById(templateId);
    if (!template) {
        throw new Error(`Profile template with ID: ${templateId} not found in DOM`);
    }

    return template.content.cloneNode(true);
}

export function cloneProfileTemplate() {
    return loadTemplateById("profile-template");
}

export function cloneRoleTemplate() {
    return loadTemplateById("role-template");
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

export function cloneCaseTemplate() {
    return loadTemplateById("case-template");
}

export function cloneCreateCaseBtnTemplate() {
    return loadTemplateById("create-case-btn-template");
}

export function cloneCreateStepBtnTemplate() {
    return loadTemplateById("create-step-btn-template");
}