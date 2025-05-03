const API = {
    LOGIN:          `/api/public/login`,
    REGISTER:       `/api/public/registration`,
}

export async function fetchToLogin(credentials) {
    const fetchParams = {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(credentials)
    }

    try {
        const response = await fetch(API.LOGIN, fetchParams);

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.detail || "Login failed");
        }

        return await response.json();
    } catch (error) {
        console.log(error);
        throw error;
    }
}

export async function fetchToRegister(registerData) {
    const fetchParams = {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(registerData)
    }

    try {
        const response = await fetch(API.REGISTER, fetchParams);

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.detail || "Registration failed");
        }
    } catch (error) {
        console.log(error);
        throw error;
    }
}
