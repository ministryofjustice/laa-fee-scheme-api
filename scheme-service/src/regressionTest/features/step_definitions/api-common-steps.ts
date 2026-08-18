import { Given, Then } from '@cucumber/cucumber';
import assert from 'node:assert/strict';
import World from '../support/world';

function coerceNumber(value: unknown): number | undefined {
    if (typeof value === 'number') return Number.isFinite(value) ? value : undefined;
    if (typeof value === 'string') {
        const cleaned = value.replace(/[,]/g, '').replace(/[^\d.+\-Ee]/g, '').trim();
        if (!cleaned) return undefined;
        const n = Number(cleaned);
        return Number.isFinite(n) ? n : undefined;
    }
    return undefined;
}

Given('I have an initialized API client', function (this: World) {
    assert.ok(this.client, 'Expected API client to be initialized.');
});

Then(
    'the JSON path {string} should equal number {float}',
    async function (this: World, jsonPath: string, expected: number) {
        const data = this.response?.data ?? {};
        const actual = this.getByPath(data, jsonPath);

        if (actual === undefined) {
            if (this.attach) {
                try {
                    await this.attach(
                        JSON.stringify(
                            {
                                message: `Value at path "${jsonPath}" is undefined.`,
                                availableKeysAtRoot: Object.keys(data ?? {}),
                                sampleOfResponse: data,
                            },
                            null,
                            2
                        ),
                        'application/json'
                    );
                } catch {}
            }
            throw new Error(`Value at path "${jsonPath}" is undefined in response.`);
        }

        const numeric = coerceNumber(actual);

        if (numeric === undefined) {
            if (this.attach) {
                try {
                    await this.attach(
                        JSON.stringify(
                            { message: 'Non-numeric value at JSON path.', jsonPath, actual, type: typeof actual },
                            null,
                            2
                        ),
                        'application/json'
                    );
                } catch {}
            }
            throw new Error(`Value at path "${jsonPath}" is not a finite number: ${JSON.stringify(actual)}`);
        }

        const tolerance = 0.01;
        assert.ok(
            Math.abs(numeric - expected) <= tolerance,
            `Expected ${numeric} to be within ${tolerance} of ${expected}`
        );
    }
);

Then(
    'the JSON path {string} should be boolean {word}',
    async function (this: World, jsonPath: string, expectedWord: string) {
        const expected = expectedWord.toLowerCase();
        if (expected !== 'true' && expected !== 'false') {
            throw new Error(`Expected boolean must be "true" or "false", got "${expectedWord}"`);
        }

        const data = this.response?.data ?? {};
        const actual = this.getByPath(data, jsonPath);

        if (actual === undefined) {
            if (this.attach) {
                try {
                    await this.attach(
                        JSON.stringify(
                            {
                                message: `Value at path "${jsonPath}" is undefined.`,
                                availableKeysAtRoot: Object.keys(data ?? {}),
                                sampleOfResponse: data,
                            },
                            null,
                            2
                        ),
                        'application/json'
                    );
                } catch {}
            }
            throw new Error(`Value at path "${jsonPath}" is undefined in response.`);
        }

        let normalized: boolean | undefined;

        if (typeof actual === 'boolean') {
            normalized = actual;
        } else if (typeof actual === 'string') {
            const s = actual.trim().toLowerCase();
            if (s === 'true' || s === '1') normalized = true;
            if (s === 'false' || s === '0') normalized = false;
        } else if (typeof actual === 'number') {
            if (actual === 1) normalized = true;
            if (actual === 0) normalized = false;
        }

        if (normalized === undefined) {
            throw new Error(`Value at path "${jsonPath}" is not coercible to boolean: ${JSON.stringify(actual)}`);
        }

        assert.equal(normalized, expected === 'true');
    }
);

Then(
    'the JSON path {string} should equal {string}',
    function (this: World, jsonPath: string, expected: string) {
        const data = this.response?.data ?? {};
        const actual = this.getByPath(data, jsonPath);

        if (actual === undefined) {
            throw new Error(`Value at path "${jsonPath}" is undefined in response.`);
        }

        assert.equal(String(actual), expected);
    }
);

Then('the response status should be {int}', function (this: World, statusCode: number) {
    const actual = this.response?.status;

    if (actual !== statusCode) {
        console.error('Expected status:', statusCode, 'but got:', actual);
        console.error('Response body:', JSON.stringify(this.response?.data, null, 2));
    }

    assert.equal(actual, statusCode);
});

Then('print the response body', function (this: World) {
    if (!this.response) {
        console.error('No response to print');
        return;
    }
    console.log('Response status:', this.response.status);
    console.log('Response headers:', JSON.stringify(this.response.headers, null, 2));
    console.log('Response body:', JSON.stringify(this.response.data, null, 2));
});

Then(
    'the validation message codes should be {string}',
    function (this: World, expected: string) {
        const data = this.response?.data as { validationMessages?: Array<{ code?: string }> } | undefined;
        const messages = data?.validationMessages ?? [];
        const actualCodes = messages.map((message) => message.code).sort();
        const expectedCodes = expected.split(',').map((code) => code.trim()).sort();

        assert.deepEqual(actualCodes, expectedCodes);
    }
);
