export function validateFlow(flow) {
    if (!flow.title || flow.title.trim().length < 3) {
        throw new Error(`Title length must be at least 3 symbols`);
    }

    if (!flow.targetPercentage || flow.targetPercentage > 100 || flow.targetPercentage < 0) {
        throw new Error(`Target percentage must be between 0 and 100`);
    }
}

export function validateCreatedCase(case1) {
    if (!case1.text || case1.text.length <= 3) {
        throw new Error("Case description length must be greater than 3 symbols");
    }

    if (case1.percent > 100 || case1.percent < 0) {
        throw new Error("Case percent must be between 0 and 100")
    }

}