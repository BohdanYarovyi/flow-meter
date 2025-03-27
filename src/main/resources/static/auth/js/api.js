const GET_CURRENT_ACCOUNT_ID_URL = `/api/accounts/current`;
const GET_ACCOUNT_BY_ID_URL = (id) => `/api/accounts/${id}`;
const CREATE_FLOW_FOR_ACCOUNT_BY_ID = (id) => `/api/accounts/${id}/flows`;

const GET_ALL_FLOWS_URL = `/api/flows`;
const GET_FLOW_BY_ID_URL = (id) => `/api/flows/${id}`;
const GET_FLOWS_BY_ACCOUNT_ID_URL = (id) => `/api/accounts/${id}/flows`;


export async function fetchCurrentAccountId() {
    try {
        const response = await fetch(GET_CURRENT_ACCOUNT_ID_URL, {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        });

        if (response.status === 401) {
            throw new Error("Unauthorized");
        } else if (!response.ok) {
            throw new Error("Unexpected error");
        }

        const body = await response.json();
        return body.currentAccountId;
    } catch (error) {
        console.log("Error: ", error.message)
        throw error;
    }
}

export async function fetchAccountById(id){
    try {
        const response = await fetch(GET_ACCOUNT_BY_ID_URL(id), {
            method: "GET",
            headers: {"Content-Type": "application/json"},
        });

        if (response.status === 401) {
            throw new Error("Unauthorized");
        } else if (!response.ok) {
            throw new Error("Unexpected error");
        }

        return await response.json();
    } catch (error) {
        console.log("Error: ", error.message);
        throw error;
    }
}

export async function fetchFlowsByAccountId(accountId) {
    try {
        const response = await fetch(GET_FLOWS_BY_ACCOUNT_ID_URL(accountId), {
            method: "GET",
            headers: {"Content-Type": "application/json"}
        });

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to fetchFlowsByAccountId with id:${accountId}`);
        }

        return await response.json();
    } catch (error) {
        console.log("Error: ", error.message);
        throw error;
    }
}

export async function createFlowForAccountById(flow, accountId) {
    try {
        const response = await fetch(CREATE_FLOW_FOR_ACCOUNT_BY_ID(accountId), {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(flow)
        });

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to create flow for account ${accountId}`);
        }

        return response.headers.get("location");
    } catch (error) {
        console.log("Error: ", error);
        throw error;
    }
}

