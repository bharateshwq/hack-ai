const vscode = require('vscode');
const USERID = vscode.env.machineId;

const GENERATE_DOCS_URL = 'http://192.168.83.41:8080/api/chat/1/docs';

const GENERATE_DOCS_WITHOUT_SELECTION_URL = 'http://192.168.83.41:8080/api/chat/1/docs';

const CONVERSATIONAL_API_URL = 'http://192.168.83.41:8080/api/chat/1/conversational';

module.exports = {
    USERID,
    GENERATE_DOCS_URL,
    GENERATE_DOCS_WITHOUT_SELECTION_URL,
    CONVERSATIONAL_API_URL
};