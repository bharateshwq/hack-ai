const vscode = require('vscode');

class MockAIService {
    constructor() {
        this.responses = {
            'explain': 'This code appears to be a function that calculates the sum of two numbers. It takes two arguments, `a` and `b`, and returns their sum.',
            'refactor': 'Here is a refactored version of the code:\n\n```javascript\nconst add = (a, b) => a + b;\n```',
            'bugs': 'I did not find any obvious bugs in this code snippet.',
            'readability': 'The code is already quite readable. You could add JSDoc comments to improve it further.',
            'default': 'I am a mock AI. I can help you explain, refactor, or find bugs in your code.'
        };
    }

    async getResponse(input, context) {
        // Simulate network delay
        await new Promise(resolve => setTimeout(resolve, 1000));

        const lowerInput = input.toLowerCase();
        if (lowerInput.includes('explain')) return this.responses['explain'];
        if (lowerInput.includes('refactor')) return this.responses['refactor'];
        if (lowerInput.includes('bug')) return this.responses['bugs'];
        if (lowerInput.includes('readability')) return this.responses['readability'];
        if (lowerInput.includes('generate_docs')) {
            if (context && context.length > 0) {
                const snippet = context[0]; // Just use the first snippet for now
                return `Here is the documentation for the selected code:\n\n/**\n * Generated documentation for ${snippet.file}\n * \n * @description\n * This function appears to be important logic.\n * \n * @param {any} args - The arguments passed to the function.\n * @returns {any} - The return value.\n */`;
            }
            return 'Please select some code to generate documentation for.';
        }

        return `I received your message: "${input}". Context: ${context ? 'Yes' : 'No'}. ${this.responses['default']}`;
    }
}

module.exports = MockAIService;
