import {
    fetchAccountById,
    fetchCurrentAccountId, fetchToUpdateCredentials, fetchToUpdatePersonalInfo
} from "./api.js";
import {
    cloneEditCredentialsTemplate,
    cloneEditPersonalInfoTemplate,
    cloneProfileButtonTemplate,
    cloneProfileItemTemplate,
    cloneProfileTitleTemplate
} from "../../template-loader.js";
import {Account, Credentials, PersonalInfo} from "./classes.js";
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
    const info = account.personalInfo;
    const container = DOM.personalInfoContainer;

    clearContainers(container);

    addItemInContainer("Firstname", info.firstname, container);
    addItemInContainer("lastname", info.lastname, container);
    addItemInContainer("Patronymic", info.patronymic, container);
    addItemInContainer("Date of birth", info.dateOfBirth, container);
    addItemInContainer("phone", info.phone, container);

    addButtonInContainer(
        "Edit personal info",
        container,
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
            container.querySelector(".edit__error"),
            container.querySelector(".edit__error p")
        );
    }
}

function loadCredentials() {
    const credentials = account.credentials;
    const container = DOM.credentialsContainer;

    clearContainers(container);

    addItemInContainer("Login", credentials.login, container);
    addItemInContainer("Email", credentials.email, container);

    addButtonInContainer(
        "Edit credentials",
        container,
        () => openCredentialsEditor(credentials, container)
    );
}

function openCredentialsEditor(credentials, container) {
    clearContainers(container);

    const clone = cloneEditCredentialsTemplate();
    const editor = {
        login: clone.querySelector("#credentials__login"),
        email: clone.querySelector("#credentials__email"),
        saveBtn: clone.querySelector("#credentials__save-button"),
        cancelBtn: clone.querySelector("#credentials__cancel-button")
    };

    editor.login.value = credentials.login;
    editor.email.value = credentials.email;

    editor.saveBtn.addEventListener("click", (event) =>
        saveCredentials(event, editor, container)
    );
    editor.cancelBtn.addEventListener("click", () =>
        loadCredentials()
    );

    container.appendChild(clone);
}

async function saveCredentials(event, editor, container) {
    event.preventDefault();

    const credentials = new Credentials(
        editor.login.value,
        editor.email.value,
        null
    );

    try {
        // todo: validate here
        await fetchToUpdateCredentials(accountId, credentials);
        account.credentials = credentials;

        loadCredentials();
    } catch (error) {
        console.log(error);
        showError(
            error,
            container.querySelector(".edit__error"),
            container.querySelector(".edit__error p"),
        );
    }
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
