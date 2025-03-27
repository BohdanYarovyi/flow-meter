export function validateCreatedFlow(flow) {
    if (!flow.title || flow.title.trim().length < 3) {
        throw new Error(`Title length must be at least 3 symbols`);
    }

    if (!flow.targetPercentage || flow.targetPercentage > 100 || flow.targetPercentage < 0) {
        throw new Error(`Target percentage must be between 0 and 100`);
    }
}