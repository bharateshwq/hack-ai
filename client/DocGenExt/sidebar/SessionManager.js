class SessionManager {
    constructor(context) {
        this.context = context;
        this.snippets = [];
        this.chatHistory = [];
    }

    addSnippet(snippet) {
        // Check for duplicates based on file and text
        console.log('test',snippet)
        const isDuplicate = this.snippets.some(s =>
            s.file === snippet.file && s.text === snippet.text
        );

        if (!isDuplicate) {
            this.snippets.unshift(snippet);
            return true;
        }
        return false;
    }

    removeSnippet(snippet) {
        this.snippets = this.snippets.filter(s => s.timestamp !== snippet.timestamp);
    }

    getSnippets() {
        return this.snippets;
    }

    addChatMessage(message) {
        this.chatHistory.push(message);
        // Persist to globalState if needed
        this.context.globalState.update('chatHistory', this.chatHistory);
    }

    getChatHistory() {
        return this.chatHistory || this.context.globalState.get('chatHistory', []);
    }

    clearSession() {
        this.snippets = [];
        this.chatHistory = [];
        this.context.globalState.update('chatHistory', []);
    }
}

module.exports = SessionManager;
