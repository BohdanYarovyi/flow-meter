import {fetchCurrentAccountId, fetchAccountById} from './api.js';
import {cloneProfileTemplate, cloneRoleTemplate} from "../../template-loader.js";

const profileHolder = document.getElementById("profile-holder");

window.onload = loadProfile;

async function loadProfile() {
    try {
        const id = await fetchCurrentAccountId();
        const account = await fetchAccountById(id);
        const clone = cloneProfileTemplate();

        function setValueForSpan(selector, value) {
            const span = clone.querySelector(selector);

            if (!span) {
                throw Error(`No found ${selector}`);
            }

            span.textContent = value || 'N/A';
        }

        clone.getElementById("account-login").textContent = account.login;
        setValueForSpan("#fullname-field", `${account.firstname} ${account.lastname} ${account.patronymic}`);
        setValueForSpan("#date-of-birth-field", account.dateOfBirth);
        setValueForSpan("#email-field", account.email);
        setValueForSpan("#phone-field", account.phone);
        setValueForSpan("#created-at-field", account.createdAt);
        setValueForSpan("#updated-at-field", account.updatedAt);

        const roleHolder = clone.getElementById("role-holder");
        const roles = account.roles;
        for (let role of roles) {
            const roleNode = cloneRoleTemplate();
            const span = roleNode.querySelector("#role-field");
            span.textContent = role.name || 'N/A';

            roleHolder.appendChild(roleNode);
        }
        profileHolder.appendChild(clone);
    } catch (error) {
        console.log(error);
    }
}
