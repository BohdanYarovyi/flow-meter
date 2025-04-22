import {
    fetchAccountById,
    fetchCurrentAccountId,
    fetchToChangePassword,
    fetchToUpdateCredentials,
    fetchToUpdatePersonalInfo
} from "./api.js";
import {
    cloneEditCredentialsTemplate, cloneEditPasswordTemplate,
    cloneEditPersonalInfoTemplate,
    cloneProfileButtonContainerTemplate,
    cloneProfileButtonTemplate,
    cloneProfileItemTemplate,
    cloneProfileTitleTemplate
} from "../../pub/js/template-loader.js";
import {Account, Credentials, PersonalInfo} from "./classes.js";
import {clearContainers, showError} from "../../pub/js/util.js";
import {
    validateEmail,
    validateField,
    validatePassword,
    validatePasswordMatches,
    validateUsername
} from "../../pub/js/validation.js";

const DOM = {
    titleContainer: document.querySelector("#profile__title"),
    personalInfoContainer: document.querySelector("#profile__personal-info"),
    credentialsContainer: document.querySelector("#profile__credentials"),
};

let accountId = null;
let account = null;

window.onload = loadProfile;

async function loadProfile() {
    accountId = await fetchCurrentAccountId();
    const json = await fetchAccountById(accountId);
    account = Account.accountFromJson(json);

    loadPage();
}

function loadPage() {
    loadPageTitle();
    loadPersonalInfo();
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

    const editPersonalInfoBtn = createBtn(
        "Edit personal info",
        () => openPersonalInfoEditor(info, container)
    );
    addButtonsInHolder(container, editPersonalInfoBtn);
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

    editor.cancelBtn.addEventListener("click", (event) => {
        event.preventDefault();
        loadPersonalInfo();
    });
    editor.saveBtn.addEventListener('click', (event) =>
        savePersonalInfo(event, editor, container)
    );

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
        validatePersonalInfoFields(info);
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

function validatePersonalInfoFields(info) {
    validateField(info.firstname, 100, "Firstname");
    validateField(info.lastname, 100, "Lastname");
    validateField(info.patronymic, 100, "Patronymic");
    validateField(info.phone, 100, "Phone");
}

function loadCredentials() {
    const credentials = account.credentials;
    const container = DOM.credentialsContainer;

    clearContainers(container);

    addItemInContainer("Login", credentials.login, container);
    addItemInContainer("Email", credentials.email, container);

    const editCredentialsBtn = createBtn(
        "Edit credentials",
        () => openCredentialsEditor(credentials, container)
    );
    const changePasswordBtn = createBtn(
        "Change password",
        () => openPasswordEditor(credentials, container)
    );

    addButtonsInHolder(container, editCredentialsBtn, changePasswordBtn);
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
    editor.cancelBtn.addEventListener("click", (event) => {
        event.preventDefault();
        loadCredentials();
    });

    container.appendChild(clone);
}

function openPasswordEditor(credentials, container) {
    clearContainers(container);

    const clone = cloneEditPasswordTemplate();
    const editor = {
        passwordCurrent: clone.querySelector("#password__current"),
        passwordNew: clone.querySelector("#password__new"),
        passwordNewRepeat: clone.querySelector("#password__new-repeat"),
        btnSave: clone.querySelector("#password__save-button"),
        btnCancel: clone.querySelector("#password__cancel-button"),
    };

    editor.btnCancel.addEventListener("click", (event) => {
        event.preventDefault();
        loadCredentials();
    });
    editor.btnSave.addEventListener("click", (event) =>
        changePassword(event, editor, container)
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
        validateCredentials(credentials);
        await fetchToUpdateCredentials(accountId, credentials);
        account.credentials = credentials;

        loadPageTitle();
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

function validateCredentials(credentials) {
    validateEmail(credentials.email);
    validateUsername(credentials.login);
}

async function changePassword(event, editor, container) {
    event.preventDefault();
    const currentPassword = editor.passwordCurrent.value;
    const newPassword1 = editor.passwordNew.value;
    const newPassword2 = editor.passwordNewRepeat.value;

    try {
        validateChangePasswordForm(currentPassword, newPassword1, newPassword2);
        await fetchToChangePassword(accountId, currentPassword, newPassword1);

        document.location.href = "/login";
    } catch (error) {
        console.log(error);
        showError(
            error,
            container.querySelector(".edit__error"),
            container.querySelector(".edit__error p"),
        );
    }
}

function validateChangePasswordForm(currentPassword, newPassword1, newPassword2) {
    validatePassword(currentPassword);
    validatePassword(newPassword1);
    validatePassword(newPassword2);
    validatePasswordMatches(newPassword1, newPassword2);
}

function addButtonsInHolder(container, ...buttons) {
    const clone = cloneProfileButtonContainerTemplate();

    const btnHolder = clone.querySelector(".profile__btn-holder");
    buttons.forEach(btn => btnHolder.appendChild(btn));

    container.appendChild(clone);
}

function createBtn(btnTitle, onClick) {
    const clone = cloneProfileButtonTemplate();

    const btn = clone.querySelector("#profile__btn");
    btn.textContent = btnTitle;
    btn.addEventListener("click", () => onClick());

    return clone;
}

function addItemInContainer(labelText, valueText, container) {
    const clone = cloneProfileItemTemplate();

    const label = clone.querySelector("#item__label");
    const value = clone.querySelector("#item__value");

    label.textContent = labelText + ": ";
    value.textContent = valueText;

    container.appendChild(clone);
}
