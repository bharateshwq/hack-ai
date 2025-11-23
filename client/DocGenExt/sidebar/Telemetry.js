const trackEvent = (name, data) => {
    console.log(`[Telemetry] Event: ${name}`, data);
};

const trackSnippetCaptured = (snippet) => {
    trackEvent('SnippetCaptured', { length: snippet.text.length, language: snippet.language });
};

const trackChatMessageSent = (message) => {
    trackEvent('ChatMessageSent', { length: message.length });
};

const trackActionTriggered = (action) => {
    trackEvent('ActionTriggered', { action });
};

module.exports = {
    trackEvent,
    trackSnippetCaptured,
    trackChatMessageSent,
    trackActionTriggered
};
