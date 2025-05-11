export function loadTemplateById(templateId) {
    const template = document.getElementById(templateId);
    if (!template) {
        throw new Error(`Profile template with ID: ${templateId} not found in DOM`);
    }

    return template.content.cloneNode(true);
}

export function cloneProfileTitleTemplate() {
    return loadTemplateById("profileTitle-template");
}

export function cloneProfileItemTemplate() {
    return loadTemplateById("profileItem-template");
}

export function cloneProfileButtonContainerTemplate() {
    return loadTemplateById("profileBtnContainer-template");
}

export function cloneProfileButtonTemplate() {
    return loadTemplateById("profileBtn-template");
}

export function cloneEditPersonalInfoTemplate() {
    return loadTemplateById("editPersonalInfo-template");
}

export function cloneEditCredentialsTemplate() {
    return loadTemplateById("editCredentials-template");
}

export function cloneEditPasswordTemplate() {
    return loadTemplateById("editPassword-template");
}

export function cloneFlowItemTemplate() {
    return loadTemplateById("flowItem-template")
}

export function cloneFlowNotFoundLabelTemplate() {
    return loadTemplateById("flowNotFoundLabel-template");
}

export function cloneTimelinePanelTemplate() {
    return loadTemplateById("timelinePanel-template");
}

export function cloneStepItemTemplate() {
    return loadTemplateById("stepItem-template");
}

export function cloneCaseWrapperTemplate() {
    return loadTemplateById("caseHolder-template");
}

export function cloneCaseTemplate() {
    return loadTemplateById("case-template");
}

export function cloneCaseEditTemplate() {
    return loadTemplateById("editCase-template");
}

export function cloneStepHeaderTemplate() {
    return loadTemplateById("stepHeader-template");
}

export function cloneFlowDetailsTemplate() {
    return loadTemplateById("flowDetails-template");
}

export function cloneFlowDetailsEditTemplate() {
    return loadTemplateById("editFlowDetails-template");
}


