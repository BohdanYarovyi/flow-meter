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
            targetPercentage,
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
            percent,
            counting
        );
    }
}