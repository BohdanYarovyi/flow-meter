const API = {
    account: {
        GET_CURRENT_ACCOUNT_ID__URL:            ()   => `/api/accounts/current`,
        GET_ACCOUNT_BY_ID__URL:                 (id) => `/api/accounts/${id}`,
        GET_FLOWS_BY_ACCOUNT_ID__URL:           (id) => `/api/accounts/${id}/flows`,
        CREATE_FLOW_FOR_ACCOUNT_BY_ID__URL:     (id) => `/api/accounts/${id}/flows`,
        EDIT_PERSONAL_INFO_BY_ID__URL:          (id) => `/api/accounts/${id}/edit/personal-info`,
        EDIT_CREDENTIALS_BY_ID__URL:            (id) => `/api/accounts/${id}/edit/credentials`,
    },
    flow: {
        GET_ALL_FLOWS__URL:                     ()   => `/api/flows`,
        GET_FLOW_BY_ID__URL:                    (id) => `/api/flows/${id}`,
        EDIT_FLOW__URL:                         ()   => `/api/flows`,
        DELETE_FLOW_BY_ID__URL:                 (id) => `/api/flows/${id}`,
        CREATE_STEP_FOR_FLOW_BY_ID__URL:        (flowId) => `/api/flows/${flowId}/steps`,
    },
    step: {
        CREATE_CASE_FOR_STEP_BY_ID__URL:        (stepId) => `/api/steps/${stepId}/cases`,
        DELETE_STEP_BY_ID__URL:                 (id) => `/api/steps/${id}`,
    },
    case1: {
        EDIT_CASE__URL:                         ()   => `/api/cases`,
        DELETE_CASE_BY_ID__URL:                 (caseId) => `/api/cases/${caseId}`,
    }
};
const defaultErrorPrefix = "Error on API layer: ";

export async function fetchCurrentAccountId() {
    const fetchParams = {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    };

    try {
        const response = await fetch(API.account.GET_CURRENT_ACCOUNT_ID__URL(), fetchParams);

        if (response.status === 401) {
            throw new Error("Unauthorized");
        } else if (!response.ok) {
            throw new Error("Unexpected error");
        }

        const body = await response.json();
        return body.currentAccountId;
    } catch (error) {
        console.log(defaultErrorPrefix, error.message)
        throw error;
    }
}


export async function fetchAccountById(id) {
    const fetchParams = {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    };

    try {
        const response = await fetch(API.account.GET_ACCOUNT_BY_ID__URL(id), fetchParams);

        if (response.status === 401) {
            throw new Error("Unauthorized");
        } else if (!response.ok) {
            throw new Error("Unexpected error");
        }

        return await response.json();
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function fetchFlowsByAccountId(accountId) {
    const fetchParams = {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        }
    };

    try {
        const response = await fetch(API.account.GET_FLOWS_BY_ACCOUNT_ID__URL(accountId), fetchParams);

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to fetchFlowsByAccountId with id:${accountId}`);
        }

        return await response.json();
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function createFlowForAccountById(flow, accountId) {
    const fetchParams = {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(flow)
    };

    try {
        const response = await fetch(API.account.CREATE_FLOW_FOR_ACCOUNT_BY_ID__URL(accountId), fetchParams);

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to create flow for account`);
        }

        return response.json();
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function fetchToUpdatePersonalInfo(accountId, personalInfo) {
    const fetchParams = {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(personalInfo)
    };

    try {
        const response = await fetch(API.account.EDIT_PERSONAL_INFO_BY_ID__URL(accountId), fetchParams);

        if (!response.ok) {
            const responseError = await response.json();
            throw new Error(responseError.detail || `Failed to update personal info`)
        }
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}

export async function fetchToUpdateCredentials(accountId, credentials) {
    const fetchParams = {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(credentials)
    };

    try {
        const response = await fetch(API.account.EDIT_CREDENTIALS_BY_ID__URL(accountId), fetchParams);

        if (!response.ok) {
            const responseError = await response.json();
            throw new Error(responseError.detail || `Failed to change credentials`);
        }
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function createStepForFlowById(step, flowId) {
    const fetchParams = {
        method: "POST",
        headers: {
            "Content-Type" : "application/json"
        },
        body: JSON.stringify(step)
    };

    try {
        const response = await fetch(API.flow.CREATE_STEP_FOR_FLOW_BY_ID__URL(flowId), fetchParams);

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to create step for flow`);
        }

        return response.json();
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function createCaseForStepById(case1, stepId) {
    const fetchParams = {
        method: "POST",
        headers: {
            "Content-Type" : "application/json"
        },
        body: JSON.stringify(case1)
    };

    try {
        const response = await fetch(API.step.CREATE_CASE_FOR_STEP_BY_ID__URL(stepId), fetchParams);

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to create case for step`);
        }

        return response.json();
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function fetchToEditFlow(flow) {
    const fetchParams = {
        method: "PUT",
        headers: {
            "Content-Type" : "application/json"
        },
        body: JSON.stringify(flow)
    };

    try {
        const response = await fetch(API.flow.EDIT_FLOW__URL(), fetchParams);

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to edit flow`);
        }

        return response.json();
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function fetchToEditCase(case1) {
    const fetchParams = {
        method: "PUT",
        headers: {
            "Content-Type" : "application/json"
        },
        body: JSON.stringify(case1)
    };

    try {
        const response = await fetch(API.case1.EDIT_CASE__URL(), fetchParams);

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to edit case`);
        }

        return response.json();
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function fetchToDeleteFlowById(flowId) {
    const fetchParams = {
        method: "DELETE",
        headers: {
            "Content-Type" : "application/json"
        }
    };

    try {
        const response = await fetch(API.flow.DELETE_FLOW_BY_ID__URL(flowId), fetchParams);

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to delete flow`);
        }

        return true;
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function fetchToDeleteStepById(stepId) {
    const fetchParams = {
        method: "DELETE",
        headers: {
            "Content-Type" : "application/json"
        }
    };

    try {
        const response = await fetch(API.step.DELETE_STEP_BY_ID__URL(stepId), fetchParams);

        if (!response.ok) {
            const errorResponse = await response.json();
            throw new Error(errorResponse.detail || `Failed to delete step`);
        }

        return true;
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}


export async function fetchToDeleteCaseById(caseId) {
    const fetchParams = {
        method: "DELETE",
        headers: {
            "Content-Type" : "application/json"
        }
    };

    try {
        const response = await fetch(API.case1.DELETE_CASE_BY_ID__URL(caseId), fetchParams);

        if (!response.ok) {
            const responseError = await response.json();
            throw new Error(responseError.detail || `Failed to delete case`);
        }

        return true;
    } catch (error) {
        console.log(defaultErrorPrefix, error.message);
        throw error;
    }
}




