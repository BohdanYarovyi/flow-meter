export function validateFlow(flow) {
    if (!flow.title || flow.title.trim().length < 3) {
        throw new Error(`Title length must be at least 3 symbols`);
    }

    if (!flow.targetPercentage || flow.targetPercentage > 100 || flow.targetPercentage < 0) {
        throw new Error(`Target percentage must be between 0 and 100`);
    }
}

export function validateCase(case1) {
    if (!case1.text || case1.text.length <= 3) {
        throw new Error("Case description length must be greater than 3 symbols");
    }

    if (case1.percent > 100 || case1.percent < 0) {
        throw new Error("Case percent must be between 0 and 100")
    }

}

export function validateUsername(username) {
    if (!username || username.length < 3) {
        throw new Error("Username length must be at least 3 symbols ");
    }
}

export function validatePassword(password) {
    if (!password || password.length < 8) {
        throw new Error("Password length must be at least 8 symbols ");
    }
}

export function validateEmail(email) {
    const emailRegex = /^[A-Za-z0-9.]{2,30}@(gmail|googlemail)\.com$/i;

    if (!email || !emailRegex.test(email)) {
        throw new Error("Enter valid email like: my.email20@gmail.com")
    }
}

export function validatePasswordMatches(password1, password2) {
    if (password1 !== password2) {
        throw new Error("Passwords don't matches");
    }
}

export function validateField(value, maxCapacity, nameOfField) {
    if (value.length > maxCapacity) {
        throw new Error(`${nameOfField} length must be less than ${maxCapacity}`);
    }
}