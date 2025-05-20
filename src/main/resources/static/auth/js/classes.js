export class Account {
    constructor(id, createdAt, updatedAt, roles, credentials, personalInfo) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roles = roles;
        this.credentials = credentials;
        this.personalInfo = personalInfo;
    }

    static accountFromJson(json) {
        const roles = json.roles.map(Role.roleFromJson);

        return new Account(
            json.id,
            json.createdAt,
            json.updatedAt,
            roles,
            Credentials.credentialsFromJson(json),
            PersonalInfo.personalInfoFromJson(json)
        );
    }
}

export class Credentials {
    constructor(login, email, password) {
        this.login = login;
        this.email = email;
        this.password = password;
    }

    static credentialsFromJson(json) {
        return new Credentials(
            json.login,
            json.email,
            json.password || ""
        );
    }
}

export class PersonalInfo {
    constructor(firstname, lastname, patronymic, dateOfBirth, phone) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.patronymic = patronymic;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
    }

    static personalInfoFromJson(json) {
        return new PersonalInfo(
            json.firstname,
            json.lastname,
            json.patronymic,
            json.dateOfBirth,
            json.phone
        );
    }
}

export class Role {
    constructor(id, name) {
        this.id = id;
        this.name = name;
    }

    static roleFromJson(json) {
        return new Role(
            json.id,
            json.name
        );
    }
}

export class Flow {
    constructor(id, createdAt, updatedAt, title, description, targetPercentage, steps) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.title = title;
        this.description = description;
        this.targetPercentage = targetPercentage;
        this.steps = steps;
    }

    static simpleFlow(title, description, targetPercentage) {
        return new Flow(
            null,
            null,
            null,
            title,
            description,
            Number(targetPercentage),
            null
        );
    }

    static flowFromJSON(jsonObject) {
        const steps = jsonObject.steps.map(Step.stepFromJSON);

        return new Flow(
            jsonObject.id,
            jsonObject.createdAt,
            jsonObject.updatedAt,
            jsonObject.title,
            jsonObject.description,
            jsonObject.targetPercentage,
            steps
        );
    }
}

export class Step {
    constructor(id, createdAt, updatedAt, day, cases) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt
        this.day = new Date(day);
        this.cases = cases;
    }

    getFormatDate() {
        let day = this.day.getDate();
        let month = this.day.getMonth() + 1;
        let year = this.day.getFullYear() + "";

        day = day > 9 ? day : `0${day}`;
        month = month > 9 ? month : `0${month}`;
        year = year.substring(2, 4);

        return `${day}.${month}.${year}`;
    }

    static stepFromJSON(jsonObject) {
        const cases = jsonObject.cases.map(Case.caseFromJSON);

        return new Step(
            jsonObject.id,
            jsonObject.createdAt,
            jsonObject.updatedAt,
            jsonObject.day,
            cases
        );
    }

    static simpleStep(day) {
        return new Step(
            null,
            null,
            null,
            day,
            null
        );
    }
}

export class Case {
    constructor(id, createdAt, updatedAt, text, percent, counting) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.text = text;
        this.percent = percent;
        this.counting = counting;
    }

    static caseFromJSON(jsonObject) {
        return new Case(
            jsonObject.id,
            jsonObject.createdAt,
            jsonObject.updatedAt,
            jsonObject.text,
            jsonObject.percent,
            jsonObject.counting
        );
    }

    static simpleCase(text, percent, counting) {
        return new Case(
            null,
            null,
            null,
            text,
            Number(percent),
            counting
        );
    }
}

export class StatisticsData {
    constructor(flowTitle, year, month, labels, values) {
        this.flowTitle = flowTitle;
        this.year = year;
        this.month = month;
        this.labels = labels;
        this.values = values;
    }

    static statisticDataFromJSON(json) {
        const labels = json.points.map(point => point.monthDay);
        const values = json.points.map(point => point.avgPercentage);

        return new StatisticsData(
            json.flowTitle,
            json.year,
            json.month,
            labels,
            values,
        );
    }

}