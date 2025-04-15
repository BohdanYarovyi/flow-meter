import {
    fetchAccountById,
    fetchCurrentAccountId, fetchToUpdatePersonalInfo
} from "./api.js";
import {
    cloneEditPersonalInfoTemplate,
    cloneProfileButtonTemplate,
    cloneProfileItemTemplate,
    cloneProfileTitleTemplate
} from "../../template-loader.js";
import {Account, PersonalInfo} from "./classes.js";
import {clearContainers, showError} from "../../util.js";

const DOM = {
    titleContainer: document.querySelector("#profile__title"),
    personalInfoContainer: document.querySelector("#profile__personal-info"),
    credentialsContainer: document.querySelector("#profile__credentials"),
};

let accountId = null;
let account = null;

window.onload = loadProfile;

// todo: add edit menu for credentials
// todo: add available for password changing

async function loadProfile() {
    accountId = await fetchCurrentAccountId();
    const json = await fetchAccountById(accountId);
    account = Account.accountFromJson(json);

    loadPage();
}

function loadPage() {
    loadPageTitle();
    loadPersonalInfo()
    loadCredentials();
}

function loadPageTitle() {
    clearContainers(DOM.titleContainer);

    const clone = cloneProfileTitleTemplate();

    const titleValue = clone.querySelector("#profile__title-value");
    titleValue.textContent = account.credentials.login;

    DOM.titleContainer.appendChild(clone);
}

function loadPersonalInfo() {
    clearContainers(DOM.personalInfoContainer);

    const info = account.personalInfo;
    const container = DOM.personalInfoContainer;

    addItemInContainer("Firstname", info.firstname, container);
    addItemInContainer("lastname", info.lastname, container);
    addItemInContainer("Patronymic", info.patronymic, container);
    addItemInContainer("Date of birth", info.dateOfBirth, container);
    addItemInContainer("phone", info.phone, container);

    addButtonInContainer(
        "Edit personal info",
        DOM.personalInfoContainer,
        () => openPersonalInfoEditor(info, container)
    );
}

function openPersonalInfoEditor(personalInfo, container) {
    clearContainers(container);

    const clone = cloneEditPersonalInfoTemplate();
    const editor = {
        firstname: clone.querySelector("#personal-info__firstname"),
        lastname: clone.querySelector("#personal-info__lastname"),
        patronymic: clone.querySelector("#personal-info__patronymic"),
        dateOfBirth: clone.querySelector("#personal-info__dateOfBirth"),
        phone: clone.querySelector("#personal-info__phone"),
        saveBtn: clone.querySelector("#personal-info__save-button"),
        cancelBtn: clone.querySelector("#personal-info__cancel-button")
    };

    editor.firstname.value = personalInfo.firstname;
    editor.lastname.value = personalInfo.lastname;
    editor.patronymic.value = personalInfo.patronymic;
    editor.dateOfBirth.value = personalInfo.dateOfBirth;
    editor.phone.value = personalInfo.phone;

    editor.cancelBtn.addEventListener("click", () => loadPersonalInfo());
    editor.saveBtn.addEventListener('click', (event) => savePersonalInfo(event, editor, container));

    container.appendChild(clone);
}

async function savePersonalInfo(event, editor, container) {
    event.preventDefault();

    const info = new PersonalInfo(
        editor.firstname.value,
        editor.lastname.value,
        editor.patronymic.value,
        editor.dateOfBirth.value,
        editor.phone.value
    );

    try {
        // todo: validating personal info here;
        await fetchToUpdatePersonalInfo(account.id, info);
        account.personalInfo = info;

        loadPersonalInfo();
    } catch (error) {
        console.log(error);
        showError(
            error,
            container.querySelector(".personal-info-edit__error"),
            container.querySelector("#personal-info-edit__error-message")
        );
    }
}

function loadCredentials() {
    clearContainers(DOM.credentialsContainer);

    const credentials = account.credentials;
    const container = DOM.credentialsContainer;

    addItemInContainer("Login", credentials.login, container);
    addItemInContainer("Email", credentials.email, container);

    addButtonInContainer(
        "Edit credentials",
        DOM.credentialsContainer,
        () => {console.log("edit credentials btn was clicked");}
    );
}

function addButtonInContainer(btnTitle, container, onClick) {
    const clone = cloneProfileButtonTemplate();

    const btn = clone.querySelector("#profile__btn");
    btn.textContent = btnTitle;
    btn.addEventListener("click", () => onClick());

    container.appendChild(clone);
}

function addItemInContainer(labelText, valueText, container) {
    const clone = cloneProfileItemTemplate();

    const label = clone.querySelector("#item__label");
    const value = clone.querySelector("#item__value");

    label.textContent = labelText + ": ";
    value.textContent = valueText;

    container.appendChild(clone);
}
